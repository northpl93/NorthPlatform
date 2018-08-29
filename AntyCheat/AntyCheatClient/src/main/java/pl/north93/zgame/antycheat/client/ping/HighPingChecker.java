package pl.north93.zgame.antycheat.client.ping;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.impl.TimelineManager;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

@Slf4j
public class HighPingChecker implements Runnable
{
    private static final int CHECK_INTERVAL = 20;
    private static final int MAX_PING       = 375; // 3/8 sekundy, 1/2 sekundy to troche za duzo
    @Inject
    private TimelineManager timelineManager;
    @Inject
    private MiniGameServer  miniGameServer;
    @Inject @Messages("AntyCheatClient")
    private MessagesBox     messages;

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

            // krótko po spawnie ping moze wariowac; nie wyrzucamy wtedy graczy
            if (playerTickInfo.isShortAfterSpawn())
            {
                continue;
            }

            // jesli ping jest nizszy lub równy naszemu limitowi to nie wyrzucamy gracza
            if (this.calculateAvgPing(timeline) <= MAX_PING)
            {
                continue;
            }

            this.doKickPlayer(player);
        }
    }

    private void doKickPlayer(final Player player)
    {
        log.info("Kicking {} due too big ping", player.getName());

        final IServerManager serverManager = this.miniGameServer.getServerManager();
        if (serverManager instanceof GameHostManager)
        {
            player.sendMessage(this.messages.getComponent(player.getLocale(), "ping.kick_to_hub"));
            final GameHostManager gameHostManager = (GameHostManager) serverManager;

            final String hubId = gameHostManager.getMiniGameConfig().getHubId();
            serverManager.tpToHub(Collections.singletonList(player), hubId);
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
