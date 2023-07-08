package com.github.sirblobman.combatlogx.api.placeholder;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.minimessage.MiniMessage;
import com.github.sirblobman.api.shaded.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * You must override all required methods and at least one of the following methods:
 * <ul>
 * <li>{@link #getReplacementString(Player, List, String)}</li>
 * <li>{@link #getReplacement(Player, List, String)}</li>
 * </ul>
 * <p>
 * If you do not override at least one, you will get an infinite loop.
 */
public interface IPlaceholderExpansion extends ICombatLogXNeeded {
    @NotNull String getId();

    default @Nullable String getReplacementString(@NotNull Player player, @NotNull List<Entity> enemyList,
                                                  @NotNull String placeholder) {
        Component replacement = getReplacement(player, enemyList, placeholder);
        if (replacement == null || Component.empty().equals(replacement)) {
            return "";
        }

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        return serializer.serialize(replacement);
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    default @Nullable Component getReplacement(@NotNull Player player, @NotNull List<Entity> enemyList,
                                               @NotNull String placeholder) {
        String string = getReplacementString(player, enemyList, placeholder);
        if (string == null || string.isEmpty()) {
            return Component.empty();
        }

        if (string.contains("\u00A7")) {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
            return serializer.deserialize(string);
        }

        if (string.contains("&")) {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
            return serializer.deserialize(string);
        }

        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        MiniMessage miniMessage = languageManager.getMiniMessage();
        return miniMessage.deserialize(string);
    }
}
