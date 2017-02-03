package pl.north93.zgame.api.bukkit.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener
{
    @EventHandler
    public void onSignPlace(final SignChangeEvent event)
    {
        if (! event.getPlayer().hasPermission("sign.colorize"))
        {
            return;
        }

        final String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++)
        {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
        }
    }
}
