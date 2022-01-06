package com.kotori316.dumper

import java.nio.file.{Files, Paths}

import com.kotori316.dumper.dumps._
import com.kotori316.dumper.dumps.items.{BlocksDump, ItemsDump, TagDump}
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

object DumperInternal {
  val loadCompleteDumpers: Seq[FastDumps[_]] = Seq(ModNames, EnchantmentNames, FluidNames, TENames, EntityNames)
  val loginDumpers: Seq[Dumps[_]] = Seq(ItemsDump, BlocksDump, TagDump, RecipeNames)

  def loadComplete(event: FMLLoadCompleteEvent): Unit = {
    output(loadCompleteDumpers, null)
  }

  def worldLoaded(event: ServerStartedEvent): Unit = {
    output(loginDumpers, event.getServer)
  }

  private def output(dumpers: Seq[Dumps[_]], server: MinecraftServer): Unit = {
    val l = System.nanoTime()
    val ROOTPath = Paths.get(Dumper.modID)
    if (Files.notExists(ROOTPath))
      Files.createDirectories(ROOTPath)
    val futures = Future.traverse(dumpers) { d =>
      val future = Future(d.apply(server))
      future.onComplete {
        case Failure(exception) => Dumper.LOGGER.error(d.getClass, exception)
        case _ => Dumper.LOGGER.info(s"Success to output ${d.fileName}.txt")
      }
      future
    }
    //    Await.ready(futures, Duration(1, "min"))
    futures.onComplete { _ =>
      val l2 = System.nanoTime()
      Dumper.LOGGER.info(f"Dumper finished in ${(l2 - l) / 1e9}%.3f s")
    }
  }

}

class Config(builder: ForgeConfigSpec.Builder) {
  builder.comment("Which information to output?").push("setting")
  val enables0: Seq[ForgeConfigSpec.BooleanValue] = DumperInternal.loadCompleteDumpers.map(_.configName).map(n =>
    builder.comment(s"Enable output of $n.").define(n, true))
  val enables1: Seq[ForgeConfigSpec.BooleanValue] = DumperInternal.loginDumpers.map(_.configName).map(n =>
    builder.comment(s"Enable output of $n.").define(n, true))
  val enables: Seq[ForgeConfigSpec.BooleanValue] = enables0 ++ enables1
  builder.pop()
}
