package com.kotori316.dumper.dumps

import net.minecraft.block.Blocks
import net.minecraft.fluid.Fluid
import net.minecraft.item.Rarity
import net.minecraftforge.registries.ForgeRegistries

import scala.collection.JavaConverters._

object FluidNames extends Dumps[Fluid] {
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
  private[this] final val lengthOfRarity = Rarity.values().map(_.toString.length).max

  override def content(filters: Seq[Filter[Fluid]]): Seq[String] = {
    val fluids = ForgeRegistries.FLUIDS.asScala
    val maxRNameLength = fluids.flatMap(f => Option(f.getRegistryName).map(_.toString.length).toList).max
    val maxUNameLength = fluids.flatMap(f => Option(f.getAttributes.getTranslationKey).map(_.length).toList).max
    val format = s"%-${maxRNameLength}s : %3d : %-${maxUNameLength}s : %${luminosity.length}d : %${density.length}d : %${temperature.length}s : " +
      s"%${viscosity.length}d : %${gaseous.length}b : %${lengthOfRarity}s : %8X : %${hasBlock.length}b"
    val f2 = s"%-${maxRNameLength}s :  ID : %-${maxUNameLength}s : $luminosity : $density : $temperature : $viscosity : $gaseous : %${lengthOfRarity}s : %8s : $hasBlock"
    val title = f2.format("RegistryName", "UnlocalizedName", rarity, color)
    Seq(title, "") ++ fluids.toList.zipWithIndex.map { case (f, i) =>
      format.format(f.getRegistryName, i, f.getAttributes.getTranslationKey, f.getAttributes.getLuminosity, f.getAttributes.getDensity,
        f.getAttributes.getTemperature + " [K]", f.getAttributes.getViscosity, f.getAttributes.isGaseous, f.getAttributes.getRarity.toString,
        f.getAttributes.getColor, f.getDefaultState.getBlockState != Blocks.AIR.getDefaultState)
    }
  }
}
