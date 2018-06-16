package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import java.util.Collection;

import pl.north93.zgame.api.bukkit.gui.impl.NorthUriUtils;
import pl.north93.zgame.api.global.utils.Vars;

class NorthUriDynamicRenderer implements IDynamicRenderer
{
    private final String uri;
    
    public NorthUriDynamicRenderer(String uri)
    {
        this.uri = uri;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Collection<DynamicElementData> render()
    {
        return (Collection<DynamicElementData>) NorthUriUtils.getInstance().call(this.uri, Vars.empty());
    }
}