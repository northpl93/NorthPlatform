package pl.arieals.minigame.elytrarace.arena.meta;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;

public class RaceMetaHandler implements IFinishHandler
{
    private int place; // uzywane w RACE_MODE do okreslania miejsca gracza

    @Override
    public void handle(final LocalArena arena, final Player player)
    {
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        playerData.setFinished(true);

        final int playerPlace = this.place + 1;
        this.place = playerPlace;

        player.sendMessage("Zajales " + playerPlace + " miejsce!");

        if (IFinishHandler.checkFinished(arena))
        {
            arena.setGamePhase(GamePhase.POST_GAME);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("place", this.place).toString();
    }
}
