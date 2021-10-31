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

  override def content(filters: Seq[Filter[Tag[_]]], server: MinecraftServer): Seq[String] = {
    import scala.jdk.CollectionConverters._
    tagToMessage(ItemTags.getAllTags, "Items") ++
      tagToMessage(BlockTags.getAllTags, "Blocks") ++
      tagToMessage(FluidTags.getAllTags, "Fluids") ++
      tagToMessage(EntityTypeTags.getAllTags, "Entities") ++
      SerializationTags.getInstance.collections.asScala.flatMap { case (name, c) => tagToMessage(c, name.toString) }
  }

  def tagToMessage(collection: TagCollection[_], name: String): Seq[String] = {
    val map: Map[ResourceLocation, Tag[_]] = CollectionConverters.asScala(collection.getAllTags).toMap
    val tagSeq: Seq[TagData] = map.map { case (location, value) => TagData(location.toString, CollectionConverters.asScala(value.getValues).toSeq) }.toSeq
      .sortBy(_.name)
    ("-" * 10 + name + "-" * 10) +: formatter.format(tagSeq) :+ "\n"
  }

  case class TagData(name: String, content: Seq[_]) {
    def contentRegistryNames(): Seq[String] =
      content.collect {
        case e: IForgeRegistryEntry[_] => e.getRegistryName.toString
        case o => o.toString
      }
  }

}
