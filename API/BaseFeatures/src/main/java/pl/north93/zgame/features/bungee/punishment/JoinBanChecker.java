package pl.north93.zgame.features.bungee.punishment;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.bungee.proxy.event.HandlePlayerProxyJoinEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.features.global.punishment.AbstractBan;
import pl.north93.zgame.features.global.punishment.BanService;

public class JoinBanChecker implements Listener
{
    @Inject
    private BanService banService;

    @Bean
    private JoinBanChecker(final BungeeApiCore apiCore)
    {
        apiCore.registerListeners(this);
    }

    @EventHandler
    public void checkJoinConditions(final HandlePlayerProxyJoinEvent event)
    {
        final IOnlinePlayer player = event.getValue().get();

        final AbstractBan ban = this.banService.getBan(player.getIdentity());
        if (ban == null || ban.isExpired())
        {
            return;
        }

        final BaseComponent banMessage = this.banService.getBanMessage(ban, player.getMyLocale());
        event.setCancelled(banMessage);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
