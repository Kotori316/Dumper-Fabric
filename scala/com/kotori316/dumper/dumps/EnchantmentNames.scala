package com.kotori316.dumper.dumps

import net.minecraft.client.resources.I18n
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.collection.JavaConverters._

object EnchantmentNames extends Dumps {
  override val configName: String = "OutputEnchantments"
  override val fileName: String = "enchantment"

  override def content(): Seq[String] = {
    val data = ForgeRegistries.ENCHANTMENTS.getEntries.asScala.toSeq.map(new EData(_)).sorted
    val mn: Int = data.map(_.e.getName.length).max
    val mi: Int = data.map(_.name.toString.length).max
    //       index   id    name  Resource maxLevel rarity treasure
    val f = s"%3d : %3d : %-${mn}s : %-${mi}s : %d : %s : %b"
    "number : ID : name : mod : MaxLevel : Rarity : Treasure" +: data.zipWithIndex
      .map { case (e, i) => f.format(i, e.id, I18n.format(e.e.getName), e.name, e.e.getMaxLevel, e.e.getRarity, e.e.isTreasureEnchantment) }
  }

  case class EData(name: ResourceLocation, e: Enchantment) extends Ordered[EData] {
    def this(e: java.util.Map.Entry[ResourceLocation, Enchantment]) = {
      this(e.getKey, e.getValue)
    }

    val id = Enchantment.getEnchantmentID(e)

    override def compare(that: EData): Int = this.id compareTo that.id
  }

}
