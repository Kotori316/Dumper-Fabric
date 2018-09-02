package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemPickaxe, ItemStack}

object PickaxeFilter extends Filter {
    private val PICKAXEOutput = Paths.get(Dumper.mod_ID, "pickaxes.txt")
    private val PICKAXE_PATTERN = Pattern.compile(".*pickaxe", Pattern.CASE_INSENSITIVE)
    private val PICKAXE_PATTERN2 = Pattern.compile(".*_(pickaxe|pick)")
    private val pickaxeBuilder = Seq.newBuilder[String]
    private val pickaxeShortBuilder = Seq.newBuilder[String]

    def accept(item: Item, displayName: String, uniqueName: String) =
        item.isInstanceOf[ItemPickaxe] ||
          PICKAXE_PATTERN.matcher(item.getUnlocalizedName).matches ||
          PICKAXE_PATTERN.matcher(displayName).matches ||
          PICKAXE_PATTERN2.matcher(uniqueName).find

    override def addtoList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean = {
        if (accept(stack.getItem, displayName, uniqueName)) {
            pickaxeBuilder += (shortName + " : " + stack.getDestroySpeed(Blocks.STONE.getDefaultState))
            pickaxeShortBuilder += uniqueName
            true
        } else
            false
    }

    override def writeToFile() = {
        val pickaxeShort = pickaxeShortBuilder.result()
        val s: Seq[String] = Seq("Pickaxe") ++
          pickaxeBuilder.result() ++
          Seq("", "") ++
          pickaxeShort ++
          Seq("", "", pickaxeShort.reduce((s1, s2) => s1 + ", " + s2), pickaxeShort.size.toString)
        import scala.collection.JavaConverters._
        Files.write(PICKAXEOutput, s.asJava)
    }
}
