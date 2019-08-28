package com.kotori316.dumper.dumps

import java.util

import net.minecraft.tileentity.{TileEntity, TileEntityType}
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}
import net.minecraftforge.registries.ForgeRegistries

import scala.collection.JavaConverters._
import scala.util.control.Exception._
import scala.util.{Failure, Success, Try}

object TENames extends Dumps[TileEntity] {
  override val configName: String = "OutputTileentity"
  override val fileName: String = "tileentity"

  val field_Capacity = classOf[CapabilityManager].getDeclaredField("providers")
  field_Capacity.setAccessible(true)

  override def content(filters: Seq[Filter[TileEntity]]): Seq[String] = {
    val value = ForgeRegistries.TILE_ENTITIES
    val REGISTRY = value.getKeys.asScala.clone().map(name => {
      val tileType: TileEntityType[_ <: TileEntity] = value.getValue(name)
      val instance: Try[TileEntity] = nonFatalCatch withTry tileType.create()
      val clazz = instance.map(_.getClass).getOrElse(classOf[TileEntity])
      (name, clazz, instance)
    }).toSeq.sortBy(_._1.toString)
    val CAPABILITY = field_Capacity.get(CapabilityManager.INSTANCE).asInstanceOf[util.IdentityHashMap[String, Capability[_]]].asScala.clone().values

    val a = CAPABILITY.map(a => a.getName).toSeq
    val b = REGISTRY.flatMap {
      case (name, clazz, t: Try[TileEntity]) =>

        val capName = t.map { tile =>
          CAPABILITY.filter(c => tile.getCapability(c, Direction.UP).isPresent).map(simpleName).mkString(" : ")
        } match {
          case Success(s) => "         " + s
          case Failure(exception) => exception.toString
        }

        Seq(name + " : " + clazz.getName, capName)

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
