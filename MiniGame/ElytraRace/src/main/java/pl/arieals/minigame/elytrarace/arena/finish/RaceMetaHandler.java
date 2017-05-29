package pl.arieals.minigame.elytrarace.arena.finish;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class RaceMetaHandler implements IFinishHandler
{
    @Inject
    @Messages("ElytraRace")
    private MessagesBox messages;
    private int place; // uzywane w RACE_MODE do okreslania miejsca gracza

    @Override
    public void handle(final LocalArena arena, final Player player)
    {
        final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
        if (playerData.isFinished())
        {
            // gracz juz przekroczyl linie mety, wiecej razy nie mozemy go obslugiwac
            return;
        }
        playerData.setFinished(true);

        final int playerPlace = this.place + 1;
        this.place = playerPlace;

        if (playerPlace == 1)
        {
            arena.getPlayersManager().broadcast(
                    this.messages,
                    "race.finish.broadcast_first",
                    player.getDisplayName(),
                    arena.getTimer().humanReadableTimeAfterStart());
        }
        else
        {
            arena.getPlayersManager().broadcast(
                    this.messages,
                    "race.finish.broadcast",
                    player.getDisplayName(),
                    playerPlace,
                    arena.getTimer().humanReadableTimeAfterStart());
        }

        // todo wyswietlic graczowi duży komunikat
        // todo rekordy

        if (IFinishHandler.checkFinished(arena))
        {
            arena.setGamePhase(GamePhase.POST_GAME);
        }
    }

    @Override
    public void gameEnd(final LocalArena arena)
    {
        // todo wywalic top3?
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("place", this.place).toString();
    }
}
