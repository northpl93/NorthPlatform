package pl.arieals.minigame.bedwars.listener;

import com.destroystokyo.paper.Title;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.minigame.bedwars.event.PlayerEliminatedEvent;
import pl.arieals.minigame.bedwars.hotbar.SpectatorHotbar;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

@Slf4j
public class PlayerEliminationListener implements Listener
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    @EventHandler
    public void onPlayerEliminated(final PlayerEliminatedEvent event)
    {
        final Player player = event.getPlayer();

        log.info("Player eliminated event called for {} on arena {}", player.getName(), event.getArena().getId());
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
        final BaseComponent title = this.messages.getComponent(locale, "die.norespawn.title");
        final BaseComponent subtitle = this.messages.getComponent(locale, "die.norespawn.subtitle");
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
