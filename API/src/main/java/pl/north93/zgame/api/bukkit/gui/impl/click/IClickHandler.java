package pl.north93.zgame.api.bukkit.gui.impl.click;

import pl.north93.zgame.api.bukkit.gui.event.ClickEvent;

/**
 * Reprezentuje akcję do wykonania po kliknięciu.
 */
public interface IClickHandler
{
    void handle(IClickSource source, ClickEvent event);
}
