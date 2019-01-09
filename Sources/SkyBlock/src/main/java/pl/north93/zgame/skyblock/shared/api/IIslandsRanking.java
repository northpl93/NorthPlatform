package pl.north93.zgame.skyblock.shared.api;

import java.util.Set;
import java.util.UUID;

public interface IIslandsRanking
{
    void setPoints(UUID islandId, double points);

    double getPoints(UUID islandId);

    long getPosition(UUID islandId);

    void removeIsland(UUID islandId);

    Set<UUID> getTopIslands(int count);

    void clearRanking();
}
