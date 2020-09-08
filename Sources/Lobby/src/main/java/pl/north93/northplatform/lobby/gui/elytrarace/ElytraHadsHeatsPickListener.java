package pl.north93.northplatform.lobby.gui.elytrarace;

import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.event.ItemMarkedActiveEvent;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

/**
 * Jesli gracz wybierze cos w czapkach to resetujemy wybor w glowach i na odwrot.
 */
public class ElytraHadsHeatsPickListener implements AutoListener
{
    @Inject
    private IGlobalShops globalShops;

    @EventHandler
    public void onHeadOrHatPicked(final ItemMarkedActiveEvent event)
    {
        if (event.getItem() == null)
        {
            // jesli uzywane jest resetowaniu (lub jak to my resetujemy)
            // to nic nie robimy
            return;
        }

        final String groupId = event.getGroup().getId();
        if (groupId.equals("elytra_hats"))
        {
            event.getContainer().resetActiveItem(this.globalShops.getGroup("elytra_heads"));
        }
        else if (groupId.equals("elytra_heads"))
        {
            event.getContainer().resetActiveItem(this.globalShops.getGroup("elytra_hats"));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
