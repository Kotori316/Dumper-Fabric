package com.kotori316.dumper.dumps

import net.minecraft.core.Registry
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluid

import scala.jdk.CollectionConverters._

object FluidNames extends FastDumps[Fluid] {
  override val configName: String = "OutputFluid"
  override val fileName: String = "fluid"

  private[this] final val luminosity = "Luminosity"
  private[this] final val density = "Density"
  private[this] final val temperature = "Temperature"
  private[this] final val viscosity = "Viscosity"
  private[this] final val gaseous = "Gaseous"
  private[this] final val rarity = "Rarity"
  private[this] final val color = "Color"
  private[this] final val hasBlock = "hasBlock"
  final val formatter = new Formatter[Fluid](Seq("-RegistryName", "-Name", luminosity, density, temperature, viscosity, gaseous, rarity, color, hasBlock),
    Seq(Registry.FLUID.getKey, f => f.defaultFluidState().createLegacyBlock().getBlock.getName.getString, _ => 0, _ => 0,
      _ => s"${0} [K]", _ => 0, _ => false, _ => Rarity.COMMON,
      _ => "00000000", _.defaultFluidState().createLegacyBlock() != Blocks.AIR.defaultBlockState))

  override def content(filters: Seq[Filter[Fluid]]): Seq[String] = {
    val fluids = Registry.FLUID.asScala
    formatter.format(fluids.toSeq)
  }

}
