package pl.arieals.minigame.elytrarace.arena.finish.score;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.INetworkManager;

public class ScoreMessage
{
    @Inject @Messages("ElytraRace")
    private       MessagesBox           messages;
    @Inject
    private       INetworkManager       network;
    private final List<ScoreFinishInfo> top;
    private final IRecord<NumberUnit>   record;
    private final boolean               isPartial;

    public ScoreMessage(final List<ScoreFinishInfo> top, final IRecord<NumberUnit> record, final boolean isPartial)
    {
        this.top = top;
        this.record = record;
        this.isPartial = isPartial;
    }

    public void print(final Player player)
    {
        this.messages.sendMessage(player, "separator");
        this.messages.sendMessage(player, "finish.score.header", MessageLayout.CENTER);
        player.sendMessage("");
        if (this.isPartial)
        {
            this.partialResults(player);
        }
        else
        {
            this.fullResults(player);
        }
        this.yourInfo(player);

        player.sendMessage(" ");
        this.messages.sendMessage(player, "separator");
        this.messages.sendMessage(player, "finish.rewards", MessageLayout.CENTER);
        player.sendMessage(" ");

        final LocalArena arena = getArena(player);
        if (this.isPartial)
        {
            this.messages.sendMessage(player, "finish.wait_awards", MessageLayout.CENTER);
        }
        else if (arena != null) // zawsze powinno byc spelnione
        {
            arena.getRewards().renderRewards(this.messages, player);
        }

        this.messages.sendMessage(player, "separator");
    }

    private void fullResults(final Player player)
    {
        final Iterator<ScoreFinishInfo> topIter = this.top.iterator();
        for (int place = 1; topIter.hasNext() && place < 4; place++)
        {
            final ScoreFinishInfo finishInfo = topIter.next();

            this.messages.sendMessage(player, "finish.score.place." + place, MessageLayout.CENTER, finishInfo.getDisplayName(), finishInfo.getPoints());
        }
    }

    private void partialResults(final Player player)
    {
        final Iterator<ScoreFinishInfo> topIter = this.top.iterator();
        this.messages.sendMessage(player, "finish.partial_results", MessageLayout.CENTER);
        for (int place = 0; topIter.hasNext() && place < 3; place++)
        {
            final ScoreFinishInfo finishInfo = topIter.next();

            this.messages.sendMessage(player, "finish.score.partial_place", MessageLayout.CENTER, finishInfo.getDisplayName(), finishInfo.getPoints());
        }
        final int others = this.top.size() - 3;
        if (others > 0)
        {
            this.messages.sendMessage(player, "finish.partial_results_others", MessageLayout.CENTER, others);
        }
    }

    private void yourInfo(final Player player)
    {
        player.sendMessage("");

        final ElytraRacePlayer racePlayer = getPlayerData(player, ElytraRacePlayer.class);
        assert racePlayer != null; // tutaj nie moze byc nullem, bo jakos ukonczyl ta arene...

        final ElytraScorePlayer scorePlayer = racePlayer.asScorePlayer();

        this.messages.sendMessage(player, "finish.score.your_points", MessageLayout.CENTER, scorePlayer.getPoints());
        if (this.record != null)
        {
            final UUID uniqueId = this.record.getHolder().getIdentity().getUuid();
            final String recordOwner = this.network.getPlayers().getNickFromUuid(uniqueId).orElse(uniqueId.toString());
            this.messages.sendMessage(player, "finish.score.record", MessageLayout.CENTER, recordOwner, this.record.getValue().getValue());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("top", this.top).append("record", this.record).append("isPartial", this.isPartial).toString();
    }
}
