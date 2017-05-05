package pl.arieals.api.minigame.server.gamehost.region;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.utils.region.IRegion;

public interface ITrackedRegion
{
    IRegion getRegion();

    void unTrack();

    ITrackedRegion whenEnter(Consumer<Player> player);

    ITrackedRegion whenLeave(Consumer<Player> player);
}
