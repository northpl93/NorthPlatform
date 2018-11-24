package pl.north93.northplatform.antycheat.cheat.other;

import java.nio.charset.StandardCharsets;

import pl.north93.northplatform.antycheat.analysis.FalsePositiveProbability;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.analysis.event.EventAnalyser;
import pl.north93.northplatform.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.northplatform.antycheat.event.impl.PluginMessageTimelineEvent;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;

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

        final SingleAnalysisResult analysisResult = SingleAnalysisResult.create();
        this.checkBrand(analysisResult, event);

        return analysisResult;
    }

    private void checkBrand(final SingleAnalysisResult result, final PluginMessageTimelineEvent event)
    {
        final String brand = new String(event.getData(), StandardCharsets.UTF_8);
        if (! brand.endsWith("vanilla"))
        {
            result.addViolation(OtherViolation.CLIENT_BRAND, brand, FalsePositiveProbability.MEDIUM);
        }
    }
}
