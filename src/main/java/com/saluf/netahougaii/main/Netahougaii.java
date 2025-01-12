package com.saluf.netahougaii.main;

import com.saluf.netahougaii.command.RegisterCommand;
import com.saluf.netahougaii.config.ConfigManager;
import com.saluf.netahougaii.utils.AudioUtil;
import com.saluf.netahougaii.utils.ClientUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.LocalTime;
import java.util.Objects;

import static com.saluf.netahougaii.config.ConfigManager.displayMode;
import static com.saluf.netahougaii.utils.ClientUtil.isOverworld;

public class Netahougaii {
    private static final int NIGHT_START = 12541;
    private static final int NIGHT_END = 23458;
    private static boolean hasDisplayedRealWorldMessage = false;
    private static boolean hasDisplayedSleepMessage = false;
    private static int lastDisplayedHour = -1;

    private static void displaySleepMessage(MinecraftClient client) {
        assert client.player != null;
        if (ClientUtil.hasPhantomSpawned(client)) {
            client.player.sendMessage(Text.literal("ファントムが湧くらしいので、そろそろ寝たほうがいいですよ(笑)").styled(style -> style.withColor(Formatting.RED).withBold(true)), true);
        } else {
            client.player.sendMessage(Text.literal("そろそろ寝たほうがいいですよ(笑)").styled(style -> style.withColor(Formatting.GOLD).withBold(true)), true);
        }
    }

    public static void registerNetahougaii() {
        ConfigManager.loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long time = 0;
            if (client.world != null) {
                time = client.world.getTimeOfDay() % 24000;

                //簡略化可能
                if (time >= NIGHT_START && time <= NIGHT_END) {
                    if (displayMode == RegisterCommand.DisplayMode.SHOW_ONCE) {
                        if (!hasDisplayedSleepMessage) {
                            displaySleepMessage(client);
                            hasDisplayedSleepMessage = true;
                        }
                    } else if (displayMode == RegisterCommand.DisplayMode.ALWAYS) {
                        displaySleepMessage(client);
                        hasDisplayedSleepMessage = true;
                    } else if (displayMode == RegisterCommand.DisplayMode.OVERWORLD_ONLY) {
                        if(isOverworld(client)) {
                            displaySleepMessage(client);
                            hasDisplayedSleepMessage = true;
                        } else {
                            if (!hasDisplayedSleepMessage) {
                                displaySleepMessage(client);
                                hasDisplayedSleepMessage = true;
                            }
                        }
                    }
                } else {
                    if (displayMode == RegisterCommand.DisplayMode.SHOW_ONCE || displayMode == RegisterCommand.DisplayMode.HIDE) {
                        hasDisplayedSleepMessage = false;
                    } else if (displayMode == RegisterCommand.DisplayMode.ALWAYS) {
                        hasDisplayedSleepMessage = true;
                    } else if (displayMode == RegisterCommand.DisplayMode.OVERWORLD_ONLY) {
                        if (isOverworld(client)) {
                            hasDisplayedSleepMessage = true;
                        } else {
                            hasDisplayedSleepMessage = false;
                        }
                    }
                }
            }
            registerRealTimeNetahougaii(client);
        });
    }

    public static void registerRealTimeNetahougaii(MinecraftClient client) {
        AudioUtil audioUtil = new AudioUtil();
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        if (client.player != null && (hour == 23 || hour <= 2) && !hasDisplayedRealWorldMessage || client.player != null && (hour == 23 || hour <= 2) && lastDisplayedHour != hour) {

            String customMessage = ConfigManager.getCustomMessage(hour);
            client.execute(() -> {
                audioUtil.loadAudio("sounds/sorosoronero.wav");
                audioUtil.setVolume(-75.0f + ConfigManager.volume);
                audioUtil.play();

                Objects.requireNonNull(client.player).sendMessage(
                        Text.literal("そろそろ寝たほうがいいですよ(笑)").styled(style -> style.withColor(Formatting.GOLD).withBold(true)),
                        false
                );
                client.inGameHud.setTitle(
                        Text.literal("そろそろ寝たほうがいいですよ(笑)").styled(style -> style.withColor(Formatting.GOLD).withBold(true))
                );

                client.inGameHud.setSubtitle(
                        Text.literal(customMessage).styled(style -> style.withColor(Formatting.BOLD).withBold(true))
                );

            });

            hasDisplayedRealWorldMessage = true;
            lastDisplayedHour = hour;
        } else if (hour >= 3 && hour < 23) {
            hasDisplayedRealWorldMessage = false;
            lastDisplayedHour = -1;
        }
    }
}
