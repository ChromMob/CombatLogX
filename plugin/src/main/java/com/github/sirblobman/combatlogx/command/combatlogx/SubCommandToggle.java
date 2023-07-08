package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

public final class SubCommandToggle extends CombatLogPlayerCommand {
    public SubCommandToggle(@NotNull ICombatLogX plugin) {
        super(plugin, "toggle");
        setPermissionName("combatlogx.command.combatlogx.toggle");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        if (args.length == 1) {
            return getMatching(args[0], "actionbar", "bossbar", "scoreboard");
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        String sub = args[0].toLowerCase(Locale.US);
        List<String> validToggleList = Arrays.asList("actionbar", "bossbar", "scoreboard");
        if (!validToggleList.contains(sub)) {
            return false;
        }

        toggleValue(player, sub);
        return true;
    }

    private void toggleValue(Player player, String value) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        LanguageManager languageManager = getLanguageManager();

        YamlConfiguration playerData = playerDataManager.get(player);
        boolean currentValue = playerData.getBoolean(value, true);
        playerData.set(value, !currentValue);
        playerDataManager.save(player);

        boolean status = playerData.getBoolean(value, true);
        String statusPath = ("placeholder.toggle." + (status ? "enabled" : "disabled"));
        String statusString = languageManager.getMessageString(player, statusPath);

        Replacer replacer = new StringReplacer("{status}", statusString);
        String messagePath = ("command.combatlogx.toggle-" + value);
        sendMessageWithPrefix(player, messagePath, replacer);
    }
}
