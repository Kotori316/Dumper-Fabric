package com.kotori316.dumper

import java.nio.file.{Files, Path}

import com.google.gson.{GsonBuilder, JsonObject}
import com.kotori316.dumper.dumps._
import com.kotori316.dumper.dumps.items.{BlocksDump, ItemsDump, MineableDump, TagDump}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.GsonHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps
import scala.util.{Failure, Success, Using}

object DumperInternal {
  val loadCompleteDumpers: Seq[FastDumps[_]] = Seq(ModNames, EnchantmentNames, FluidNames, TENames, EntityNames)
  val loginDumpers: Seq[Dumps[_]] = Seq(ItemsDump, BlocksDump, TagDump, RecipeNames, MineableDump)

  def worldLoaded(server: MinecraftServer): Unit = {
    output(loginDumpers ++ loadCompleteDumpers, server)
  }

  private def output(dumpers: Seq[Dumps[_]], server: MinecraftServer): Unit = {
    val l = System.nanoTime()
    val ROOTPath = Path.of(Dumper.modID)
    if (Files.notExists(ROOTPath))
      Files.createDirectories(ROOTPath)
    val futures = Future.traverse(dumpers) { d =>
      val future = Future(d.apply(server))
      future.onComplete {
        case Failure(exception) => Dumper.LOGGER.error(d.getClass.getName, exception)
        case Success(value) if value => Dumper.LOGGER.info(s"Success to output ${d.fileName}.txt")
        case _ =>
      }
      future
    }
    //    Await.ready(futures, Duration(1, "min"))
    futures.onComplete { _ =>
      val l2 = System.nanoTime()
      Dumper.LOGGER.info(f"Dumper finished in ${(l2 - l) / 1e9}%.3f s")
    }
  }

  private val config: Map[String, Boolean] = {
    val gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
    val filePath = Path.of("config", "kotori_dumper-common.json")
    val json = Using(Files.newBufferedReader(filePath))(i => GsonHelper.fromJson(gson, i, classOf[JsonObject]))
    val keys = (loadCompleteDumpers ++ loginDumpers).map(_.configName)
    val fromJson = keys
      .flatMap(s => json.map(o => GsonHelper.getAsBoolean(o, s)).map(b => s -> b).toOption.toSeq)
      .toMap
    val defaultValue = keys.filterNot(fromJson.keySet).map(_ -> true).toMap
    val combined = fromJson ++ defaultValue
    val newJson = (new JsonObject).tap(o => combined.foreach { case (str, bool) => o.addProperty(str, bool) })

    Using(Files.newBufferedWriter(filePath)) { o =>
      gson.toJson(newJson, o)
    }
    combined
  }

  def isEnabled(key: String): Boolean = config.getOrElse(key, true)
}
