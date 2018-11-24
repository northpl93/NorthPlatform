package pl.north93.northplatform.antycheat.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.timeline.Tick;
import pl.north93.northplatform.antycheat.timeline.Timeline;
import pl.north93.northplatform.antycheat.timeline.impl.TimelineManager;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class GetPingCommand extends NorthCommand
{
    @Inject
    private TimelineManager timelineManager;

    public GetPingCommand()
    {
        super("getping");
        this.setPermission("antycheat.cmd.getping");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            sender.sendMessage("&c/getping <nick>");
            return;
        }

        final Player player = Bukkit.getPlayer(args.asString(0));
        if (player == null)
        {
            sender.sendMessage("&cNie ma takiego gracza");
            return;
        }

        final Tick tick = this.timelineManager.getPreviousTick(this.timelineManager.getCurrentTick(), 1);
        final Timeline timeline = this.timelineManager.getPlayerTimeline(player);

        final PlayerTickInfo currentPlayerTickInfo = timeline.getPlayerTickInfo(tick);
        sender.sendMessage("&aAktualny ping {0}", currentPlayerTickInfo.getPing());

        final double avgPing = this.calculateAvgPing(timeline);
        sender.sendMessage("&aSredni ping {0}", avgPing);
    }

    private double calculateAvgPing(final Timeline timeline)
    {
        return timeline.getAllTicks().stream().mapToInt(PlayerTickInfo::getPing).average().orElse(0);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
