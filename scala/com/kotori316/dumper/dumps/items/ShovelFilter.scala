package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemSpade, ItemStack}

object ShovelFilter extends Filter {
    private val SHOVELOutput = Paths.get(Dumper.mod_ID, "shovels.txt")
    private val SHOVEL_PATTERN = Pattern.compile(".*(shovel|spade)", Pattern.CASE_INSENSITIVE)
    private val SHOVEL_PATTERN2 = Pattern.compile(".*_(shovel|spade)")
    private val SHOVELBuilder = Seq.newBuilder[String]
    private val SHOVELShortBuilder = Seq.newBuilder[String]

    def accept(item: Item, displayName: String, uniqueName: String) =
        item.isInstanceOf[ItemSpade] ||
          SHOVEL_PATTERN.matcher(item.getUnlocalizedName).matches ||
          SHOVEL_PATTERN.matcher(displayName).matches ||
          SHOVEL_PATTERN2.matcher(uniqueName).find

    override def addtoList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean = {
        if (accept(stack.getItem, displayName, uniqueName)) {
            SHOVELBuilder += (shortName + " : " + stack.getDestroySpeed(Blocks.GRASS.getDefaultState))
            SHOVELShortBuilder += uniqueName
            true
        } else
            false
    }

    override def writeToFile() = {
        val SHOVELShort = SHOVELShortBuilder.result()
        val s: Seq[String] = Seq("Shovel") ++
          SHOVELBuilder.result() ++
          Seq("", "") ++
          SHOVELShort ++
          Seq("", "", SHOVELShort.reduce((s1, s2) => s1 + ", " + s2), SHOVELShort.size.toString)
        import scala.collection.JavaConverters._
        Files.write(SHOVELOutput, s.asJava)
    }
}
