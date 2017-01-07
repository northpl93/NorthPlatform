package pl.north93.zgame.datashare.sharedimpl.basemcdata;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.datashare.api.data.IDataUnitSerialization;

public class BaseMcDataSerialization implements IDataUnitSerialization<BaseMcDataContainer>
{
    @Override
    public BaseMcDataContainer toRedis(final Player player)
    {
        final BaseMcDataContainer container = new BaseMcDataContainer();

        container.setInventory(VersionDepend.serializePlayerInventory(player));
        container.setEnderchest(VersionDepend.serializePlayerEnderchest(player));
        container.setHeldItemSlot(player.getInventory().getHeldItemSlot());

        container.setHealth(player.getHealth());

        container.setFoodLevel(player.getFoodLevel());
        container.setExhaustion(player.getExhaustion());
        container.setSaturation(player.getSaturation());

        container.setStatistics(VersionDepend.serializePlayerStatistics(player));
        container.setGameMode(player.getGameMode().ordinal());

        return container;
    }

    @Override
    public void fromRedis(final Player player, final BaseMcDataContainer dataUnit)
    {
        VersionDepend.deserializePlayerInventory(player, dataUnit.getInventory());
        VersionDepend.deserializePlayerEnderchest(player, dataUnit.getEnderchest());
        player.getInventory().setHeldItemSlot(dataUnit.getHeldItemSlot());

        player.setHealth(dataUnit.getHealth());

        player.setFoodLevel(dataUnit.getFoodLevel());
        player.setExhaustion(dataUnit.getExhaustion());
        player.setSaturation(dataUnit.getSaturation());

        VersionDepend.deserializePlayerStatistics(player, dataUnit.getStatistics());
        if (player.getGameMode().ordinal() != dataUnit.getGameMode())
        {
            ((BukkitApiCore) API.getApiCore()).sync(() -> player.setGameMode(GameMode.values()[dataUnit.getGameMode()]));
        }
    }
}
