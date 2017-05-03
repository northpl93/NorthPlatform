package pl.arieals.api.minigame.server.gamehost;

import static pl.north93.zgame.api.global.exceptions.SingletonException.checkSingleton;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public final class MiniGameApi
{
    private static MiniGameApi INSTANCE;
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    MiniGameApi() // package-local constructor
    {
        checkSingleton(INSTANCE, "INSTANCE");
        INSTANCE = this;
    }

    public static LocalArena getArena(final Player player)
    {
        final GameHostManager manager = INSTANCE.server.getServerManager();
        return manager.getArenaManager().getArenaAssociatedWith(player.getUniqueId()).orElse(null);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
