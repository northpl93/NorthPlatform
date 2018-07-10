package pl.arieals.api.minigame.server.gamehost.region;

import java.util.function.Consumer;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.region.IRegion;

public interface ITrackedRegion
{
    IRegion getRegion();

    void unTrack();

    ITrackedRegion whenEnter(Consumer<INorthPlayer> handler);

    ITrackedRegion whenLeave(Consumer<INorthPlayer> handler);
}
