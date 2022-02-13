package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.server.MinecraftServer
import net.minecraft.tags._

import scala.jdk.javaapi.CollectionConverters

object TagDump extends Dumps[Tag[_]] {
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
  ).map(_.getName)

  override def content(filters: Seq[Filter[Tag[_]]], server: MinecraftServer): Seq[String] = {
    val className = FabricLoader.getInstance().getMappingResolver.unmapClassName("intermediary", classOf[TagContainer].getName)
    val fieldName = FabricLoader.getInstance().getMappingResolver.mapFieldName("intermediary", className, "field_28306",
      s"L${classOf[java.util.Map[_, _]].getName.replace('.', '/')};")
    val field = classOf[TagContainer].getDeclaredField(fieldName)
    field.setAccessible(true)
    import scala.jdk.CollectionConverters._
    val collections = field.get(SerializationTags.getInstance).asInstanceOf[java.util.Map[ResourceKey[_ <: Registry[_]], TagCollection[_]]].asScala

    collections.toSeq.sortBy(_._1.location).flatMap { case (name, c) =>
      tagToMessage(c, name.location.toString, Registry.REGISTRY.get(name.location()))
    }
  }

  def tagToMessage(collection: TagCollection[_], name: String, registry: Registry[_]): Seq[String] = {
    val map: Map[ResourceLocation, Tag[_]] = CollectionConverters.asScala(collection.getAllTags).filterNot(t => ignoreTags(t._1)).toMap

    val tagSeq: Seq[TagData] = map.map { case (location, value) =>
      val locations = CollectionConverters.asScala(value.getValues).toSeq.map(v => registry.asInstanceOf[Registry[Any]].getKey(v))
      TagData(location.toString, locations)
    }.toSeq.sortBy(_.name)
    ("# " + name) +: formatter.format(tagSeq) :+ "\n"
  }

  case class TagData(name: String, content: Seq[ResourceLocation])

}
