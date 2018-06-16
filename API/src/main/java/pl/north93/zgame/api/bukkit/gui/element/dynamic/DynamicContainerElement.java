package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiCanvas;
import pl.north93.zgame.api.bukkit.gui.IGuiIcon;
import pl.north93.zgame.api.bukkit.gui.element.ButtonElement;
import pl.north93.zgame.api.bukkit.gui.element.ContainerElement;

public class DynamicContainerElement extends ContainerElement
{
    private final IDynamicRenderer dynamicRenderer;
//    private final String                  dataUri;
    private final ElementLocationStrategy strategy;
    private final Map<String, IGuiIcon>   icons;

    public DynamicContainerElement(final int width, final int height, final String dynamicRenderer, final ElementLocationStrategy strategy,
            final Map<String, IGuiIcon> icons, final Gui gui)
    {
        super(false, width, height);
        this.dynamicRenderer = IDynamicRenderer.of(gui, dynamicRenderer);
        this.strategy = strategy;
        this.icons = icons;
    }

    @Override
    protected void renderElements(final GuiCanvas canvas)
    {
        final Collection<DynamicElementData> elements = dynamicRenderer.render();

        final List<ButtonElement> buttons = elements.stream().map(this::createElement).collect(Collectors.toList());
        this.strategy.addItems(canvas, buttons);
    }

    private ButtonElement createElement(final DynamicElementData elementData)
    {
        final ButtonElement buttonElement;
        
        if ( elementData.getIcon() != null )
        {
            buttonElement = new ButtonElement(elementData.getIcon());
        }
        else
        {
            buttonElement = new ButtonElement(this.icons.get(elementData.getIconCase()));
        }
        
        if ( elementData.getVars() != null )
        {
            buttonElement.addVariables(elementData.getVars());
        }
        
        if ( elementData.getClickHandler() != null )
        {
            buttonElement.getClickHandlers().add(elementData.getClickHandler());
        }
        return buttonElement;
    }

//    @SuppressWarnings("unchecked")
//    private Collection<DynamicElementData> getElements()
//    {
        
        //return (Collection<DynamicElementData>) NorthUriUtils.getInstance().call(this.dataUri, Vars.empty());
//    }
}






