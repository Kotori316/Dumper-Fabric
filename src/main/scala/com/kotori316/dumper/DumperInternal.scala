package com.kotori316.dumper

import java.nio.file.{Files, Paths}

import com.kotori316.dumper.dumps.items.{BlocksDump, ItemsDump}
import com.kotori316.dumper.dumps.{EnchantmentNames, FluidNames, ModNames, TENames}
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Failure

object DumperInternal {
  val dumpers = Seq(ModNames, EnchantmentNames, /*FluidNames,*/ TENames, ItemsDump, BlocksDump)

  class Config(builder: ForgeConfigSpec.Builder) {
    builder.comment("Which information to output?").push("setting")
    val enables = dumpers.view.map(_.configName).map(n =>
      builder.comment(s"Enable output of $n.").define(n, true)).force
    builder.pop()
  }

  def loadComplete(event: FMLLoadCompleteEvent): Unit = {
    val l = System.nanoTime()
    val ROOTPath = Paths.get(Dumper.modID)
    if (Files.notExists(ROOTPath))
      Files.createDirectories(ROOTPath)
    val futures = Future.traverse(dumpers)(d => {
      val future = Future(d())
      future.onComplete {
        case Failure(exception) => Dumper.LOGGER.error(d.getClass, exception)
        case _ =>
      }
      future
    })
    Await.ready(futures, Duration.Inf)
    futures.onComplete { _ =>
      val l2 = System.nanoTime()
      Dumper.LOGGER.info(f"Dumper finished in ${(l2 - l) / 1e9}%.3f s")
    }
  }
}
