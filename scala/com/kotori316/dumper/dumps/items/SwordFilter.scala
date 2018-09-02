package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.{Item, ItemStack, ItemSword}

import scala.collection.JavaConverters._

object SwordFilter extends Filter {
    private val SWORD_PATTERN = Pattern.compile(".*(sword)", Pattern.CASE_INSENSITIVE)
    private val SWORD_PATTERN2 = Pattern.compile(".*_sword", Pattern.CASE_INSENSITIVE)
    private val SWORDOutput = Paths.get(Dumper.mod_ID, "swords.txt")
    private val SWORDBuilder = Seq.newBuilder[String]
    private val SWORDShortBuilder = Seq.newBuilder[String]

    def accept(item: Item, displayName: String, uniqueName: String) =
        item.isInstanceOf[ItemSword] ||
          SWORD_PATTERN.matcher(item.getUnlocalizedName).matches ||
          SWORD_PATTERN.matcher(displayName).matches ||
          SWORD_PATTERN2.matcher(uniqueName).find

    override def addtoList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean = {
        if (accept(stack.getItem, displayName, uniqueName)) {
            val valueMap = stack.getItem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack)
            val damage = valueMap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName).asScala.map(_.getAmount)
            val speed = valueMap.get(SharedMonsterAttributes.ATTACK_SPEED.getName).asScala.map(f => "%.1f".format(f.getAmount * -1))
            SWORDBuilder += (shortName + " : " + damage.mkString(", ") + " : " + speed.mkString(", "))
            SWORDShortBuilder += uniqueName
            true
        } else
            false
    }

    override def writeToFile() = {
        val SWORDShort = SWORDShortBuilder.result()
        val s: Seq[String] = Seq("Sword", "    ID: Damege: Name : ATTACK_DAMAGE : ATTACK_SPEED") ++
          SWORDBuilder.result() ++
          Seq("", "") ++
          SWORDShort ++
          Seq("", "", SWORDShort.reduce((s1, s2) => s1 + ", " + s2), SWORDShort.size.toString)
        Files.write(SWORDOutput, s.asJava)
    }
}
