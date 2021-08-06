package pl.north93.northplatform.antycheat.client.ping;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.timeline.Tick;
import pl.north93.northplatform.antycheat.timeline.Timeline;
import pl.north93.northplatform.antycheat.timeline.impl.TimelineManager;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.IServerManager;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;

@Slf4j
public class HighPingChecker implements Runnable
{
    private static final int CHECK_INTERVAL = 20;
    private static final int MAX_PING = 375; // 3/8 of second, 1/2 of second is quite too much
    @Inject @Messages("AntyCheatClient")
    private MessagesBox messages;
    @Inject
    private TimelineManager timelineManager;
    @Inject
    private IServerManager serverManager;

    @Bean
    private HighPingChecker(final IBukkitExecutor executor)
    {
        executor.syncTimer(CHECK_INTERVAL, this);
    }

    @Override
    public void run()
    {
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            final Timeline timeline = this.timelineManager.getPlayerTimeline(player);

            final Tick tick = this.timelineManager.getPreviousTick(this.timelineManager.getCurrentTick(), 1);
            final PlayerTickInfo playerTickInfo = timeline.getPlayerTickInfo(tick);

            // shortly after spawn, player's ping is unreliable, don't kick him then
            if (playerTickInfo.isShortAfterSpawn())
            {
                continue;
            }

            // kick player if ping is too high
            if (this.calculateAvgPing(timeline) <= MAX_PING)
            {
                continue;
            }

            this.doKickPlayer(player);
        }
    }

    private void doKickPlayer(final Player player)
    {
        log.info("Kicking {} due too high ping", player.getName());

        if (this.serverManager instanceof GameHostManager)
        {
            player.sendMessage(this.messages.getComponent(player.getLocale(), "ping.kick_to_hub"));
            final GameHostManager gameHostManager = (GameHostManager) this.serverManager;

            final String hubId = gameHostManager.getMiniGameConfig().getHubId();
            gameHostManager.tpToHub(Collections.singletonList(player), hubId);
        }
        else
        {
            player.kickPlayer(this.messages.getString(player.getLocale(), "ping.kick_from_server"));
        }
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
