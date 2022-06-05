package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags._

import scala.jdk.StreamConverters._

object TagDump extends Dumps[TagKey[_]] {
  override val configName = "OutputTagNames"
  override val fileName = "tags"
  final val formatter = new Formatter[TagData](
    Seq("-name", "count", "-content"),
    Seq(_.name, _.content.size, _.content.mkString(", "))
  )
  private final val ignoreTags: Set[ResourceLocation] = Set(
    BlockTags.MINEABLE_WITH_AXE,
    BlockTags.MINEABLE_WITH_HOE,
    BlockTags.MINEABLE_WITH_PICKAXE,
    BlockTags.MINEABLE_WITH_SHOVEL,
    BlockTags.WALL_POST_OVERRIDE,
  ).map(_.location)

  override def content(filters: Seq[Filter[TagKey[_]]], server: MinecraftServer): Seq[String] = {
    import scala.jdk.CollectionConverters._
    Registry.REGISTRY.asScala.map(r => r.key() -> r).toSeq.sortBy(_._1.location).flatMap { case (name, c) =>
      tagToMessage[Any](c.asInstanceOf[Registry[Any]], name.location.toString)
    }
  }

  def tagToMessage[T](collection: Registry[T], name: String): Seq[String] = {
    val map: Map[ResourceLocation, Seq[T]] = collection.getTags.toScala(Seq)
      .filterNot(p => ignoreTags(p.getFirst.location))
      .map(p =>
        p.getFirst.location -> p.getSecond.stream().map(_.value()).toScala(Seq)
      ).toMap
    val tagSeq: Seq[TagData] = map.map { case (location, values) => TagData(location.toString, values.map(v => collection.getKey(v))) }.toSeq
      .sortBy(_.name)
    if (tagSeq.isEmpty) {
      Seq.empty
    } else {
      ("# " + name) +: formatter.format(tagSeq) :+ "\n"
    }
  }

  case class TagData(name: String, content: Seq[ResourceLocation])

}
