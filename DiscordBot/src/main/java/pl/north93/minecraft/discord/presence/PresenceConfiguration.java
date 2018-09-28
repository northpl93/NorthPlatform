package pl.north93.minecraft.discord.presence;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class PresenceConfiguration
{
    @Bean
    private PresenceConfiguration(final JDA jda)
    {
        jda.getPresence().setGame(Game.playing("McPiraci.pl"));
    }
}
