package com.saluf.netahougaii.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.saluf.netahougaii.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.saluf.netahougaii.config.ConfigManager.volume;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RegisterCommand {

    public enum DisplayMode {
        ALWAYS,
        SHOW_ONCE,
        HIDE,
        OVERWORLD_ONLY
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("netahougaii")
                    .then(literal("set_mode")
                            .then(argument("mode", StringArgumentType.word())
                                    .suggests(((context, builder) -> {
                                        builder.suggest("ALWAYS");
                                        builder.suggest("SHOW_ONCE");
                                        builder.suggest("OVERWORLD_ONLY");
                                        builder.suggest("HIDE");
                                        return builder.buildFuture();
                                    }))
                                    .executes(ctx -> {
                                        String mode2 = StringArgumentType.getString(ctx, "mode");
                                        DisplayMode newmode = switch (mode2) {
                                            case "ALWAYS" -> DisplayMode.ALWAYS;
                                            case "SHOW_ONCE" -> DisplayMode.SHOW_ONCE;
                                            case "OVERWORLD_ONLY" -> DisplayMode.OVERWORLD_ONLY;
                                            case "HIDE" -> DisplayMode.HIDE;
                                            default -> null;
                                        };
                                        if (newmode != null) {
                                            ConfigManager.displayMode = newmode;
                                            ConfigManager.setDisplayMode(newmode);
                                            ConfigManager.saveConfig(); // 保存
                                            ctx.getSource().sendFeedback(Text.literal("モードを " + mode2 + " に設定しました。").styled(style -> style.withColor(Formatting.GREEN)));
                                            return 1;
                                        }
                                        return 0;
                                    })
                            )
                    )
                    .then(literal("set_message")
                            .then(argument("hour", IntegerArgumentType.integer(0, 23))
                                    .then(argument("message", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                int hour = IntegerArgumentType.getInteger(ctx, "hour");
                                                String message = StringArgumentType.getString(ctx, "message");
                                                ConfigManager.setCustomMessage(hour, message);
                                                ctx.getSource().sendFeedback(Text.literal("カスタムメッセージを設定しました: " + message));
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(literal("reset_message")
                            .executes(ctx -> {
                                ConfigManager.resetCustomMessages();
                                ctx.getSource().sendFeedback(Text.literal("カスタムメッセージをリセットしました。"));
                                return 1;
                            })
                    )
                    .then(literal("set_volume")
                            .then(argument("volume", IntegerArgumentType.integer(0, 100))
                                    .executes(ctx -> {
                                        volume = IntegerArgumentType.getInteger(ctx, "volume");
                                        ConfigManager.setVolume(volume);
                                        ctx.getSource().sendFeedback(Text.literal("音量を " + volume + "% に設定しました。"));
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }
}
