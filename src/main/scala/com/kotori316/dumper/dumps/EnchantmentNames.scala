package com.kotori316.dumper.dumps

import net.minecraft.client.resources.I18n
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry
import net.minecraftforge.registries.ForgeRegistries

import scala.collection.JavaConverters._

object EnchantmentNames extends Dumps[Enchantment] {
  override val configName: String = "OutputEnchantments"
  override val fileName: String = "enchantment"

  override def content(filters: Seq[Filter[Enchantment]]):Seq[String] = {
    implicit val vanillaRegistry: Registry[Enchantment] = ForgeRegistries.ENCHANTMENTS.getSlaveMap(WRAPPER_ID, classOf[Registry[Enchantment]])
    val data = ForgeRegistries.ENCHANTMENTS.getEntries.asScala.toSeq.map(EData.apply).sorted
    val mn: Int = data.map(_.translatedName.length).max
    val mi: Int = data.map(_.name.toString.length).max
    //       index   id    name  Resource maxLevel rarity treasure
    val f = s"%3d : %3d : %-${mn}s : %-${mi}s : %d : %s : %b"
    "number : ID : name : mod : MaxLevel : Rarity : Treasure" +: data.zipWithIndex
      .map { case (e, i) => f.format(i, e.id, e.translatedName, e.name, e.e.getMaxLevel, e.e.getRarity, e.e.isTreasureEnchantment) }
  }

  case class EData(name: ResourceLocation, e: Enchantment, id: Int) extends Ordered[EData] {
    override def compare(that: EData): Int = this.id compare that.id

    def translatedName = I18n.format(e.getName)
  }

  object EData {
    def apply(e: java.util.Map.Entry[ResourceLocation, Enchantment])(implicit r: Registry[Enchantment]): EData = {
      new EData(e.getKey, e.getValue, r.getId(e.getValue))
    }
  }

}
