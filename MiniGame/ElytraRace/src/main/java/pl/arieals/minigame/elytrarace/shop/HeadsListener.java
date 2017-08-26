package pl.arieals.minigame.elytrarace.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class HeadsListener implements Listener
{
    @Inject
    private HeadsManagement headsManagement;

    @EventHandler
    public void givePlayerHelmet(final PlayerJoinArenaEvent event)
    {
        final Player player = event.getPlayer();
        player.getInventory().setHelmet(this.headsManagement.getHeadOfPlayer(player));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
