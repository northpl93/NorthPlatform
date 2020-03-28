package pl.north93.northplatform.minigame.elytrarace.arena.finish.race;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.entity.Player;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;

public class RaceMessage
{
    @Inject @Messages("ElytraRace")
    private       MessagesBox           messages;
    @Inject
    private       INetworkManager       network;
    @Inject @Named("Elytra Race time format")
    private       SimpleDateFormat      timeFormat;
    private final List<RaceFinishInfo>  finishInfo;
    private final IRecord<Duration, DurationUnit> record;
    private final boolean               isPartial;

    public RaceMessage(final List<RaceFinishInfo> finishInfo, final IRecord<Duration, DurationUnit> record, final boolean isPartial)
    {
        this.finishInfo = finishInfo;
        this.record = record;
        this.isPartial = isPartial;
    }

    public void print(final Player player)
    {
        this.messages.sendMessage(player, "separator");
        this.messages.sendMessage(player, "finish.race.header", MessageLayout.CENTER);
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
        final Iterator<RaceFinishInfo> iterator = this.finishInfo.iterator();
        for (int place = 1; iterator.hasNext() && place < 4; place++)
        {
            final RaceFinishInfo placeInfo = iterator.next();

            final String formattedTime = this.timeFormat.format(new Date(placeInfo.getTime()));
            this.messages.sendMessage(player, "finish.race.place." + place, MessageLayout.CENTER, placeInfo.getDisplayName(), formattedTime);
        }
    }

    private void partialResults(final Player player)
    {
        final Iterator<RaceFinishInfo> iterator = this.finishInfo.iterator();
        this.messages.sendMessage(player, "finish.partial_results", MessageLayout.CENTER);
        for (int place = 0; iterator.hasNext() && place < 3; place++)
        {
            final RaceFinishInfo placeInfo = iterator.next();

            final String formattedTime = this.timeFormat.format(new Date(placeInfo.getTime()));
            this.messages.sendMessage(player, "finish.race.partial_place", MessageLayout.CENTER, placeInfo.getDisplayName(), formattedTime);
        }
        final int others = this.finishInfo.size() - 3;
        if (others > 0)
        {
            this.messages.sendMessage(player, "finish.partial_results_others", MessageLayout.CENTER, others);
        }
    }

    private void yourInfo(final Player player)
    {
        player.sendMessage("");

        final String formattedTime = this.timeFormat.format(new Date(this.getFinishInfo(player).getTime()));
        this.messages.sendMessage(player, "finish.race.your_time", MessageLayout.CENTER, formattedTime);

        if (this.record != null)
        {
            final UUID uniqueId = this.record.getHolder().getIdentity().getUuid();
            final String recordOwner = this.network.getPlayers().getNickFromUuid(uniqueId).orElse(uniqueId.toString());
            final String formattedRecord = this.timeFormat.format(new Date(this.record.getValue().getValue().toMillis()));
            this.messages.sendMessage(player, "finish.race.record", MessageLayout.CENTER, recordOwner, formattedRecord);
        }
    }

    private RaceFinishInfo getFinishInfo(final Player player)
    {
        for (final RaceFinishInfo raceFinishInfo : this.finishInfo)
        {
            if (raceFinishInfo.getUuid().equals(player.getUniqueId()))
            {
                return raceFinishInfo;
            }
        }
        throw new RuntimeException("Not found " + player.getName() + " in finishInfo in RaceMessage");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("finishInfo", this.finishInfo).append("record", this.record).append("isPartial", this.isPartial).toString();
    }
}
