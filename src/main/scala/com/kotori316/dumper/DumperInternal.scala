package com.kotori316.dumper

import java.nio.file.{Files, Paths}

import com.kotori316.dumper.dumps.items.{BlocksDump, ItemsDump}
import com.kotori316.dumper.dumps.{Dumps, EnchantmentNames, ModNames, TENames}
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.event.server.FMLServerStartedEvent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Failure

object DumperInternal {
  val loadCompleteDumpers: Seq[Dumps[_]] = Seq(ModNames, EnchantmentNames, /*FluidNames,*/ TENames)
  val loginDumpers: Seq[Dumps[_]] = Seq(ItemsDump, BlocksDump)

  def loadComplete(event: FMLLoadCompleteEvent): Unit = {
    output(loadCompleteDumpers)
  }

  def worldLoaded(event: FMLServerStartedEvent): Unit = {
    output(loginDumpers)
  }

  private def output(dumpers: Seq[Dumps[_]]): Unit = {
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

class Config(builder: ForgeConfigSpec.Builder) {
  builder.comment("Which information to output?").push("setting")
  val enables0 = DumperInternal.loadCompleteDumpers.view.map(_.configName).map(n =>
    builder.comment(s"Enable output of $n.").define(n, true)).force
  val enables1 = DumperInternal.loginDumpers.view.map(_.configName).map(n =>
    builder.comment(s"Enable output of $n.").define(n, true)).force
  val enables = enables0 ++ enables1
  builder.pop()
}
