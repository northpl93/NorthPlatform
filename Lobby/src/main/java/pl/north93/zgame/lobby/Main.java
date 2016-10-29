package pl.north93.zgame.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.plugin.java.JavaPlugin;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.lobby.cmd.LobbyDevModeCmd;
import pl.north93.zgame.lobby.config.LobbyConfig;
import pl.north93.zgame.lobby.listeners.InventoryListener;
import pl.north93.zgame.lobby.listeners.JoinListener;

public class Main extends JavaPlugin
{
    private static Main        instance;
    private        LobbyConfig lobbyConfig;
    private PlayerPetsManager  playerPetsManager;

    @Override
    public void onEnable()
    {
        instance = this;
        this.lobbyConfig = ConfigUtils.loadConfigFile(LobbyConfig.class, API.getFile("lobby.yml"));
        this.playerPetsManager = new PlayerPetsManager();

        if (this.lobbyConfig.infinityDay)
        {
            this.getServer().getScheduler().runTaskTimer(this, () -> Bukkit.getWorlds().forEach(world -> world.setTime(6000)), 2, 2);
        }

        this.getServer().getWorlds().forEach(world -> world.setDifficulty(Difficulty.PEACEFUL)); // disable food lost
        this.getServer().getPluginManager().registerEvents(new JoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        this.getCommand("lobbydevmode").setExecutor(new LobbyDevModeCmd());
    }

    public LobbyConfig getLobbyConfig()
    {
        return this.lobbyConfig;
    }

    public PlayerPetsManager getPlayerPetsManager()
    {
        return this.playerPetsManager;
    }

    public static Main getInstance()
    {
        return instance;
    }
}
