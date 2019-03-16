package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import net.minecraft.init.Blocks
import net.minecraft.item.{Item, ItemStack}

import scala.collection.JavaConverters._
import scala.collection.mutable

trait SFilter extends Filter {

  private[this] final val short = mutable.Buffer.empty[String]
  private[this] final val unique = mutable.Buffer.empty[String]

  val out: Path

  def accept(stack: ItemStack): Boolean

  override final def addToList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean = {
    if (accept(stack)) {
      short += shortName
      unique += uniqueName
      true
    } else false
  }

  override final def writeToFile(): Unit = {
    val s = short ++ Seq("", "") ++ unique.distinct ++ Seq(unique.distinct.reduce((s1, s2) => s1 + ", " + s2), short.size.toString)
    Files.write(out, s.asJava)
  }
}

object OreFilter extends SFilter {
  private[this] final val oreDicPattern = Pattern.compile("^ore[A-Z].*")
  override val out: Path = Paths.get(Dumper.mod_ID, "ore.txt")

  override def accept(stack: ItemStack): Boolean = {
    val oreName = BlocksDump.oreNameSeq(stack)
    oreName.nonEmpty && oreName.exists(n => oreDicPattern.matcher(n).matches())
  }
}

object WoodFilter extends SFilter {
  private[this] final val woodPATTERN = Pattern.compile(".* Wood")
  private[this] final val woodDicPATTERN = Pattern.compile("^wood[A-Z].*")

  override val out: Path = Paths.get(Dumper.mod_ID, "wood.txt")

  override def accept(stack: ItemStack): Boolean = {
    if ((stack.getItem eq Item.getItemFromBlock(Blocks.LOG)) || (stack.getItem eq Item.getItemFromBlock(Blocks.LOG2)))
      return true
    val s = stack.getItem.getItemStackDisplayName(stack)
    if (woodPATTERN.matcher(s).matches)
      return true
    val oreName = BlocksDump.oreNameSeq(stack)
    oreName.exists(woodDicPATTERN.matcher(_).matches)
  }
}

object LeaveFilter extends SFilter {
  override val out: Path = Paths.get(Dumper.mod_ID, "leave.txt")

  override def accept(stack: ItemStack): Boolean = BlocksDump.oreNameSeq(stack).contains("treeLeaves")
}
