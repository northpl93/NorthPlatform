package pl.north93.zgame.lobby.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.lobby.LobbyFeatures;
import pl.north93.zgame.lobby.config.LobbyConfig;

public class LobbyDevModeCmd extends NorthCommand
{
    @InjectComponent("Lobby.Features")
    private LobbyFeatures component;

    public LobbyDevModeCmd()
    {
        super("lobbydevmode");
        this.setPermission("lobby.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final LobbyConfig lobbyConfig = this.component.getLobbyConfig();
        if (lobbyConfig.devMode)
        {
            lobbyConfig.devMode = false;
            Bukkit.broadcastMessage(ChatColor.RED + "Wyłączono dev mode. Wyjdź i wejdź na serwer. Pamiętaj o tym, że ta opcja nie wprowadza zmian w pliku konfiguracyjnym.");
        }
        else
        {
            lobbyConfig.devMode = true;
            Bukkit.broadcastMessage(ChatColor.RED + "Włączono dev mode. Wyjdź i wejdź na serwer. Pamiętaj o tym, że ta opcja nie wprowadza zmian w pliku konfiguracyjnym.");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("component", this.component).toString();
    }
}
