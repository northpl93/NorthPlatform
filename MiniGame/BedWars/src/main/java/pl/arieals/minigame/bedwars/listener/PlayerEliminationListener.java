package pl.arieals.minigame.bedwars.listener;

import com.destroystokyo.paper.Title;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.arieals.minigame.bedwars.event.PlayerEliminatedEvent;
import pl.arieals.minigame.bedwars.hotbar.SpectatorHotbar;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class PlayerEliminationListener implements Listener
{
    private final Logger logger = LoggerFactory.getLogger(PlayerEliminationListener.class);
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @EventHandler
    public void onPlayerEliminated(final PlayerEliminatedEvent event)
    {
        final Player player = event.getPlayer();

        this.logger.info("Player eliminated event called for {0} on arena {1}", player.getName(), event.getArena().getId());
        if (event.getBedWarsPlayer().isOffline())
        {
            // gracz nie jest online gdy zostal wyeliminowany
            return;
        }

        // resetujemy maksymalne zdrowie ktore moglo zostac zmienione przez ulepszenie
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        // czyscimy ekwipunek, nie jest potrzebny spectatorowi
        player.getInventory().clear();

        final String locale = player.getLocale();
        final String title = this.messages.getMessage(locale, "die.norespawn.title");
        final String subtitle = this.messages.getMessage(locale, "die.norespawn.subtitle");
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
