package pl.arieals.minigame.elytrarace;

import javax.xml.bind.JAXB;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.minigame.elytrarace.cfg.ElytraConfig;
import pl.arieals.minigame.elytrarace.listener.ArenaEndListener;
import pl.arieals.minigame.elytrarace.listener.ArenaStartListener;
import pl.arieals.minigame.elytrarace.listener.BoostListener;
import pl.arieals.minigame.elytrarace.listener.CheckpointListener;
import pl.arieals.minigame.elytrarace.listener.FinishLineListener;
import pl.arieals.minigame.elytrarace.listener.ModifyListener;
import pl.arieals.minigame.elytrarace.listener.MoveListener;
import pl.arieals.minigame.elytrarace.listener.ScoreListener;
import pl.arieals.minigame.elytrarace.listener.ScoreboardListener;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

public class ElytraRaceComponent extends Component
{
    @Inject
    private MiniGameServer server;
    @Inject
    private BukkitApiCore  apiCore;

    @Override
    protected void enableComponent()
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            return; // TODO prevent errors in testing environment
        }
        this.apiCore.registerEvents(
                new ArenaStartListener(),
                new ScoreboardListener(),
                new MoveListener(),
                new ModifyListener(),
                new CheckpointListener(),
                new BoostListener(),
                new ScoreListener(),
                new FinishLineListener(),
                new ArenaEndListener());

        Bukkit.getScheduler().runTaskTimer(this.apiCore.getPluginMain(), new ParticleTask(), 1, 1);

        /*final XmlLocation location1 = new XmlLocation(1, 1, 1, 0, 0);
        final XmlLocation location2 = new XmlLocation(2, 1, 2, 5, 5);
        final ArenaConfig config = new ArenaConfig(Arrays.asList(location1, location2), null);
        JAXB.marshal(config, new File("test.xml"));*/
    }

    @Override
    protected void disableComponent()
    {
    }

    @Bean @Named("Elytra Race time format")
    private SimpleDateFormat timeFormat()
    {
        return new SimpleDateFormat("mm:ss.SSS");
    }

    @Bean
    private ElytraConfig elytraConfig(final ApiCore api)
    {
        return JAXB.unmarshal(api.getFile("MiniGame.ElytraRace.xml"), ElytraConfig.class);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
