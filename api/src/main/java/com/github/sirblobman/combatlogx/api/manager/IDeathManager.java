package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;

public interface IDeathManager extends ICombatLogXNeeded {
    /**
     * Track and kill a player.
     * The player will be killed by setting their health to zero.
     *
     * @param player The {@link Player} to kill.
     */
    void kill(@NotNull Player player, @NotNull List<Entity> enemyList);

    /**
     * Check if a player was killed while tracked.
     *
     * @param player The {@link Player} to check.
     * @return {@code true} if the player died from CombatLogX,
     * {@code false} if they were killed by any other reason.
     */
    boolean wasPunishKilled(@NotNull Player player);

    /**
     * Stop tracking a player.
     *
     * @param player The {@link Player} to stop tracking.
     * @return {@code true} if the player was previously being tracked.
     */
    boolean stopTracking(@NotNull Player player);

    /**
     * @param player The player to check.
     * @return A list of tracked enemies for the player.
     */
    @NotNull List<Entity> getTrackedEnemies(@NotNull Player player);
}
