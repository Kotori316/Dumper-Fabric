package com.kotori316.dumper.dumps

import com.google.common.collect.BiMap
import net.minecraftforge.fluids.{Fluid, FluidRegistry}

import scala.collection.JavaConverters._

object FluidNames extends Dumps {
  override val configName: String = "OutputFluid"
  override val fileName: String = "fluid"
  /*private[this] val defaultFluidNameField = classOf[FluidRegistry].getDeclaredField("defaultFluidName")
  defaultFluidNameField.setAccessible(true)
  lazy val defaultFluidNameMap = defaultFluidNameField.get(null).asInstanceOf[BiMap[String, String]].asScala
  //noinspection ScalaDeprecation
  lazy val nameToIdMap = FluidRegistry.getRegisteredFluidIDs.asScala*/

  override def content(): Seq[String] = {
    /*val fluids = FluidRegistry.getRegisteredFluids.asScala.map { case (s, f) => FD(s, f) }
    val maxRNameLength = fluids.map(_.uniqueName.length).max
    val maxUNameLength = fluids.map(_.fluid.getUnlocalizedName.length).max
    val format = s"%-${maxRNameLength}s : %3d : %-${maxUNameLength}s : %5d : %5d : %4d : %6d : %5b : %8s : %8X : %5b"
    val f2 = s"%-${maxRNameLength}s :  ID : %-${maxUNameLength}s : Luminosity : Density : Temperature : Viscosity : Gaseous : Rarity : Color : hasBlock"
    val title = f2.format("RegistryName", "UnlocalizedName")

    Seq(title, "") ++ fluids.toList.sorted.map(f =>
      format.format(f.uniqueName, f.id, f.fluid.getUnlocalizedName, f.fluid.getLuminosity, f.fluid.getDensity,
        f.fluid.getTemperature, f.fluid.getViscosity, f.fluid.isGaseous, f.fluid.getRarity.toString, f.fluid.getColor, f.fluid.canBePlacedInWorld)
    )*/
    Nil
  }

  /*case class FD(name: String, fluid: Fluid) extends Ordered[FD] {
    val id = nameToIdMap(fluid)
    val uniqueName = defaultFluidNameMap(name)

    override def compare(that: FD): Int = this.id compareTo that.id
  }*/

}
