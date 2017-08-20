package pl.arieals.minigame.bedwars.shop.stattrack;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class StatTrackItems
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @Bean
    private StatTrackItems()
    {
    }

    public void updateWeapons(final Player player, final TrackedWeapon type)
    {
        final StatTrackPlayer playerData = getPlayerData(player, StatTrackPlayer.class);

        for (final ItemStack itemStack : player.getInventory().getContents())
        {
            if (itemStack == null || !type.isMatches(itemStack.getType()))
            {
                continue;
            }

            this.updateItem(playerData, type, itemStack);
        }
    }

    public void updateItem(final Player player, final ItemStack itemStack)
    {
        final TrackedWeapon trackedWeapon = TrackedWeapon.getByMaterial(itemStack.getType());
        if (trackedWeapon == null)
        {
            return;
        }

        final StatTrackPlayer playerData = getPlayerData(player, StatTrackPlayer.class);
        if (playerData == null)
        {
            return;
        }

        this.updateItem(playerData, trackedWeapon, itemStack);
    }

    public void updateItem(final StatTrackPlayer playerData, final TrackedWeapon type, final ItemStack itemStack)
    {
        if (! playerData.isEnabled(type))
        {
            return;
        }

        final long kills = playerData.getCachedStatistic(TrackedStatistic.KILLS, type);

        final String playerLocale = playerData.getBukkitPlayer().spigot().getLocale();
        final String loreContent = this.messages.getMessage(playerLocale, "stattrack_lore", kills);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(Arrays.asList(StringUtils.split(loreContent, "\n")));
        itemStack.setItemMeta(itemMeta);
    }

    public void clearItem(final ItemStack itemStack)
    {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(null);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
