package pl.arieals.minigame.bedwars.utils;

import java.util.function.Predicate;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public final class PlayerTeamPredicates
{
    private PlayerTeamPredicates()
    {
    }

    public static Predicate<INorthPlayer> isInTeam(final Team team)
    {
        return player ->
        {
            final BedWarsPlayer data = player.getPlayerData(BedWarsPlayer.class);
            return data != null && data.getTeam() == team;
        };
    }

    public static Predicate<INorthPlayer> notInTeam(final Team team)
    {
        return player ->
        {
            final BedWarsPlayer data = player.getPlayerData(BedWarsPlayer.class);
            return data == null || data.getTeam() != team;
        };
    }
}
