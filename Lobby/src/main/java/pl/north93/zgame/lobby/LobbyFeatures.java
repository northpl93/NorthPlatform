package pl.north93.zgame.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.lobby.config.LobbyConfig;
import pl.north93.zgame.lobby.listeners.InventoryListener;
import pl.north93.zgame.lobby.listeners.JoinListener;

public class LobbyFeatures extends Component
{
    private BukkitApiCore      bukkitApiCore;
    private LobbyConfig        lobbyConfig;
    private PlayerPetsManager  playerPetsManager;

    public LobbyConfig getLobbyConfig()
    {
        return this.lobbyConfig;
    }

    public PlayerPetsManager getPlayerPetsManager()
    {
        return this.playerPetsManager;
    }

    @Override
    protected void enableComponent()
    {
        this.bukkitApiCore = (BukkitApiCore) this.getApiCore();
        this.lobbyConfig = ConfigUtils.loadConfigFile(LobbyConfig.class, API.getFile("lobby.yml"));
        this.playerPetsManager = new PlayerPetsManager();
        Bukkit.getPluginManager().registerEvents(this.playerPetsManager, this.bukkitApiCore.getPluginMain());

        if (this.lobbyConfig.infinityDay)
        {
            this.getApiCore().getPlatformConnector().runTaskAsynchronously(() -> Bukkit.getWorlds().forEach(world -> world.setTime(6000)), 2);
        }

        Bukkit.getWorlds().forEach(world -> world.setDifficulty(Difficulty.PEACEFUL)); // disable food lost
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this.bukkitApiCore.getPluginMain());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this.bukkitApiCore.getPluginMain());
    }

    @Override
    protected void disableComponent()
    {

    }
}
