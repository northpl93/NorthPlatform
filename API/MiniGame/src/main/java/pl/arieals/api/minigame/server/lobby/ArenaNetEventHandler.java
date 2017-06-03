package pl.arieals.api.minigame.server.lobby;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.event.IncomingArenaNetEvent;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.SubscriptionHandler;

public class ArenaNetEventHandler implements SubscriptionHandler
{
    @Inject
    private TemplateManager msgPack;

    @Override
    public void handle(final String channel, final byte[] message)
    {
        final IArenaNetEvent event = this.msgPack.deserialize(IArenaNetEvent.class, message);
        Bukkit.getPluginManager().callEvent(new IncomingArenaNetEvent(event));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
