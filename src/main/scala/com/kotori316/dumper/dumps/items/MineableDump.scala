package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.{BlockTags, Tag}
import net.minecraft.world.level.block.Block

import scala.jdk.CollectionConverters._

object MineableDump extends Dumps[Tag[_]] {
  override val configName: String = "OutputMineable"
  override val fileName: String = "mineable"
  private final val tags = Seq(
    BlockTags.MINEABLE_WITH_AXE,
    BlockTags.MINEABLE_WITH_HOE,
    BlockTags.MINEABLE_WITH_PICKAXE,
    BlockTags.MINEABLE_WITH_SHOVEL,
  )
  final val formatter = new Formatter[Entry](
    Seq("-name"),
    Seq(_.registryName)
  )

  override def content(filters: Seq[Filter[Tag[_]]], server: MinecraftServer): Seq[String] = {
    for {
      tag <- tags
      entries = tag.getValues.asScala.map(Entry)
      string <- s"# ${tag.getName}" +: formatter.format(entries) :+ System.lineSeparator()
    } yield string
  }

  case class Entry(block: Block) {
    def registryName: ResourceLocation = block.getRegistryName
  }
}
