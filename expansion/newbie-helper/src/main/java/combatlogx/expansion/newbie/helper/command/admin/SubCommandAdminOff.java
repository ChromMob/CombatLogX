package combatlogx.expansion.newbie.helper.command.admin;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;

public final class SubCommandAdminOff extends CombatLogCommand {
    private final NewbieHelperExpansion expansion;

    public SubCommandAdminOff(@NotNull NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "off");
        setPermissionName("combatlogx.command.togglepvp.admin.off");
        this.expansion = expansion;
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        Player target = findTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        NewbieHelperExpansion expansion = getExpansion();
        PVPManager pvpManager = expansion.getPVPManager();
        pvpManager.setPVP(target, false);
        pvpManager.sendAdminToggleMessage(sender, target);
        return true;
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}
