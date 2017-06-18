package pl.arieals.minigame.elytrarace.arena.finish.race;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IRecordResult;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;

public class RaceMessage
{
    @Inject @Messages("ElytraRace")
    private       MessagesBox          messages;
    @Inject
    private       INetworkManager      network;
    private final List<RaceFinishInfo> finishInfo;
    private final IRecordResult        record;
    private final boolean              isPartial;

    public RaceMessage(final List<RaceFinishInfo> finishInfo, final IRecordResult record, final boolean isPartial)
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

        this.messages.sendMessage(player, "finish.rewards", MessageLayout.CENTER);
        player.sendMessage(" ");

        if (this.isPartial)
        {
            this.messages.sendMessage(player, "finish.wait_awards", MessageLayout.CENTER);
        }
        else
        {

            // todo rewards
        }

        this.messages.sendMessage(player, "separator");
    }

    private void fullResults(final Player player)
    {
        final Iterator<RaceFinishInfo> iterator = this.finishInfo.iterator();
        for (int place = 1; iterator.hasNext() && place < 4; place++)
        {
            final RaceFinishInfo placeInfo = iterator.next();

            this.messages.sendMessage(player, "finish.race.place." + place, MessageLayout.CENTER, placeInfo.getDisplayName(), placeInfo.getTime());
        }
    }

    private void partialResults(final Player player)
    {
        final Iterator<RaceFinishInfo> iterator = this.finishInfo.iterator();
        this.messages.sendMessage(player, "finish.partial_results", MessageLayout.CENTER);
        for (int place = 0; iterator.hasNext() && place < 3; place++)
        {
            final RaceFinishInfo placeInfo = iterator.next();

            this.messages.sendMessage(player, "finish.race.partial_place", MessageLayout.CENTER, placeInfo.getDisplayName(), placeInfo.getTime());
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
        this.messages.sendMessage(player, "finish.race.your_time", MessageLayout.CENTER, this.record.getProcessedRecord().value());
        final IRecord previousGlobal = this.record.previousGlobal();
        if (previousGlobal != null)
        {
            final String recordOwner = this.network.getPlayers().getNickFromUuid(previousGlobal.getOwner());
            this.messages.sendMessage(player, "finish.race.record", MessageLayout.CENTER, recordOwner, previousGlobal.value());
        }
    }
}
