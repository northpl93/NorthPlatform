package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import pl.north93.zgame.api.bukkit.gui.GuiCanvas;
import pl.north93.zgame.api.bukkit.gui.IGuiIcon;
import pl.north93.zgame.api.bukkit.gui.element.ButtonElement;
import pl.north93.zgame.api.bukkit.gui.element.ContainerElement;
import pl.north93.zgame.api.bukkit.gui.impl.NorthUriUtils;
import pl.north93.zgame.api.global.utils.Vars;

public class DynamicContainerElement extends ContainerElement
{
    private final String                  dataUri;
    private final ElementLocationStrategy strategy;
    private final IGuiIcon                icon;

    public DynamicContainerElement(final int width, final int height, final String dataUri, final ElementLocationStrategy strategy, final IGuiIcon icon)
    {
        super(false, width, height);
        this.dataUri = dataUri;
        this.strategy = strategy;
        this.icon = icon;
    }

    @Override
    protected void renderElements(final GuiCanvas canvas)
    {
        final Collection<DynamicElementData> elements = this.getElements();

        final List<ButtonElement> buttons = elements.stream().map(this::createElement).collect(Collectors.toList());
        this.strategy.addItems(canvas, buttons);
    }

    private ButtonElement createElement(final DynamicElementData elementData)
    {
        final ButtonElement buttonElement = new ButtonElement(this.icon);
        buttonElement.addVariables(elementData.getVars());
        buttonElement.getClickHandlers().add(elementData.getClickHandler());

        return buttonElement;
    }

    @SuppressWarnings("unchecked")
    private Collection<DynamicElementData> getElements()
    {
        return (Collection<DynamicElementData>) NorthUriUtils.getInstance().call(this.dataUri, Vars.empty());
    }
}