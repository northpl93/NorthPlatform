package pl.north93.zgame.antycheat.analysis.impl;

import static java.text.MessageFormat.format;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.util.Collection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.FalsePositiveProbability;
import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/*default*/ class AnalysisLogger
{
    private static final String  LOG_FORMAT    = "&cP: &e{0} &cV: &e{1} &cFPP: &e{2}\n&cD: &7{3}";
    private static final boolean DEBUG_TO_CHAT = false;
    @Inject
    private Logger logger;

    @Bean
    private AnalysisLogger()
    {
    }

    public void print(final Player player, final SingleAnalysisResult singleAnalysisResult)
    {
        final Collection<SingleAnalysisResult.ViolationEntry> violations = singleAnalysisResult.getViolations();
        for (final SingleAnalysisResult.ViolationEntry violation : violations)
        {
            final FalsePositiveProbability probability = violation.getFalsePositiveProbability();
            if (probability == FalsePositiveProbability.DEFINITELY)
            {
                // ukrywamy definitely
                continue;
            }
            final String nick = player.getName();
            final String violationName = violation.getViolation().name();

            this.logLine(format(LOG_FORMAT, nick, violationName, probability, violation.getDescription()));
        }
    }

    private void logLine(final String message)
    {
        final String coloured = translateAlternateColorCodes(message);
        if (DEBUG_TO_CHAT)
        {
            Bukkit.broadcastMessage(coloured);
        }
        else
        {
            this.logger.info(coloured);
        }
    }
}
