package com.kotori316.dumper.dumps

import java.util

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.{BlockPos, Registry}
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.state.BlockState

import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.util.control.Exception._

object TENames extends FastDumps[BlockEntity] {
  override val configName: String = "OutputTileentity"
  override val fileName: String = "tileentity"

  private final val fieldValidBlocks = {
    val className = FabricLoader.getInstance().getMappingResolver.unmapClassName("intermediary", classOf[BlockEntityType[_]].getName)
    //noinspection SpellCheckingInspection
    val fieldName = FabricLoader.getInstance().getMappingResolver.mapFieldName(
      "intermediary", className, "field_19315", s"L${classOf[java.util.Set[_]].getName.replace('.', '/')};"
    )
    val f = classOf[BlockEntityType[_]].getDeclaredField(fieldName)
    f.setAccessible(true)
    f
  }

  override def content(filters: Seq[Filter[BlockEntity]]): Seq[String] = {
    val value = Registry.BLOCK_ENTITY_TYPE
    val REGISTRY = value.keySet().asScala.clone().map(name => {
      val tileType: BlockEntityType[_ <: BlockEntity] = value.get(name).asInstanceOf[BlockEntityType[_ <: BlockEntity]]
      val instance: Try[BlockEntity] = allCatch withTry tileType.create(BlockPos.ZERO, getStateForBlockEntity(tileType))
      val clazz = instance.map(_.getClass).getOrElse(classOf[BlockEntity])
      (name, clazz, instance)
    }).toSeq.sortBy(_._1.toString)

    val b = REGISTRY.flatMap {
      case (name, clazz, t: Try[BlockEntity]) =>
        Seq(name.toString + " : " + clazz.getName)
    }
    "------TileEntities------" +: b
  }

  private def getStateForBlockEntity(entityType: BlockEntityType[_]): BlockState = {
    fieldValidBlocks.get(entityType).asInstanceOf[util.Set[Block]].asScala
      .head
      .defaultBlockState()
  }
}
