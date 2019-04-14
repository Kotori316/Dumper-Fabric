package com.kotori316.dumper;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Dumper.modID)
public class Dumper {
    public static final String modID = "kotori_dumper";
    public static final Logger LOGGER = LogManager.getLogger(modID);
    private static Dumper dumper;
    public final DumperInternal.Config config;

    public Dumper() {
        dumper = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onComplete);
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        config = new DumperInternal.Config(builder);
        ForgeConfigSpec spec = builder.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec);
    }

    public void onComplete(FMLLoadCompleteEvent event) {
        DumperInternal.loadComplete(event);
    }

    public static Dumper getInstance() {
        return dumper;
    }
}
