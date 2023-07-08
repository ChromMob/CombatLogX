package com.github.sirblobman.combatlogx.api.utility;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.FoliaHelper;
import com.github.sirblobman.api.folia.details.RunnableTask;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

public final class CommandHelper {
    public static void runSync(@NotNull ICombatLogX plugin, @NotNull Runnable runnable) {
        FoliaHelper foliaHelper = plugin.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();
        RunnableTask task = new RunnableTask(plugin.getPlugin(), runnable);
        scheduler.scheduleTask(task);
    }

    public static void runAsConsole(@NotNull ICombatLogX plugin, @NotNull String command) {
        try {
            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        } catch (Exception ex) {
            String messageFormat = "Failed to execute command '/%s' as the server console:";
            String logMessage = String.format(Locale.US, messageFormat, command);

            Logger logger = plugin.getLogger();
            logger.log(Level.SEVERE, logMessage, ex);
        }
    }

    public static void runAsPlayer(@NotNull ICombatLogX plugin, @NotNull Player player, @NotNull String command) {
        try {
            player.performCommand(command);
        } catch (Exception ex) {
            String playerName = player.getName();
            String messageFormat = "Failed to execute command '/%s' as player '%s':";
            String logMessage = String.format(Locale.US, messageFormat, command, playerName);

            Logger logger = plugin.getLogger();
            logger.log(Level.SEVERE, logMessage, ex);
        }
    }

    public static void runAsOperator(@NotNull ICombatLogX plugin, @NotNull Player player, @NotNull String command) {
        if (player.isOp()) {
            runAsPlayer(plugin, player, command);
            return;
        }

        try {
            player.setOp(true);
            player.performCommand(command);
        } catch (Exception ex) {
            String playerName = player.getName();
            String messageFormat = "Failed to execute command '/%s' as player '%s' with operator permissions:";
            String logMessage = String.format(Locale.US, messageFormat, command, playerName);

            Logger logger = plugin.getLogger();
            logger.log(Level.SEVERE, logMessage, ex);
        } finally {
            player.setOp(false);
        }
    }
}
