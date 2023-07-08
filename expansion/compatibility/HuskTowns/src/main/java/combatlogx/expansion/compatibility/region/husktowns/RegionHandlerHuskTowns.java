package combatlogx.expansion.compatibility.region.husktowns;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.william278.husktowns.api.HuskTownsAPI;
import net.william278.husktowns.claim.Position;
import net.william278.husktowns.listener.Operation;
import net.william278.husktowns.listener.Operation.Type;
import net.william278.husktowns.user.OnlineUser;

public final class RegionHandlerHuskTowns extends RegionHandler<HuskTownsExpansion> {
    public RegionHandlerHuskTowns(@NotNull HuskTownsExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.husktowns-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        HuskTownsAPI api = HuskTownsAPI.getInstance();

        OnlineUser user = api.getOnlineUser(player);
        if (user == null) {
            return false;
        }

        Position position = api.getPosition(location);
        if (position == null) {
            return false;
        }

        if (tagType == TagType.PLAYER) {
            Operation operation = Operation.of(user, Type.PLAYER_DAMAGE_PLAYER, position, true);
            return !api.isOperationAllowed(operation);
        } else if (tagType == TagType.MOB) {
            Operation operation = Operation.of(user, Type.PLAYER_DAMAGE_ENTITY, position, true);
            return !api.isOperationAllowed(operation);
        }

        return false;
    }
}
