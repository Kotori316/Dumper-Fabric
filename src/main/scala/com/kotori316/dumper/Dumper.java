package com.kotori316.dumper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dumper implements ModInitializer {
    public static final String modID = "kotori_dumper";
    public static final Logger LOGGER = LoggerFactory.getLogger(modID);
    private static Dumper dumper;

    public Dumper() {
        dumper = this;
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onWorldLoad);
    }

    public void onWorldLoad(MinecraftServer server) {
        DumperInternal.worldLoaded(server);
    }

    public static Dumper getInstance() {
        return dumper;
    }

    public static boolean isEnabled(String key) {
        return DumperInternal.isEnabled(key);
    }
}
