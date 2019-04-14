package com.kotori316.dumper.dumps.items

import java.nio.file.{Files, Path, Paths}
import java.util.regex.Pattern

import com.kotori316.dumper.Dumper
import net.minecraft.block.Block
import net.minecraft.tags.{BlockTags, ItemTags}
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor

import scala.collection.JavaConverters._
import scala.collection.mutable

trait SFilter extends Filter[Block] {

  private[this] final val short = mutable.Buffer.empty[String]
  private[this] final val unique = mutable.Buffer.empty[String]

  val out: Path

  def accept(block: Block): Boolean

  override final def addToList(v: Block, shortName: String, displayName: String, uniqueName: String) = {
    if (accept(v)) {
      short += shortName
      unique += uniqueName
      true
    } else false
  }

  override final def writeToFile(): Unit = {
    val s = short ++ Seq("", "") ++ unique.distinct ++ Seq(unique.distinct.reduceOption((s1, s2) => s1 + ", " + s2).getOrElse(""), short.size.toString)
    Files.write(out, s.asJava)
  }
}

object OreFilter extends SFilter {
  private[this] final val oreDicPattern = Pattern.compile("^ore[A-Z].*")
  override val out: Path = Paths.get(Dumper.modID, "ore.txt")

  override def accept(block: Block) = {
    val oreName = BlocksDump.oreNameSeq(block)
    oreName.exists(n => oreDicPattern.matcher(n.getPath).matches())
  }
}

object WoodFilter extends SFilter {
  private[this] final val woodPATTERN = Pattern.compile(".* Wood")
  private[this] final val woodDicPATTERN = Pattern.compile("^wood[A-Z].*")

  override val out: Path = Paths.get(Dumper.modID, "wood.txt")

  override def accept(block: Block): Boolean = {
    if (BlockTags.LOGS.contains(block) || ItemTags.LOGS.contains(block.asItem()))
      return true
    var nameFlag = false
    DistExecutor.callWhenOn(Dist.CLIENT, () => () => {
      val s = block.getNameTextComponent.getUnformattedComponentText
      if (woodPATTERN.matcher(s).matches)
        nameFlag = true
    })
    if (nameFlag)
      return true
    val oreName = BlocksDump.oreNameSeq(block)
    oreName.exists(n => woodDicPATTERN.matcher(n.getPath).matches)
  }
}

object LeaveFilter extends SFilter {
  override val out: Path = Paths.get(Dumper.modID, "leave.txt")

  override def accept(block: Block) = BlockTags.LEAVES.contains(block) || ItemTags.LEAVES.contains(block.asItem())
}
