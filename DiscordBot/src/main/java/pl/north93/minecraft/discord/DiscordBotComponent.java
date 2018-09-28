package pl.north93.minecraft.discord;

import javax.security.auth.login.LoginException;

import java.io.File;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.utils.ConfigUtils;

@Slf4j
public class DiscordBotComponent extends Component
{
    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
        final JDA jda = this.getBean(JDA.class);
        jda.shutdownNow();
    }

    @Bean
    private DiscordBotConfig discordBotConfiguration(final ApiCore apiCore)
    {
        final File configFile = apiCore.getFile("discordbot.xml");
        log.info("Loading Discord bot configuration from {}", configFile.getAbsolutePath());

        return ConfigUtils.loadConfig(DiscordBotConfig.class, configFile);
    }

    @Bean
    private JDA discordConnection(final DiscordBotConfig config) throws LoginException
    {
        log.info("Connecting to Discord...");
        return new JDABuilder(config.getToken()).build();
    }
}
