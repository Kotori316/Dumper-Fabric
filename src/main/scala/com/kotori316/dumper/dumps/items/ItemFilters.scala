package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import com.kotori316.dumper.dumps.Filter
import net.minecraft.block.Blocks
import net.minecraft.entity.ai.attributes.Attributes
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.item._

import scala.jdk.CollectionConverters._

class PickaxeFilter extends Filter[ItemData] {
  private[this] final val PICKAXEOutput = Paths.get(Dumper.modID, "pickaxes.txt")
  private[this] final val PICKAXE_PATTERN = Pattern.compile(".*pickaxe", Pattern.CASE_INSENSITIVE)
  private[this] final val PICKAXE_PATTERN2 = Pattern.compile(".*_(pickaxe|pick_)")
  private[this] final val pickaxeBuilder = Seq.newBuilder[String]
  private[this] final val pickaxeShortBuilder = Seq.newBuilder[String]

  def accept(item: Item, displayName: String, uniqueName: String): Boolean =
    item.isInstanceOf[PickaxeItem] ||
      PICKAXE_PATTERN.matcher(item.getTranslationKey).matches ||
      PICKAXE_PATTERN.matcher(displayName).matches ||
      PICKAXE_PATTERN2.matcher(uniqueName).find

  override def addToList(v: ItemData): Boolean = {
    val uniqueName = v.item.getRegistryName.toString
    if (accept(v.item, v.displayName, uniqueName)) {
      pickaxeBuilder += (v.displayName + " : " + v.stack.getMaxDamage + " : " + v.stack.getDestroySpeed(Blocks.STONE.getDefaultState))
      pickaxeShortBuilder += uniqueName
      true
    } else
      false
  }

  override def writeToFile(): Unit = {
    val pickaxeShort = pickaxeShortBuilder.result()
    val s: Seq[String] = Seq("Pickaxe") ++
      pickaxeBuilder.result() ++
      Seq("", "") ++
      pickaxeShort ++
      Seq("", "", pickaxeShort.reduce((s1, s2) => s1 + ", " + s2), pickaxeShort.size.toString)
    Files.write(PICKAXEOutput, s.asJava)
  }
}

class SwordFilter extends Filter[ItemData] {
  private[this] final val SWORD_PATTERN = Pattern.compile(".*(sword)", Pattern.CASE_INSENSITIVE)
  private[this] final val SWORD_PATTERN2 = Pattern.compile(".*_sword", Pattern.CASE_INSENSITIVE)
  private[this] final val SWORDOutput = Paths.get(Dumper.modID, "swords.txt")
  private[this] final val SWORDBuilder = Seq.newBuilder[String]
  private[this] final val SWORDShortBuilder = Seq.newBuilder[String]

  def accept(item: Item, displayName: String, uniqueName: String): Boolean =
    item.isInstanceOf[SwordItem] ||
      SWORD_PATTERN.matcher(item.getTranslationKey).matches ||
      SWORD_PATTERN.matcher(displayName).matches ||
      SWORD_PATTERN2.matcher(uniqueName).find

  override def addToList(v: ItemData): Boolean = {
    val uniqueName = v.item.getRegistryName.toString
    if (accept(v.item, v.displayName, uniqueName)) {
      val valueMap = v.item.getAttributeModifiers(EquipmentSlotType.MAINHAND, v.stack)
      val damage = valueMap.get(Attributes.ATTACK_DAMAGE).asScala.map(_.getAmount)
      val speed = valueMap.get(Attributes.ATTACK_SPEED).asScala.map(f => "%.1f".format(f.getAmount * -1))
      SWORDBuilder += (v.displayName + " : " + v.stack.getMaxDamage + " : " + damage.mkString(", ") + " : " + speed.mkString(", "))
      SWORDShortBuilder += uniqueName
      true
    } else
      false
  }

