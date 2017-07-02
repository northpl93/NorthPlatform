package pl.arieals.minigame.bedwars;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorTask;
import pl.arieals.minigame.bedwars.arena.generator.ItemRotator;
import pl.arieals.minigame.bedwars.listener.ArenaStartListener;
import pl.arieals.minigame.bedwars.listener.BuildListener;
import pl.arieals.minigame.bedwars.listener.DeathListener;
import pl.arieals.minigame.bedwars.listener.PlayerTeamListener;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BedWarsComponent extends Component
{
    @Inject
    private MiniGameServer server;
    @Inject
    private BukkitApiCore  bukkitApi;

    @Override
    protected void enableComponent()
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            return; // TODO prevent errors in testing environment
        }
        this.bukkitApi.registerEvents(
                new ArenaStartListener(),
                new PlayerTeamListener(),
                new BuildListener(),
                new DeathListener());

        // uruchamiamy task generatorów co 1 tick
        new GeneratorTask().runTaskTimer(this.bukkitApi.getPluginMain(), 1, 1);
        new ItemRotator().start(); // uruchamiamy watek obracajacy itemkami
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
