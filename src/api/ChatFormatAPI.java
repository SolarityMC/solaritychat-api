package org.busybee.solaritychat.api;

import net.md_5.bungee.api.chat.BaseComponent;
import org.busybee.solaritychat.SolarityChat;
import org.busybee.solaritychat.format.ChatFormatter;
import org.busybee.solaritychat.format.FormatManager;
import org.bukkit.entity.Player;

public class ChatFormatAPI {

    private static ChatFormatAPI instance;

    private FormatManager formatManager;
    private ChatFormatter chatFormatter;

    private ChatFormatAPI() {
        // Private constructor for singleton
    }

    public static ChatFormatAPI getInstance() {
        if (instance == null) {
            instance = new ChatFormatAPI();
        }
        return instance;
    }

    public void initialize(SolarityChat plugin) {
        this.formatManager = plugin.getFormatManager();
        this.chatFormatter = plugin.getChatFormatter();
    }

    public FormatManager.ChatFormat getChatFormat(Player player) {
        if (formatManager == null) {
            return null;
        }
        return formatManager.getFormat(player);
    }

    public BaseComponent[] formatMessage(Player player, String message) {
        if (formatManager == null || chatFormatter == null) {
            return null;
        }
        FormatManager.ChatFormat format = getChatFormat(player);
        if (format == null) {
            return null;
        }
        return chatFormatter.formatMessage(player, message, format);
    }

    public ChatFormatter getChatFormatter() {
        return chatFormatter;
    }

    public FormatManager getFormatManager() {
        return formatManager;
    }
}