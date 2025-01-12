package com.saluf.netahougaii.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saluf.netahougaii.command.RegisterCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Path CONFIG_PATH = Paths.get("config/netahougaii_config.json");
    private static final Gson GSON = new Gson();

    private static JsonObject json;
    private static Map<Integer, String> customMessages = new HashMap<>();
    private static final Map<Integer, String> DEFAULT_MESSAGES = Map.of(
            23, "そろそろ寝たほうがいいですよ(笑)",
            0, "日付超えましたよ、そろそろ寝たほうがいいですよ(笑)",
            1, "1時です、そろそろ寝たほうがいいですよ(笑)",
            2, "2時です、そろそろ寝たほうがいいですよ(笑)"
    );

    public static RegisterCommand.DisplayMode displayMode;
    public static boolean hardModeEnabled = false;
    public static int volume = 50;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                //ファイルのロード
                json = GSON.fromJson(Files.readString(CONFIG_PATH), JsonObject.class);
                System.out.println("Loaded JSON: " + json);

                if (json == null) {
                    json = new JsonObject();
                }
                // カスタムメッセージの読み込み
                if (json.has("custom_messages")) {
                    JsonObject messages = json.getAsJsonObject("custom_messages");
                    for (String key : messages.keySet()) {
                        customMessages.put(Integer.parseInt(key), messages.get(key).getAsString());
                    }
                }
                // ハードモードの設定を読み込み
                if (json.has("hard_mode_enabled")) {
                    hardModeEnabled = json.get("hard_mode_enabled").getAsBoolean();
                }
                //ボリュームの読み込み
                if (json.has("set_volume")) {
                    volume = json.get("set_volume").getAsInt();
                }
                // displayModeの読み込み
                if (json.has("displayMode")) {
                    String mode = String.valueOf(json.get("displayMode").getAsString());
                    displayMode = switch (mode) {
                        case "ALWAYS" -> RegisterCommand.DisplayMode.ALWAYS;
                        case "OVERWORLD_ONLY" -> RegisterCommand.DisplayMode.OVERWORLD_ONLY;
                        case "HIDE" -> RegisterCommand.DisplayMode.HIDE;
                        case "SHOW_ONCE" -> RegisterCommand.DisplayMode.SHOW_ONCE;
                        default -> {
                            System.err.println("Invalid displayMode found in JSON: " + mode + ". Defaulting to SHOW_ONCE.");
                            yield RegisterCommand.DisplayMode.SHOW_ONCE;
                        }
                    };

                } else {
                    displayMode = RegisterCommand.DisplayMode.SHOW_ONCE; // デフォルト値
                }

            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load config: " + e.getMessage());
                json = new JsonObject();
                displayMode = RegisterCommand.DisplayMode.SHOW_ONCE; // デフォルト値
            }
        } else {
            System.out.println("Configuration file not found, creating new...");
            json = new JsonObject();
            displayMode = RegisterCommand.DisplayMode.SHOW_ONCE;
            resetCustomMessages();
        }
    }

    public static void saveConfig() {
        try {
            if (json == null) {
                json = new JsonObject();
            }
            if (displayMode != null) {
                json.addProperty("displayMode", displayMode.name().toUpperCase());
            } else {
                System.err.println("displayMode is null. Defaulting to SHOW_ONCE during save.");
                json.addProperty("displayMode", RegisterCommand.DisplayMode.SHOW_ONCE.name()); // デフォルト値
            }
            JsonObject messages = new JsonObject();
            for (Map.Entry<Integer, String> entry : customMessages.entrySet()) {
                messages.addProperty(String.valueOf(entry.getKey()), entry.getValue());
            }
            json.add("custom_messages", messages);
            json.addProperty("hard_mode_enabled", hardModeEnabled);
            json.addProperty("set_volume", volume);
            if (CONFIG_PATH.getParent() != null) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            Files.writeString(CONFIG_PATH, GSON.toJson(json));
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public static void setDisplayMode(RegisterCommand.DisplayMode mode) {
        displayMode = mode;
        saveConfig();
    }

    public static String getCustomMessage(int hour) {
        return customMessages.getOrDefault(hour, DEFAULT_MESSAGES.getOrDefault(hour, ""));
    }

    public static void setCustomMessage(int hour, String message) {
        customMessages.put(hour, message);
        saveConfig();
    }

    public static void resetCustomMessages() {
        customMessages = new HashMap<>(DEFAULT_MESSAGES);
        saveConfig();
    }

    public static void setVolume(int setvolume) {
        volume = setvolume;
        saveConfig();
    }
}
