package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.core.{BlockPos, NonNullList, Registry}
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.SerializationTags
import net.minecraft.world.item.{BlockItem, ItemStack}
import net.minecraft.world.level.EmptyBlockGetter
import net.minecraft.world.level.block.Block

import scala.jdk.CollectionConverters._
import scala.util.chaining._

object BlocksDump extends Dumps[Block] {
  override val configName: String = "outputBlocks"
  override val fileName: String = "blocksOutput"

  override def getFilters: Seq[SFilter] = Seq(new OreFilter, new WoodFilter, new LeaveFilter)

  private final val formatter = new Formatter[Data](
    Seq("-Name", "-RegistryName", "Hardness", "-Item Class", "-Properties", "-Tag"),
    Seq(_.name, _.registryName, _.hardness, _.itemClass, _.properties, _.tags)
  )

  override def content(filters: Seq[Filter[Block]], server: MinecraftServer): Seq[String] = {
    Registry.BLOCK.forEach(b => filters.foreach(_.addToList(b)))
    val blockList = for {
      block <- Registry.BLOCK.asScala
      stack <- NonNullList.create[ItemStack]().tap(i => block.fillItemCategory(block.asItem().getItemCategory, i)).asScala
    } yield Data(block, stack)
    formatter.format(blockList.toSeq)
  }

  def oreNameSeq(block: Block): Iterable[ResourceLocation] = {
    SerializationTags.getInstance().getOrEmpty(Registry.BLOCK_REGISTRY)
      .getMatchingTags(block)
      .asScala
  }

  private case class Data(block: Block, stack: ItemStack) {
    def name: String = if (stack.isEmpty) {
      block.getName.getString
    } else {
      stack.getHoverName.getString
    }

    def registryName: ResourceLocation = Registry.BLOCK.getKey(block)

    def itemClass: String = stack.getItem.getClass match {
      case c if c == classOf[BlockItem] => ""
      case c => c.getName.replace("net.minecraft.world.item.", "")
    }

    def tags: String = oreNameSeq(block).toSeq.sortBy(_.toString).mkString(", ")

    def properties: String = block.getStateDefinition.getProperties.asScala.map(_.getName).mkString(", ")

    def hardness: Float = block.defaultBlockState().getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)
  }

}
