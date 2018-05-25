package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import java.util.Collection;

import pl.north93.zgame.api.bukkit.gui.GuiCanvas;
import pl.north93.zgame.api.bukkit.gui.element.ButtonElement;

public interface ElementLocationStrategy
{
    void addItems(GuiCanvas canvas, Collection<ButtonElement> elements);
}
