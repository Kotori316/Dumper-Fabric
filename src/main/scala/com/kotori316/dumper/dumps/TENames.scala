package com.kotori316.dumper.dumps

import java.util

import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}
import net.minecraftforge.registries.ForgeRegistries

import scala.jdk.CollectionConverters._
import scala.util.control.Exception._
import scala.util.{Failure, Success, Try}

object TENames extends FastDumps[BlockEntity] {
  override val configName: String = "OutputTileentity"
  override val fileName: String = "tileentity"

  private[this] final val field_Capacity = classOf[CapabilityManager].getDeclaredField("providers")
  field_Capacity.setAccessible(true)
  private final val fieldValidBlocks = classOf[BlockEntityType[_]].getDeclaredField("validBlocks")
  fieldValidBlocks.setAccessible(true)

  override def content(filters: Seq[Filter[BlockEntity]]): Seq[String] = {
    val value = ForgeRegistries.BLOCK_ENTITIES
    val REGISTRY = value.getKeys.asScala.clone().map(name => {
      val tileType: BlockEntityType[_ <: BlockEntity] = value.getValue(name).asInstanceOf[BlockEntityType[_ <: BlockEntity]]
      val instance: Try[BlockEntity] = allCatch withTry tileType.create(BlockPos.ZERO, getStateForBlockEntity(tileType))
      val clazz = instance.map(_.getClass).getOrElse(classOf[BlockEntity])
      (name, clazz, instance)
    }).toSeq.sortBy(_._1.toString)
    val CAPABILITY = field_Capacity.get(CapabilityManager.INSTANCE).asInstanceOf[util.IdentityHashMap[String, Capability[_]]].asScala.clone().values

    val a = CAPABILITY.map(a => a.getName).toSeq
    val b = REGISTRY.flatMap {
      case (name, clazz, t: Try[BlockEntity]) =>

        val capName = t.map { tile =>
          CAPABILITY.filter(c => tile.getCapability(c, Direction.UP).isPresent).map(simpleName).mkString(" : ")
        } match {
          case Success(s) => "         " + s
          case Failure(exception) => exception.toString
        }

        Seq(name.toString + " : " + clazz.getName, capName)

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

  private def getStateForBlockEntity(entityType: BlockEntityType[_]): BlockState = {
    val set = fieldValidBlocks.get(entityType).asInstanceOf[util.Set[BlockState]]
    set.iterator().next()
  }
}
