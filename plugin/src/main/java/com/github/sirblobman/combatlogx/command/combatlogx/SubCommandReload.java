package com.github.sirblobman.combatlogx.command.combatlogx;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

public final class SubCommandReload extends CombatLogCommand {
    public SubCommandReload(@NotNull ICombatLogX plugin) {
        super(plugin, "reload");
        setPermissionName("combatlogx.command.combatlogx.reload");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        try {
            ICombatLogX plugin = getCombatLogX();
            plugin.onReload();

            sendMessageWithPrefix(sender, "command.combatlogx.reload-success");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessageWithPrefix(sender, "command.combatlogx.reload-failure");
            return true;
        }
    }
}
