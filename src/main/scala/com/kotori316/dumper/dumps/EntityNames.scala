package com.kotori316.dumper.dumps

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.{EntityType, MobCategory}

import scala.jdk.CollectionConverters._

object EntityNames extends FastDumps[EntityType[_]] {
  override def content(filters: Seq[Filter[EntityType[_]]]): Seq[String] = {
    val value = Registry.ENTITY_TYPE.iterator().asScala.toSeq.map(e => EntityData(Registry.ENTITY_TYPE.getKey(e), e))
      .sorted
    formatter.format(value)
  }

  override val configName: String = "OutputEntities"
  override val fileName: String = "entities"
  final val formatter: Formatter[EntityData] = new Formatter[EntityData](
    "-Name" :: "-RegistryName" :: "summon able" :: "immuneToFire" :: "-Classification" :: "-Size" :: Nil,
    Seq(_.name, _.location, _.isSummonable, _.isImmuneToFire, _.getClassification, _.getSize)
  )

  final case class EntityData(location: ResourceLocation, t: EntityType[_]) {
    def name: String = t.getDescription.getString

    //noinspection SpellCheckingInspection
    def isSummonable: Boolean = t.canSummon

    def isImmuneToFire: Boolean = t.fireImmune()

    def getClassification: MobCategory = t.getCategory

    def getSize: String = {
      val dim = t.getDimensions
      s"w=${dim.width}, h=${dim.height}, f=${dim.fixed}"
    }
  }

  final implicit val pairOrder: Ordering[EntityData] =
    Ordering.by((t: EntityData) => t.location.getNamespace) orElseBy (_.location.getPath)
}
