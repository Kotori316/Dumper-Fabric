package com.kotori316.dumper

import java.nio.file.{Files, Paths}

import com.kotori316.dumper.dumps._
import com.kotori316.dumper.dumps.items.{BlocksDump, ItemsDump, MineableDump, TagDump}
import net.minecraft.server.MinecraftServer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

object DumperInternal {
  val loadCompleteDumpers: Seq[FastDumps[_]] = Seq(ModNames, EnchantmentNames, FluidNames, TENames, EntityNames)
  val loginDumpers: Seq[Dumps[_]] = Seq(ItemsDump, BlocksDump, TagDump, RecipeNames, MineableDump)

  def loadComplete(server: MinecraftServer): Unit = {
    output(loadCompleteDumpers, server)
  }

  def worldLoaded(server: MinecraftServer): Unit = {
    output(loginDumpers, server)
  }

  private def output(dumpers: Seq[Dumps[_]], server: MinecraftServer): Unit = {
    val l = System.nanoTime()
    val ROOTPath = Paths.get(Dumper.modID)
    if (Files.notExists(ROOTPath))
      Files.createDirectories(ROOTPath)
    val futures = Future.traverse(dumpers) { d =>
      val future = Future(d.apply(server))
      future.onComplete {
        case Failure(exception) => Dumper.LOGGER.error(d.getClass.getName, exception)
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

