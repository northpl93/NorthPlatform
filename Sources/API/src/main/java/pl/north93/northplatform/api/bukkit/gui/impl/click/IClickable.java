package pl.north93.northplatform.api.bukkit.gui.impl.click;

import java.util.List;

/**
 * Reprezentuje kliknięty element, np. przycisk.
 */
public interface IClickable
{
    List<IClickHandler> getClickHandlers();
}
