package pl.arieals.minigame.bedwars.listener;

import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.event.PlayerEliminatedEvent;
import pl.arieals.minigame.bedwars.hotbar.SpectatorHotbar;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class PlayerEliminationListener implements Listener
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;
    @Inject
    private Logger      logger;

    @EventHandler
    public void onPlayerEliminated(final PlayerEliminatedEvent event)
    {
        final Player player = event.getPlayer();

        this.logger.log(Level.INFO, "Player eliminated event called for {0} on arena {1}", new Object[]{player.getName(), event.getArena().getId()});

        // resetujemy maksymalne zdrowie ktore moglo zostac zmienione przez ulepszenie
        player.setMaxHealth(20);
        // czyscimy ekwipunek, nie jest potrzebny spectatorowi
        player.getInventory().clear();

        final String locale = player.spigot().getLocale();
        final String title = translateAlternateColorCodes(this.messages.getMessage(locale, "die.norespawn.title"));
        final String subtitle = translateAlternateColorCodes(this.messages.getMessage(locale, "die.norespawn.subtitle"));
        player.sendTitle(new Title(title, subtitle, 20, 20, 20));

        final SpectatorHotbar spectatorHotbar = new SpectatorHotbar();
        spectatorHotbar.display(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
