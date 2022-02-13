package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import com.kotori316.dumper.dumps.Filter
import net.minecraft.core.Registry
import net.minecraft.tags.{BlockTags, ItemTags}
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

import scala.collection.mutable
import scala.jdk.CollectionConverters._

trait SFilter extends Filter[Block] {

  private[this] final val short = mutable.Buffer.empty[String]
  private[this] final val unique = mutable.Buffer.empty[String]

  val out: Path

  def accept(block: Block): Boolean

  override final def addToList(v: Block): Boolean = {
    if (accept(v)) {
      short += v.getName.getString + BlocksDump.oreName(new ItemStack(v))
      unique += Registry.BLOCK.getKey(v).toString
      true
    } else false
  }

  override final def writeToFile(): Unit = {
    val s = short ++ Seq("", "") ++ unique.distinct ++ Seq(unique.distinct.reduceOption((s1, s2) => s1 + ", " + s2).getOrElse(""), short.size.toString)
    Files.write(out, s.asJava)
  }
}

class OreFilter extends SFilter {
  private[this] final val oreDicPattern = Pattern.compile("(forge:ores)|(.*:ores/.*)|(.*_ores.*)")
  override val out: Path = Paths.get(Dumper.modID, "ore.txt")

  override def accept(block: Block): Boolean = {
    val oreName = BlocksDump.oreNameSeq(block)
    oreName.exists(n => oreDicPattern.matcher(n.toString).matches())
  }
}

class WoodFilter extends SFilter {
  private[this] final val woodPATTERN = Pattern.compile(".* Wood")
  private[this] final val woodDicPATTERN = Pattern.compile("^(.*:logs)|(wood[A-Z].*)")

  override val out: Path = Paths.get(Dumper.modID, "wood.txt")

  override def accept(block: Block): Boolean = {
    BlockTags.LOGS.contains(block) ||
      ItemTags.LOGS.contains(block.asItem()) ||
      woodPATTERN.matcher(block.getName.getString).matches ||
      BlocksDump.oreNameSeq(block).exists(n => woodDicPATTERN.matcher(n.toString).matches)
  }
}

class LeaveFilter extends SFilter {
  override val out: Path = Paths.get(Dumper.modID, "leave.txt")

  override def accept(block: Block): Boolean = BlockTags.LEAVES.contains(block) || ItemTags.LEAVES.contains(block.asItem())
}
