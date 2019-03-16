package com.kotori316.dumper.dumps

import java.util

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.registry.RegistryNamespaced
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}
import net.minecraftforge.fml.relauncher.ReflectionHelper

import scala.collection.JavaConverters._
import scala.util.control.Exception._
import scala.util.{Failure, Success}

object TENames extends Dumps {
  override val configName: String = "OutputTileentity"
  override val fileName: String = "tileentity"

  val field_registry = ReflectionHelper.findField(classOf[TileEntity], "REGISTRY", "field_190562_f")
  val field_Capacity = ReflectionHelper.findField(classOf[CapabilityManager], "providers")

  override def content(): Seq[String] = {
    val value = field_registry.get(null).asInstanceOf[RegistryNamespaced[ResourceLocation, Class[_ <: TileEntity]]]
    val REGISTRY = value.getKeys.asScala.clone().map(name => {
      val clazz = value.getObject(name)
      val instance = nonFatalCatch withTry clazz.newInstance()
      (name, clazz, instance)
    }).toSeq.sortBy(_._1.toString)
    val CAPABILITY = field_Capacity.get(CapabilityManager.INSTANCE).asInstanceOf[util.IdentityHashMap[String, Capability[_]]].asScala.clone().values

    val a = CAPABILITY.map(a => a.getName).toSeq
    val b = REGISTRY.flatMap {
      case (name, clazz, t) =>
        Seq(name + " : " + clazz.getName,
          t.map(t => CAPABILITY.filter(t.hasCapability(_, EnumFacing.UP)).map(simpleName).mkString(" : ")) match {
            case Success(s) => "         " + s
            case Failure(exception) => exception.toString
          })

    }
    "------Capabilities------" +: (a ++ Seq("", "", "------TileEntities------") ++ b)
  }

  val simpleName: Capability[_] => String = { c =>
    val s = c.getName
    val a = s.lastIndexOf('.')
    if (a == -1) {
      s
    } else {
      s.drop(a + 1)
    }
  }
}
