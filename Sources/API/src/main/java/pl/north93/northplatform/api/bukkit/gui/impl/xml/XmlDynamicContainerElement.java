package pl.north93.northplatform.api.bukkit.gui.impl.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.northplatform.api.bukkit.gui.element.ContainerElement;
import pl.north93.northplatform.api.bukkit.gui.element.dynamic.DynamicContainerElement;
import pl.north93.northplatform.api.bukkit.gui.element.dynamic.ElementLocationStrategy;
import pl.north93.northplatform.api.bukkit.gui.element.dynamic.SimpleElementLocationStrategy;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.IGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.impl.XmlReaderContext;

@XmlRootElement(name = "dynamicContainer")
public class XmlDynamicContainerElement extends XmlStaticContainerElement
{
    @XmlElement
    private String renderer;

    @XmlElement(name = "icon")
    private List<XmlDynamicGuiIcon> icons = new ArrayList<>();

    public String getRenderer()
    {
        return renderer;
    }

    public List<? extends XmlGuiIcon> getIcons()
    {
        return this.icons;
    }

    @Override
    protected ContainerElement toGuiElement0(final XmlReaderContext renderContext)
    {
        final String[] split = this.getSize().split(",");

        final int sizeX = Integer.parseInt(split[0]);
        final int sizeY = Integer.parseInt(split[1]);

        final ElementLocationStrategy strategy = new SimpleElementLocationStrategy();
        if ( icons == null ) icons = new ArrayList<>(); // TODO: remove this
        final Map<String, IGuiIcon> baseIcons = icons.stream().collect(Collectors.toMap(XmlDynamicGuiIcon::getIconCase, icon -> icon.toGuiIcon(renderContext, this.getVariables())));

        final DynamicContainerElement result = new DynamicContainerElement(sizeX, sizeY, renderer, strategy, baseIcons, (Gui) renderContext.getClickSource());
        result.setBackground(this.getBackground() != null ? this.getBackground().toGuiIcon(renderContext, this.getVariables()) : null);
        result.setBorder(this.getBorder() != null ? this.getBorder().toGuiIcon(renderContext, this.getVariables()) : null);

        return result;
    }
}

class XmlDynamicGuiIcon extends XmlGuiIcon
{
    @XmlAttribute(name = "case")
    private String iconCase = "";
    
    public String getIconCase()
    {
        return iconCase;
    }
}

