package pl.north93.zgame.api.bukkit.gui.element;

import pl.north93.zgame.api.bukkit.gui.GuiCanvas;

public class StaticContainerElement extends ContainerElement
{
    public StaticContainerElement(final int width, final int height)
    {
        super(true, width, height);
    }

    @Override
    protected void renderElements(final GuiCanvas canvas)
    {
        this.getChildren().forEach(child -> child.render(canvas));
    }
}
