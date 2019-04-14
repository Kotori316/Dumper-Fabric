package com.kotori316.dumper.dumps

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import com.kotori316.dumper.Dumper
import net.minecraft.item.ItemStack
import net.minecraft.tags.ItemTags
import net.minecraft.util.ResourceLocation

import scala.collection.JavaConverters._

trait Dumps {

  val configName: String
  val fileName: String

  val factoryClass = Class.forName("net.minecraftforge.registries.NamespacedWrapper$Factory")
  val WAPPAER_ID = factoryClass.getDeclaredField("ID").get(null).asInstanceOf[ResourceLocation]

  def path: Path = Paths.get(Dumper.modID, fileName + ".txt")

  def apply(): Unit = {
    if (isEnabled) {
      val path1 = Paths.get(Dumper.modID, s"1_${fileName}_1.txt")
      val path2 = Paths.get(Dumper.modID, s"2_${fileName}_2.txt")
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

  protected def isEnabled = {
    Dumper.getInstance().config.enables.find(_.getPath.contains(configName)).fold(false)(_.get())
  }

  def content(): Seq[String]

  def oreName(stack: ItemStack): String = {
    oreNameSeq(stack).mkString(", ") match {
      case "" => ""
      case s => " : " + s
    }
  }

  def oreNameSeq(stack: ItemStack) = {
    ItemTags.getCollection.getTagMap.asScala.collect { case (name, tag) if tag.contains(stack.getItem) => name }
  }
}
