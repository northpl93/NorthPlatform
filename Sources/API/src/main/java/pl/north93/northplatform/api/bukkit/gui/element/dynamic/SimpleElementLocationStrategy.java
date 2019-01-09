package pl.north93.northplatform.api.bukkit.gui.element.dynamic;

import java.util.Collection;

import pl.north93.northplatform.api.bukkit.gui.GuiCanvas;
import pl.north93.northplatform.api.bukkit.gui.element.ButtonElement;

public class SimpleElementLocationStrategy implements ElementLocationStrategy
{
    @Override
    public void addItems(final GuiCanvas canvas, final Collection<ButtonElement> elements)
    {
        final int width = canvas.getWidth();

        int currentX = - 1;
        int currentY = 0;

        for (final ButtonElement element : elements)
        {
            if (currentX++ >= width)
            {
                currentY++;
                currentX = 0;
            }

            element.setPosition(currentX, currentY);
            canvas.setEntry(currentX, currentY, element.getIcon(), element);
        }
    }
}
