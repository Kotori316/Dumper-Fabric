package com.kotori316.dumper.dumps

import net.minecraft.entity.EntityType
import net.minecraftforge.registries.ForgeRegistries

import scala.jdk.CollectionConverters._

object EntityNames extends FastDumps[EntityType[_]] {
  override def content(filters: Seq[Filter[EntityType[_]]]): Seq[String] = {
    val value = ForgeRegistries.ENTITIES.getEntries.asScala.toSeq.sortBy(_.getKey.getLocation).map(_.getValue)
    formatter.format(value)
  }

  override val configName: String = "OutputEntities"
  override val fileName: String = "entities"
  final val formatter: Formatter[EntityType[_]] = new Formatter[EntityType[_]](
    "Name" :: "-RegistryName" :: "summon able" :: "immuneToFire" :: "Classification" :: "Size" :: Nil,
    Seq(_.getName.getString, EntityType.getKey, _.isSummonable, _.isImmuneToFire, _.getClassification, _.getSize)
  )
}
