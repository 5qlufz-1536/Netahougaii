package com.saluf.netahougaii.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class ClientUtil {
    public static boolean isOverworld(MinecraftClient client) {
        return client.world != null && client.world.getRegistryKey() == World.OVERWORLD;
    }

    public static boolean isNight(MinecraftClient client) {
        if(client.world == null) {
            return false;
        }
        long timeOfDay = client.world.getTimeOfDay() % 24000;
        return timeOfDay >= 13000 && timeOfDay <= 23000;
    }

    public static boolean hasPhantomSpawned(MinecraftClient client) {
        if (client.world == null) {
            return false;
        }
        assert client.player != null;
        return !client.world.getEntitiesByType(EntityType.PHANTOM, client.player.getBoundingBox().expand(100), entity -> true).isEmpty();
    }
}
