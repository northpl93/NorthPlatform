package pl.north93.northplatform.discord.presence;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

public class PresenceConfiguration
{
    @Bean
    private PresenceConfiguration(final JDA jda)
    {
        jda.getPresence().setGame(Game.playing("McPiraci.pl"));
    }
}
