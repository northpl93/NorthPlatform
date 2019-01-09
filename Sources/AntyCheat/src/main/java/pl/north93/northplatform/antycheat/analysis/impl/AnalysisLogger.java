package pl.north93.northplatform.antycheat.analysis.impl;

import static java.text.MessageFormat.format;

import static pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.antycheat.analysis.FalsePositiveProbability;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
/*default*/ class AnalysisLogger
{
    private static final String  LOG_FORMAT    = "&cP: &e{0} &cV: &e{1} &cFPP: &e{2}\n&cD: &7{3}";
    private static final boolean DEBUG_TO_CHAT = false;

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
            log.info(coloured);
        }
    }
}
