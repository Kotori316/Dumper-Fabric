package com.kotori316.dumper.dumps

import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo
import net.minecraftforge.forgespi.language.{IModFileInfo, IModInfo}

import scala.jdk.CollectionConverters._

object ModNames extends FastDumps[IModInfo] {

  override val configName: String = "OutputModNames"
  override val fileName: String = "mods"

  final val formatter = new Formatter[ModData](Seq("Number", "-ModID", "-Name", "Version", "-File Name", "-Class"),
    Seq(_.num, _.getModId, d => "\"" + d.getName + "\"", _.mod.getVersion, _.getSource match {
      case modFileInfo: ModFileInfo => modFileInfo.getFile.getFileName
      case _ => ""
    }, data => ModList.get().getModObjectById[AnyRef](data.getModId).map[String](o => o.getClass.getName).orElse("Dummy")))

  override def content(filters: Seq[Filter[IModInfo]]): Seq[String] = {
    val modContainers = ModList.get().getMods.asScala.zipWithIndex.map((ModData.apply _).tupled)
    //    val apiContainers = ModAPIManager.INSTANCE.getAPIList.asScala.toSeq.sortBy(_.getModId.toLowerCase)
    // val map = modContainers.filterNot(_.isDummy).map(o => (o.getSource, o.getSource.getMods.get(0).getDisplayName))

    //    val maxId: Int = modContainers.map(_.idLength).max
    //    val maxName: Int = modContainers.map(_.nameLength + 2).max
    //    val format = s"%3d : %-${maxId}s : %-${maxName}s : %s : %s : %s"
    //    val mods = for ((data, index) <- modContainers.zipWithIndex) yield {
    //      val id = data.getModId
    //      val name = "\"" + data.getName + "\""
    //      val ver = data.mod.getVersion
    //      val file = data.getSource match {
    //        case modFileInfo: ModFileInfo => modFileInfo.getFile.getFileName
    //        case _ => ""
    //      }
    //      val modObjClassName = ModList.get().getModObjectById[AnyRef](data.getModId).map(o => o.getClass.getName).orElse("Dummy")
    //      format.format(index + 1, id, name, ver, file, modObjClassName)
    //    }
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

  case class ModData(mod: IModInfo, i: Int) {
    val num: Int = i + 1

    def getName: String = mod.getDisplayName

    def getModId: String = mod.getModId

    def getSource: IModFileInfo = mod.getOwningFile

    def idLength: Int = mod.getModId.length

    def nameLength: Int = getName.length

    def isDummy: Boolean = getSource == null
  }

}
