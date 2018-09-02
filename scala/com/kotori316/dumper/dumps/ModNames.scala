package com.kotori316.dumper.dumps

import net.minecraftforge.fml.common.{InjectedModContainer, Loader, ModAPIManager, ModContainer}

import scala.collection.JavaConverters._

object ModNames extends Dumps {

    override val configName: String = "OutputModNames"
    override val fileName: String = "mods"

    override def content(): Seq[String] = {
        val modContainers = Loader.instance.getModList.asScala.map(ModData.apply)
        val apiContainers = ModAPIManager.INSTANCE.getAPIList.asScala.toSeq.sortBy(_.getModId.toLowerCase)
        val map = modContainers.filter { case ModData(p) => p.getMetadata != null && p.getMetadata.parentMod == null && p.getMetadata.parent.isEmpty }
          .filterNot(_.isDummy).map(o => (o.mod.getSource, o.mod.getName)).toMap

        val maxId: Int = modContainers.map(_.idLength).max
        val maxName: Int = modContainers.map(_.nameLength + 2).max
        val format = s"%3d : %-${maxId}s : %-${maxName}s : %s : %s : %s"
        val mods = for ((data, index) <- modContainers.zipWithIndex) yield {
            val id = data.mod.getModId
            val name = "\"" + data.mod.getName + "\""
            val ver = data.mod.getVersion
            val file = data.mod.getSource.getName
            val modObj = Option(data.mod.getMod).fold("Dummy")(_.getClass.getName)
            format.format(index + 1, id, name, ver, file, modObj)
        }

        val apis = apiContainers.map(api => {
            val name = "\"" + api.getModId + "\""
            val ver = api.getVersion
            val file = api.getSource
            val providedMod = map.get(file)
            s"$name : ${providedMod.getOrElse(file.toString)} : $ver"
        })
        "Number:Mod ID:Name:Version" +: (if (apis.nonEmpty) mods ++ Seq("", "", "Supported API { ApiName, Provided, version }") ++ apis else mods)
    }

    case class ModData(mod: ModContainer) {
        def idLength = mod.getModId.length

        def nameLength = mod.getName.length

        def isDummy = mod.isInstanceOf[InjectedModContainer]
    }

}
