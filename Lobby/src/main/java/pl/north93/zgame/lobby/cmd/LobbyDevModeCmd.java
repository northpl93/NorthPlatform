package pl.north93.zgame.lobby.cmd;

import static pl.north93.zgame.api.global.I18n.getBukkitMessage;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.north93.zgame.lobby.Main;
import pl.north93.zgame.lobby.config.LobbyConfig;

public class LobbyDevModeCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
    {
        if (!sender.hasPermission("lobby.admin") && !sender.isOp())
        {
            sender.sendMessage(getBukkitMessage("command.no_permissions"));
            return true;
        }

        final LobbyConfig lobbyConfig = Main.getInstance().getLobbyConfig();
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

        return true;
    }
}
