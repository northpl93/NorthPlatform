package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import java.util.Collection;

import pl.north93.zgame.api.bukkit.gui.Gui;

interface IDynamicRenderer
{
    Collection<DynamicElementData> render();
    
    static IDynamicRenderer of(Gui gui, String dynamicRenderer)
    {
        if ( dynamicRenderer.startsWith("northplatform://") )
        {
            return new NorthUriDynamicRenderer(dynamicRenderer);
        }
        
        return new MethodDynamicRenderer(gui, dynamicRenderer);
    }
}