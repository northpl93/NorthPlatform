package pl.north93.zgame.antycheat.cheat.other;

import java.nio.charset.StandardCharsets;

import org.bukkit.Bukkit;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.PluginMessageTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;

public class ClientBrandChecker implements EventAnalyser<PluginMessageTimelineEvent>
{
    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(PluginMessageTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final PluginMessageTimelineEvent event)
    {
        if (! event.getChannel().equals("MC|Brand"))
        {
            return null;
        }

        final String brand = new String(event.getData(), StandardCharsets.UTF_8);
        if (! brand.endsWith("vanilla"))
        {
            Bukkit.broadcastMessage(data.getPlayer().getName() + " uzywa zmodowanego klienta: " + brand);
        }

        return null;
    }
}
