package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemAxe, ItemStack}

object AxeFilter extends Filter {
    private val AXEOutput = Paths.get(Dumper.mod_ID, "axes.txt")
    private val AXE_PATTERN = Pattern.compile(".*axe", Pattern.CASE_INSENSITIVE)
    private val AXE_PATTERN2 = Pattern.compile(".*_axe")
    private val axeBuilder = Seq.newBuilder[String]
    private val axeShortBuilder = Seq.newBuilder[String]

    def accept(item: Item, displayName: String, uniqueName: String) =
        item.isInstanceOf[ItemAxe] ||
          AXE_PATTERN.matcher(item.getUnlocalizedName).matches ||
          AXE_PATTERN.matcher(displayName).matches ||
          AXE_PATTERN2.matcher(uniqueName).find

    override def addtoList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean = {
        if (accept(stack.getItem, displayName, uniqueName)) {
            axeBuilder += (shortName + " : " + stack.getDestroySpeed(Blocks.LOG.getDefaultState))
            axeShortBuilder += uniqueName
            true
        } else
            false
    }

    override def writeToFile() = {
        val axeShort = axeShortBuilder.result()
        val s: Seq[String] = Seq("Axe") ++
          axeBuilder.result() ++
          Seq("", "") ++
          axeShort ++
          Seq("", "", axeShort.reduce((s1, s2) => s1 + ", " + s2), axeShort.size.toString)
        import scala.collection.JavaConverters._
        Files.write(AXEOutput, s.asJava)
    }
}
