package com.kotori316.dumper.dumps

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import com.kotori316.dumper.Dumper
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

import scala.collection.JavaConverters._

trait Dumps {

  val configName: String
  val fileName: String

  def path: Path = Paths.get(Dumper.mod_ID, fileName + ".txt")

  def apply(): Unit = {
    if (Dumper.enables.contains(configName)) {
      val path1 = Paths.get(Dumper.mod_ID, s"1_${fileName}_1.txt")
      val path2 = Paths.get(Dumper.mod_ID, s"2_${fileName}_2.txt")
      if (Files.exists(path)) {
        if (Files.exists(path1))
          Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING)
        Files.move(path, path1, StandardCopyOption.REPLACE_EXISTING)
      }
      val nano = System.nanoTime()
      val c = content()
      val strings = c :+ s"Output took ${((System.nanoTime() - nano) / 1e9).toString.substring(0, 4)}s"
      Files.write(path, strings.asJava)
    }
  }

  def content(): Seq[String]

  def oreName(stack: ItemStack): String = {
    oreNameSeq(stack).mkString(", ") match {
      case "" => ""
      case s => " : " + s
    }
  }

  def oreNameSeq(stack: ItemStack) = {
    Some(stack).filterNot(_.isEmpty).toList
      .flatMap(s => OreDictionary.getOreIDs(s))
      .map(OreDictionary.getOreName)
      .filterNot(_ == "Unknown")
      .sorted
  }
}
