package pl.north93.northplatform.minigame.bedwars.shop.stattrack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class StatTrackItems
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @Bean
    private StatTrackItems()
    {
    }

    public void updateWeapons(final INorthPlayer player, final TrackedWeapon type)
    {
        final StatTrackPlayer playerData = player.getPlayerData(StatTrackPlayer.class);
        if (playerData == null)
        {
            return;
        }

        for (final ItemStack itemStack : player.getInventory().getContents())
        {
            if (itemStack == null || !type.isMatches(itemStack.getType()))
            {
                continue;
            }

            this.updateItem(playerData, type, itemStack);
        }
    }

    public void updateItem(final INorthPlayer player, final ItemStack itemStack)
    {
        final TrackedWeapon trackedWeapon = TrackedWeapon.getByMaterial(itemStack.getType());
        if (trackedWeapon == null)
        {
            return;
        }

        final StatTrackPlayer playerData = player.getPlayerData(StatTrackPlayer.class);
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

        final String playerLocale = playerData.getBukkitPlayer().getLocale();
        final LegacyMessage loreContent = this.messages.getLegacy(playerLocale, "stattrack_lore", kills);

        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(loreContent.asList());
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
