package pl.north93.zgame.api.bukkit.gui.impl;

import javax.xml.bind.JAXB;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.reflections.Reflections;

import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlGuiLayout;
import pl.north93.zgame.api.global.API;

public class XmlGuiLayoutRegistry
{
    private static final Map<String, XmlGuiLayout> loadedLayouts = new HashMap<>();
    
    public static XmlGuiLayout getLayout(String name)
    {
        Preconditions.checkArgument(loadedLayouts.containsKey(name), "Gui layout with name " + name + " doesn't exists!");
        return loadedLayouts.get(name);
    }
    
    public static void loadGuiLayouts(ClassLoader cl)
    {
        final Reflections reflections = API.getApiCore().getComponentManager().accessReflections(cl);

        final Collection<String> values = reflections.getStore().get("ResourcesScanner").values();
        final Iterable<String> result = values.stream().filter(name -> name.startsWith("gui") && name.endsWith(".xml")).collect(Collectors.toList());

        for ( String resource : Sets.newHashSet(result) )
        {
            loadGuiLayout(cl, resource);
        }
    }
    
    private static void loadGuiLayout(ClassLoader cl, String path)
    {
        String name = path.substring("gui/".length(), path.length() - ".xml".length());
        XmlGuiLayout layout = JAXB.unmarshal(cl.getResourceAsStream(path), XmlGuiLayout.class);
        loadedLayouts.putIfAbsent(name, layout);
        System.out.println("Loaded layout with name " + name);
    }
}
