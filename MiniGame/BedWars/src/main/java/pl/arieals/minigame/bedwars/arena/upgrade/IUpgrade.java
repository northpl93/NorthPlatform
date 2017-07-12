package pl.arieals.minigame.bedwars.arena.upgrade;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;

public interface IUpgrade
{
    void apply(LocalArena arena, Team team);

    int maxLevel();
}
