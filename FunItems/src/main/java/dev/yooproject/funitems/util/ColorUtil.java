package dev.yooproject.funitems.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * Форматирование текста и строк майнкрафт цветами
     * Поддерживает & коды и HEX (#RRGGBB)
     */
    public static String translate(String message) {
        if (message == null) return "";
        String colored = ChatColor.translateAlternateColorCodes('&', message);
        Matcher matcher = HEX_PATTERN.matcher(colored);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group();
            StringBuilder hexCode = new StringBuilder("§x");
            for (char c : hex.substring(1).toCharArray()) {
                hexCode.append('§').append(c);
            }
            matcher.appendReplacement(sb, hexCode.toString());
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Преобразует строку с цветами в Component (Adventure)
     */
    public static Component toComponent(String message) {
        if (message == null) return Component.empty();

        String translated = translate(message);
        return Component.text(translated);
    }
}
