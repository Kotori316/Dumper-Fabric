package com.kotori316.dumper.dumps

import net.minecraft.entity.{EntityClassification, EntitySize, EntityType}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

import scala.jdk.CollectionConverters._

object EntityNames extends FastDumps[EntityType[_]] {
  override def content(filters: Seq[Filter[EntityType[_]]]): Seq[String] = {
    val value = ForgeRegistries.ENTITIES.getEntries.asScala.toSeq.map(e => EntityData(e.getKey.getLocation, e.getValue))
      .sorted
    formatter.format(value)
  }

  override val configName: String = "OutputEntities"
  override val fileName: String = "entities"
  final val formatter: Formatter[EntityData] = new Formatter[EntityData](
    "-Name" :: "-RegistryName" :: "summon able" :: "immuneToFire" :: "Classification" :: "Size" :: Nil,
    Seq(_.name, _.location, _.isSummonable, _.isImmuneToFire, _.getClassification, _.getSize)
  )

  final case class EntityData(location: ResourceLocation, t: EntityType[_]) {
    def name: String = t.getName.getString

    //noinspection SpellCheckingInspection
    def isSummonable: Boolean = t.isSummonable

    def isImmuneToFire: Boolean = t.isImmuneToFire

    def getClassification: EntityClassification = t.getClassification

    def getSize: EntitySize = t.getSize
  }

  final implicit val pairOrder: Ordering[EntityData] =
    Ordering.by((t: EntityData) => t.location.getNamespace) orElseBy (_.location.getPath)
}
