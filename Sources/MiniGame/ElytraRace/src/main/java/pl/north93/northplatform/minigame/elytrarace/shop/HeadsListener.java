package pl.north93.northplatform.minigame.elytrarace.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;

public class HeadsListener implements AutoListener
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
