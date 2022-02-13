package com.kotori316.dumper.dumps

import java.nio.file.Path

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.{FabricLoader, ModContainer, Version}

import scala.jdk.CollectionConverters._
import scala.util.Try

object ModNames extends FastDumps[ModContainer] {

  override val configName: String = "OutputModNames"
  override val fileName: String = "mods"

  final val formatter = new Formatter[ModData](Seq("Number", "-ModID", "-Name", "Version", "-File Name", "-Class"),
    Seq(_.num, _.getModId, d => "\"" + d.getName + "\"", _.getVersion, _.getFile,
      data => data.modInstance.getOrElse("No main")))

  override def content(filters: Seq[Filter[ModContainer]]): Seq[String] = {

    val modContainers = FabricLoader.getInstance().getAllMods.asScala.zipWithIndex.map((ModData.apply _).tupled).toSeq
    val mods = formatter.format(modContainers)

    val apiS = Nil /*apiContainers.map(api => {
      val name = "\"" + api.getModId + "\""
      val ver = api.getVersion
      val file = api.getSource
      val providedMod = map.get(file)
      s"$name : ${providedMod.getOrElse(file.toString)} : $ver"
    })*/
    if (apiS.nonEmpty) mods ++ Seq("", "", "Supported API { ApiName, Provided, version }") ++ apiS else mods
  }

  case class ModData(mod: ModContainer, i: Int) {
    val num: Int = i + 1

    def getName: String = mod.getMetadata.getName

    def getModId: String = mod.getMetadata.getId

    def getSource: Path = {
      mod match {
        case container: net.fabricmc.loader.impl.ModContainerImpl => container.getOriginPath
        case m => m.getRootPath
      }
    }

    def getFile: Path = Option(getSource.getFileName).getOrElse(getSource)

    def getVersion: Version = mod.getMetadata.getVersion

    def idLength: Int = getModId.length

    def nameLength: Int = getName.length

    def isDummy: Boolean = getSource == null

    def modInstance: Option[String] = {
      FabricLoader.getInstance().getEntrypointContainers("main", classOf[ModInitializer])
        .asScala
        .find(_.getProvider == mod)
        .map(c => Try(c.getEntrypoint).map(_.toString).getOrElse("Error"))
    }
  }

}
