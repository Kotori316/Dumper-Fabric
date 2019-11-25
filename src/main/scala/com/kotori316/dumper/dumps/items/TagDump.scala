package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.tags._
import net.minecraftforge.registries.IForgeRegistryEntry

import scala.jdk.javaapi.CollectionConverters


object TagDump extends Dumps[Tag[_]] {
  override val configName = "OutputTagNames"
  override val fileName = "tags"
  final val formatter = new Formatter[TagData](
    Seq("name", "-count", "content"),
    Seq(_.name, _.content.size, _.contentRegistryNames().mkString(", "))
  )

  override def content(filters: Seq[Filter[Tag[_]]]): Seq[String] = {
    tagToMessage(ItemTags.getCollection, "Items") ++
      tagToMessage(BlockTags.getCollection, "Blocks") ++
      tagToMessage(FluidTags.getCollection, "Fluids") ++
      tagToMessage(EntityTypeTags.getCollection, "Entities")
  }

  def tagToMessage(collection: TagCollection[_], name: String): Seq[String] = {
    val map = CollectionConverters.asScala(collection.getTagMap)
    val tagSeq = map.map { case (location, value) => TagData(location.toString, CollectionConverters.asScala(value.getAllElements).toSeq) }.toSeq
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