  override def writeToFile(): Unit = {
    val SWORDShort = SWORDShortBuilder.result()
    val s: Seq[String] = Seq("Sword", "Name : Damage : ATTACK_DAMAGE : ATTACK_SPEED") ++
      SWORDBuilder.result() ++
      Seq("", "") ++
      SWORDShort ++
      Seq("", "", SWORDShort.reduce((s1, s2) => s1 + ", " + s2), SWORDShort.size.toString)
    Files.write(SWORDOutput, s.asJava)
  }
}

class ShovelFilter extends Filter[ItemData] {
  private[this] final val SHOVELOutput = Paths.get(Dumper.modID, "shovels.txt")
  private[this] final val SHOVEL_PATTERN = Pattern.compile(".*(shovel|spade)", Pattern.CASE_INSENSITIVE)
  private[this] final val SHOVEL_PATTERN2 = Pattern.compile(".*_(shovel|spade)")
  private[this] final val SHOVELBuilder = Seq.newBuilder[String]
  private[this] final val SHOVELShortBuilder = Seq.newBuilder[String]

  def accept(item: Item, displayName: String, uniqueName: String): Boolean =
    item.isInstanceOf[ShovelItem] ||
      SHOVEL_PATTERN.matcher(item.getTranslationKey).matches ||
      SHOVEL_PATTERN.matcher(displayName).matches ||
      SHOVEL_PATTERN2.matcher(uniqueName).find

  override def addToList(v: ItemData): Boolean = {
    val uniqueName = v.item.getRegistryName.toString
    if (accept(v.item, v.displayName, uniqueName)) {
      SHOVELBuilder += (v.displayName + " : " + v.stack.getMaxDamage + " : " + v.stack.getDestroySpeed(Blocks.GRASS_BLOCK.getDefaultState))
      SHOVELShortBuilder += uniqueName
      true
    } else
      false
  }

  override def writeToFile(): Unit = {
    val SHOVELShort = SHOVELShortBuilder.result()
    val s: Seq[String] = Seq("Shovel") ++
      SHOVELBuilder.result() ++
      Seq("", "") ++
      SHOVELShort ++
      Seq("", "", SHOVELShort.reduce((s1, s2) => s1 + ", " + s2), SHOVELShort.size.toString)
    Files.write(SHOVELOutput, s.asJava)
  }
}

class AxeFilter extends Filter[ItemData] {
  private[this] final val AXEOutput = Paths.get(Dumper.modID, "axes.txt")
  private[this] final val AXE_PATTERN = Pattern.compile(".*axe", Pattern.CASE_INSENSITIVE)
  private[this] final val AXE_PATTERN2 = Pattern.compile(".*_axe")
  private[this] final val axeBuilder = Seq.newBuilder[String]
  private[this] final val axeShortBuilder = Seq.newBuilder[String]

  def accept(item: Item, displayName: String, uniqueName: String): Boolean =
    item.isInstanceOf[AxeItem] ||
      AXE_PATTERN.matcher(item.getTranslationKey).matches ||
      AXE_PATTERN.matcher(displayName).matches ||
      AXE_PATTERN2.matcher(uniqueName).find

  override def addToList(v: ItemData): Boolean = {
    val uniqueName = v.item.getRegistryName.toString
    if (accept(v.item, v.displayName, uniqueName)) {
      axeBuilder += (v.displayName + " : " + v.stack.getMaxDamage + " : " + v.stack.getDestroySpeed(Blocks.OAK_LOG.getDefaultState))
      axeShortBuilder += uniqueName
      true
    } else
      false
  }

  override def writeToFile(): Unit = {
    val axeShort = axeShortBuilder.result()
    val s: Seq[String] = Seq("Axe") ++
      axeBuilder.result() ++
      Seq("", "") ++
      axeShort ++
      Seq("", "", axeShort.reduce((s1, s2) => s1 + ", " + s2), axeShort.size.toString)
    import scala.jdk.CollectionConverters._
    Files.write(AXEOutput, s.asJava)
  }
}
