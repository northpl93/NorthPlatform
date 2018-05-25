package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.gui.IGuiIcon;
import pl.north93.zgame.api.bukkit.gui.element.ContainerElement;
import pl.north93.zgame.api.bukkit.gui.element.dynamic.DynamicContainerElement;
import pl.north93.zgame.api.bukkit.gui.element.dynamic.ElementLocationStrategy;
import pl.north93.zgame.api.bukkit.gui.element.dynamic.SimpleElementLocationStrategy;
import pl.north93.zgame.api.bukkit.gui.impl.XmlReaderContext;

@XmlRootElement(name = "dynamicContainer")
public class XmlDynamicContainerElement extends XmlStaticContainerElement
{
    @XmlElement
    private String dataUri;

    @XmlElement
    private XmlGuiIcon icon;

    public String getDataUri()
    {
        return this.dataUri;
    }

    public XmlGuiIcon getIcon()
    {
        return this.icon;
    }

    @Override
    protected ContainerElement toGuiElement0(final XmlReaderContext renderContext)
    {
        final String[] split = this.getSize().split(",");

        final int sizeX = Integer.parseInt(split[0]);
        final int sizeY = Integer.parseInt(split[1]);

        final ElementLocationStrategy strategy = new SimpleElementLocationStrategy();
        final IGuiIcon baseIcon = this.icon.toGuiIcon(renderContext, this.getVariables());

        final DynamicContainerElement result = new DynamicContainerElement(sizeX, sizeY, this.dataUri, strategy, baseIcon);
        result.setBackground(this.getBackground() != null ? this.getBackground().toGuiIcon(renderContext, this.getVariables()) : null);
        result.setBorder(this.getBorder() != null ? this.getBorder().toGuiIcon(renderContext, this.getVariables()) : null);

        return result;
    }
}
