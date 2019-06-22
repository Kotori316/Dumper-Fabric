package com.kotori316.dumper.dumps

import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo
import net.minecraftforge.forgespi.language.IModInfo

import scala.collection.JavaConverters._

object ModNames extends Dumps {

  override val configName: String = "OutputModNames"
  override val fileName: String = "mods"

  override def content(): Seq[String] = {
    val modContainers = ModList.get().getMods.asScala.map(ModData.apply)
    //    val apiContainers = ModAPIManager.INSTANCE.getAPIList.asScala.toSeq.sortBy(_.getModId.toLowerCase)
    // val map = modContainers.filterNot(_.isDummy).map(o => (o.getSource, o.getSource.getMods.get(0).getDisplayName))

    val maxId: Int = modContainers.map(_.idLength).max
    val maxName: Int = modContainers.map(_.nameLength + 2).max
    val format = s"%3d : %-${maxId}s : %-${maxName}s : %s : %s : %s"
    val mods = for ((data, index) <- modContainers.zipWithIndex) yield {
      val id = data.getModId
      val name = "\"" + data.getName + "\""
      val ver = data.mod.getVersion
      val file = data.getSource match {
        case modFileInfo: ModFileInfo => modFileInfo.getFile.getFileName
        case _ => ""
      }
      val modObjClassName = ModList.get().getModObjectById[AnyRef](data.getModId).map(o => o.getClass.getName).orElse("Dummy")
      format.format(index + 1, id, name, ver, file, modObjClassName)
    }

    val apiS = Nil /*apiContainers.map(api => {
      val name = "\"" + api.getModId + "\""
      val ver = api.getVersion
      val file = api.getSource
      val providedMod = map.get(file)
      s"$name : ${providedMod.getOrElse(file.toString)} : $ver"
    })*/
    "Number:Mod ID:Name:Version" +: (if (apiS.nonEmpty) mods ++ Seq("", "", "Supported API { ApiName, Provided, version }") ++ apiS else mods)
  }

  case class ModData(mod: IModInfo) {
    def getName = mod.getDisplayName

    def getModId = mod.getModId

    def getSource = mod.getOwningFile

    def idLength = mod.getModId.length

    def nameLength = getName.length

    def isDummy = getSource == null
  }

}