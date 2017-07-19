package pl.north93.zgame.api.bukkit.gui.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXB;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlGuiLayout;
import pl.north93.zgame.api.global.component.impl.JarComponentLoader;

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
        URL jarURL;
        if ( cl instanceof JarComponentLoader )
        {
            jarURL = ((JarComponentLoader) cl).getFileUrl();
        }
        else
        {
            jarURL = XmlGuiLayoutRegistry.class.getProtectionDomain().getCodeSource().getLocation();
        }
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setUrls(jarURL);
        cb.setClassLoaders(new ClassLoader[] {cl});
        cb.setScanners(new ResourcesScanner());
        
        FilterBuilder fb = new FilterBuilder();
        fb.include(FilterBuilder.prefix("gui"));
        cb.filterInputsBy(fb);
        
        Reflections reflections = new Reflections(cb);
        Set<String> resources = reflections.getResources(name -> name.endsWith(".xml"));
        
        for ( String resource : resources )
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
