package org.busybee.solaritychat.format;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.busybee.solaritychat.SolarityChat;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFormatter {

    private final SolarityChat plugin;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_PATTERN_ALT = Pattern.compile("&x&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])");

    public ChatFormatter(SolarityChat plugin) {
        this.plugin = plugin;
    }

    public BaseComponent[] formatMessage(Player player, String message, FormatManager.ChatFormat format) {
        ComponentBuilder builder = new ComponentBuilder("");

        String playerName = player.getName();
        String displayName = player.getDisplayName();
        
        String tagDisplay = "";
        String equippedTag = plugin.getTagManager().getEquippedTag(player.getUniqueId());
        if (equippedTag != null) {
            String tag = plugin.getTagManager().getTagDisplay(equippedTag);
            if (tag != null) {
                tagDisplay = tag;
            }
        }

        if (format.getPrefix() != null) {
            appendComponent(builder, format.getPrefix(), player, playerName, displayName, tagDisplay, message);
        }

        if (format.getNameComponent() != null) {
            appendComponent(builder, format.getNameComponent(), player, playerName, displayName, tagDisplay, message);
        }

        if (format.getSuffix() != null) {
            appendComponent(builder, format.getSuffix(), player, playerName, displayName, tagDisplay, message);
        }

        if (format.getMessage() != null) {
            appendComponent(builder, format.getMessage(), player, playerName, displayName, tagDisplay, message);
        }

        return builder.create();
    }

    private void appendComponent(ComponentBuilder builder, FormatManager.FormatComponent component, 
                                 Player player, String playerName, String displayName, String tag, String message) {
        String text = component.getText();
        
        text = replacePlaceholders(text, player, playerName, displayName, tag, message);
        
        text = format(text);

        BaseComponent[] textComponents = TextComponent.fromLegacyText(text);

        if (!component.getHover().isEmpty()) {
            StringBuilder hoverText = new StringBuilder();
            for (String line : component.getHover()) {
                line = replacePlaceholders(line, player, playerName, displayName, tag, message);
                line = format(line);
                if (hoverText.length() > 0) {
                    hoverText.append("\n");
                }
                hoverText.append(line);
            }
            
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                TextComponent.fromLegacyText(hoverText.toString()));
            
            for (BaseComponent comp : textComponents) {
                comp.setHoverEvent(hoverEvent);
            }
        }

        if (!component.getClick().isEmpty()) {
            String clickAction = component.getClick();
            clickAction = replacePlaceholders(clickAction, player, playerName, displayName, tag, message);
            
            ClickEvent clickEvent = null;
            if (clickAction.startsWith("SUGGEST_COMMAND:")) {
                clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickAction.substring(16));
            } else if (clickAction.startsWith("RUN_COMMAND:")) {
                clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickAction.substring(12));
            } else if (clickAction.startsWith("OPEN_URL:")) {
                clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, clickAction.substring(9));
            } else if (clickAction.startsWith("suggest:")) {
                clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickAction.substring(8));
            } else if (clickAction.startsWith("run:")) {
                clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickAction.substring(4));
            } else if (clickAction.startsWith("url:")) {
                clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, clickAction.substring(4));
            }
            
            if (clickEvent != null) {
                for (BaseComponent comp : textComponents) {
                    comp.setClickEvent(clickEvent);
                }
            }
        }
        
        for (BaseComponent comp : textComponents) {
            builder.append(comp);
        }
    }

    private String replacePlaceholders(String text, Player player, String playerName, String displayName, String tag, String message) {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        
        text = text.replace("%player%", org.bukkit.ChatColor.WHITE + playerName);
        text = text.replace("%player_name%", org.bukkit.ChatColor.WHITE + playerName);
        text = text.replace("%displayname%", displayName);
        text = text.replace("%player_displayname%", displayName);
        text = text.replace("%tag%", tag);
        text = text.replace("%message%", message);
        
        return text;
    }

    public String translateHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString());
        }
        matcher.appendTail(buffer);
        
        String result = buffer.toString();
        
        Matcher altMatcher = HEX_PATTERN_ALT.matcher(result);
        buffer = new StringBuffer(result.length() + 32);
        while (altMatcher.find()) {
            String hexCode = altMatcher.group(1) + altMatcher.group(2) + altMatcher.group(3) + 
                           altMatcher.group(4) + altMatcher.group(5) + altMatcher.group(6);
            altMatcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString());
        }
        altMatcher.appendTail(buffer);
        
        return buffer.toString();
    }

    public String format(String message) {
        String text = translateHexColors(message);
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
