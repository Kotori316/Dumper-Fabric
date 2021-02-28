package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.block.Block
import net.minecraft.item.{BlockItem, ItemStack}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraft.world.EmptyBlockReader
import net.minecraftforge.registries.ForgeRegistries

import scala.jdk.CollectionConverters._
import scala.util.chaining._

object BlocksDump extends Dumps[Block] {
  override val configName: String = "outputBlocks"
  override val fileName: String = "blocksOutput"

  override def getFilters: Seq[SFilter] = Seq(new OreFilter, new WoodFilter, new LeaveFilter)

  private final val formatter = new Formatter[Data](
    Seq("-Name", "-RegistryName", "Hardness", "Item Class", "-Properties", "-Tag"),
    Seq(_.name, _.registryName, _.block.getDefaultState.getBlockHardness(EmptyBlockReader.INSTANCE, BlockPos.ZERO), _.itemClass, _.properties, _.tags)
  )

  override def content(filters: Seq[Filter[Block]], server: MinecraftServer): Seq[String] = {
    ForgeRegistries.BLOCKS.forEach(b => filters.foreach(_.addToList(b)))
    val blockList = for {
      block <- ForgeRegistries.BLOCKS.asScala
      stack <- NonNullList.create[ItemStack]().tap(i => block.fillItemGroup(block.asItem().getGroup, i)).asScala
    } yield Data(block, stack)
    formatter.format(blockList.toSeq)
  }

  def oreNameSeq(block: Block): Iterable[ResourceLocation] = {
    block.getTags.asScala
  }

  private case class Data(block: Block, stack: ItemStack) {
    def name: String = if (stack.isEmpty) {
      new TranslationTextComponent(block.getTranslationKey).getString
    } else {
      stack.getDisplayName.getString
    }

    def registryName: ResourceLocation = block.getRegistryName

    def itemClass: String = stack.getItem.getClass match {
      case c if c == classOf[BlockItem] => ""
      case c => c.getName
    }

    def tags: String = block.getTags.asScala.toSeq.sortBy(_.toString).mkString(", ")

    def properties: String = block.getStateContainer.getProperties.asScala.map(_.getName).mkString(", ")
  }

}
