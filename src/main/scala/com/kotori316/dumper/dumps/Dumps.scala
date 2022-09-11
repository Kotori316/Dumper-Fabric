package com.kotori316.dumper.dumps

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import com.kotori316.dumper.Dumper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

import scala.annotation.nowarn
import scala.jdk.CollectionConverters._
import scala.jdk.StreamConverters._
import scala.language.reflectiveCalls

trait Dumps[T] {

  val configName: String
  val fileName: String

  def path: Path = Paths.get(Dumper.modID, fileName + ".txt")

  def apply(server: MinecraftServer): Boolean = {
    output(getFilters, server)
  }

  def output(filters: Seq[Filter[T]], server: MinecraftServer): Boolean = {
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
      true
    } else {
      false
    }
  }

  protected def isEnabled = {
    Dumper.isEnabled(configName)
  }

  def content(filters: Seq[Filter[T]], server: MinecraftServer): Seq[String]

  def getFilters: Seq[Filter[T]] = Nil

  def tagName(obj: Block): String = combineTagName(tagNameSeq(obj))

  def tagName(obj: Item): String = combineTagName(tagNameSeq(obj))

  private def combineTagName(names: Seq[ResourceLocation]): String =
    names.mkString(", ") match {
      case "" => ""
      case s => " : " + s
    }

  //noinspection ScalaDeprecation,deprecation
  @nowarn
  def tagNameSeq(obj: Block): Seq[ResourceLocation] = {
    obj.builtInRegistryHolder().tags().toScala(Seq).map(_.location)
  }

  //noinspection ScalaDeprecation,deprecation
  @nowarn
  def tagNameSeq(obj: Item): Seq[ResourceLocation] = {
    obj.builtInRegistryHolder().tags().toScala(Seq).map(_.location)
  }
}
