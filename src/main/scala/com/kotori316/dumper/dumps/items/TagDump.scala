package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags._
import net.minecraftforge.registries.IForgeRegistryEntry

import scala.jdk.javaapi.CollectionConverters

object TagDump extends Dumps[Tag[_]] {
  override val configName = "OutputTagNames"
  override val fileName = "tags"
  final val formatter = new Formatter[TagData](
    Seq("-name", "count", "-content"),
    Seq(_.name, _.content.size, _.contentRegistryNames().mkString(", "))
  )
  private final val ignoreTags: Set[ResourceLocation] = Set(
    BlockTags.MINEABLE_WITH_AXE,
    BlockTags.MINEABLE_WITH_HOE,
    BlockTags.MINEABLE_WITH_PICKAXE,
    BlockTags.MINEABLE_WITH_SHOVEL,
  ).map(_.getName)

  override def content(filters: Seq[Filter[Tag[_]]], server: MinecraftServer): Seq[String] = {
    import scala.jdk.CollectionConverters._
    SerializationTags.getInstance.collections.asScala.toSeq.sortBy(_._1.location).flatMap { case (name, c) =>
      tagToMessage(c, name.location.toString)
    }
  }

  def tagToMessage(collection: TagCollection[_], name: String): Seq[String] = {
    val map: Map[ResourceLocation, Tag[_]] = CollectionConverters.asScala(collection.getAllTags).filterNot(t => ignoreTags(t._1)).toMap
    val tagSeq: Seq[TagData] = map.map { case (location, value) => TagData(location.toString, CollectionConverters.asScala(value.getValues).toSeq) }.toSeq
      .sortBy(_.name)
    ("# " + name) +: formatter.format(tagSeq) :+ "\n"
  }

  case class TagData(name: String, content: Seq[_]) {
    def contentRegistryNames(): Seq[String] =
      content.collect {
        case e: IForgeRegistryEntry[_] => e.getRegistryName.toString
        case o => o.toString
      }
  }

}
