package com.kotori316.dumper.dumps

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import com.kotori316.dumper.Dumper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack

import scala.jdk.CollectionConverters._

trait Dumps[T] {

  val configName: String
  val fileName: String

  def path: Path = Paths.get(Dumper.modID, fileName + ".txt")

  def apply(server: MinecraftServer): Unit = {
    output(getFilters, server)
  }

  def output(filters: Seq[Filter[T]], server: MinecraftServer): Unit = {
    if (isEnabled) {
      val path1 = Paths.get(Dumper.modID, s"1_${fileName}_1.txt")
      val path2 = Paths.get(Dumper.modID, s"2_${fileName}_2.txt")
      if (Files.exists(path)) {
        if (Files.exists(path1))
          Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING)
        Files.move(path, path1, StandardCopyOption.REPLACE_EXISTING)
      }
      val nano = System.nanoTime()
      val c = content(filters, server)
      val strings = c :+ s"Output took ${((System.nanoTime() - nano) / 1e9).toString.substring(0, 4)}s"
      Files.write(path, strings.asJava)

      filters.foreach(_.writeToFile())
    }
  }

  protected def isEnabled = {
    true
  }

  def content(filters: Seq[Filter[T]], server: MinecraftServer): Seq[String]

  def getFilters: Seq[Filter[T]] = Nil

  def oreName(stack: ItemStack): String = {
    oreNameSeq(stack).mkString(", ") match {
      case "" => ""
      case s => " : " + s
    }
  }

  def oreNameSeq(stack: ItemStack): Iterator[ResourceLocation] = {
    ItemTags.getAllTags.getAllTags.asScala.collect { case (name, tag) if tag.contains(stack.getItem) => name }.iterator
  }
}
