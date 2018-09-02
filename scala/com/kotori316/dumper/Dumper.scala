package com.kotori316.dumper

import java.nio.file.{Files, Paths}

import com.kotori316.dumper.dumps.items.ItemsDump
import com.kotori316.dumper.dumps.{EnchantmentNames, FluidNames, ModNames, TENames}
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.{FMLLoadCompleteEvent, FMLPreInitializationEvent}
import org.apache.logging.log4j.LogManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

@Mod(modid = "kotori_dumper", name = "Dumper", version = "${version}", clientSideOnly = true, modLanguage = "scala")
object Dumper {
    val mod_ID = "kotori_dumper"
    val logger = LogManager.getLogger(mod_ID)
    val dumpers = Seq(ModNames, EnchantmentNames, FluidNames, TENames, ItemsDump)
    var enables = Set.empty[String]

    @Mod.EventHandler
    def preInit(event: FMLPreInitializationEvent): Unit = {
        val c = new Configuration(event.getSuggestedConfigurationFile)
        enables = dumpers.map(_.configName).filter(s => c.getBoolean(s, Configuration.CATEGORY_GENERAL, true, s)).toSet
        if (c.hasChanged) c.save()
    }

    @Mod.EventHandler
    def loadComplete(event: FMLLoadCompleteEvent): Unit = {
        val l = System.nanoTime()
        val ROOTPath = Paths.get(mod_ID)
        if (Files.notExists(ROOTPath))
            Files.createDirectories(ROOTPath)
        val futures = Future.traverse(dumpers)(d => {
            val future = Future.fromTry(Try(d()))
            future.onFailure { case e: Exception => logger.error(e) }
            future
        })
        Await.ready(futures, Duration.Inf)
        val l2 = System.nanoTime()
        logger.info(f"Dumper finished in ${(l2 - l) / 1e9}%.3f s")
    }
}
