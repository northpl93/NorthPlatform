package pl.arieals.minigame.bedwars.shop.specialentry;

import java.util.Collection;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.utils.TeamArmorUtils;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemTransaction;

public class WoolTeamColor implements IShopSpecialEntry
{
    @Override
    public boolean buy(final INorthPlayer player, final Collection<ItemStack> items)
    {
        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            return false;
        }

        final DyeColor color = TeamArmorUtils.chatColorToDyeColor(playerData.getTeam().getColor());
        for (final ItemStack item : items)
        {
            if (item.getType() != Material.WOOL)
            {
                continue;
            }

            final Wool data = (Wool) item.getData();
            data.setColor(color);
            item.setDurability(data.getData()); // bukkit jest dziwny i setData(data) nie dziala
        }

        return ItemTransaction.addItems(player.getInventory(), items);
    }
}
