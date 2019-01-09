package pl.north93.northplatform.api.bukkit.gui.impl.click;

import pl.north93.northplatform.api.global.utils.Vars;

/**
 * Przedstawia źródło kliknięcia, GUI lub hotbar.
 */
public interface IClickSource
{
    Vars<Object> getVariables();
}
