package pl.arieals.minigame.bedwars.arena.upgrade;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;

public interface IUpgrade
{
    default String getName()
    {
        return this.getClass().getSimpleName();
    }

    void apply(LocalArena arena, Team team, int level);

    int maxLevel();
}
