package pl.arieals.lobby.chest.loot;

import static pl.arieals.lobby.chest.loot.XmlGuiElementReflect.getOnClick;
import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.north93.zgame.api.bukkit.gui.impl.XmlLayoutRegistry;
import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlButtonElement;
import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlContainerElement;
import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlGuiElement;
import pl.north93.zgame.api.bukkit.gui.impl.xml.XmlGuiLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

/**
 * Klasa wyszukujaca w plikach konfiguracyjnych GUI sklepu
 * ikony dla podanego {@link Item}.
 * <p>
 *     Ta klasa korzysta z wewnetrznej implementacji API GUI znajdujacego sie w paczce
 *     {@code pl.north93.zgame.api.bukkit.gui.impl}.
 */
public class ShopIconFinder
{
    private final IconMappingsXml iconMappingsXml;

    @Bean
    private ShopIconFinder()
    {
        final URL file = ShopIconFinder.class.getResource("/ItemIconMapping.xml");
        this.iconMappingsXml = JAXB.unmarshal(file, IconMappingsXml.class);
    }

    /**
     * Probuje wyszukac ikone dla podanego przedmiotu na podstawie mapowan z
     * pliku {@code ItemIconMapping.xml} i plikow z katalogu {@code gui}.
     * <p>
     * Gdy nie znajdzie poprawnej ikony zwroci bariere (jako ikone bledu).
     *
     * @param item Item dla ktorego szukamy ikony z sklepu.
     * @return Znaleziona ikona.
     */
    public ItemStack getItemStack(final Item item)
    {
        final Collection<String> guiListForGroup = this.getGuiListForGroup(item.getGroup());
        for (final String guiName : guiListForGroup)
        {
            final ItemStack itemStack = this.tryGetIconFromShopGui(guiName, item);
            if (itemStack != null)
            {
                return itemStack;
            }
        }

        return new ItemStack(Material.BARRIER); // fallback
    }

    // zwraca liste gui w ktorych nalezy szukac ikony dla danej grupy
    private @Nonnull Collection<String> getGuiListForGroup(final ItemsGroup itemsGroup)
    {
        final String groupId = itemsGroup.getId();

        final Mapping mapping = findInCollection(this.iconMappingsXml.getMappings(), Mapping::getGroupId, groupId);
        if (mapping == null)
        {
            return Collections.emptyList();
        }

        return mapping.getGuiList();
    }

    private @Nullable ItemStack tryGetIconFromShopGui(final String guiName, final Item item)
    {
        final XmlGuiLayout guiLayout = XmlLayoutRegistry.getGuiLayout(this.getClass().getClassLoader(), guiName);
        return this.scanContentForItem(guiLayout.getContent(), item);
    }

    private boolean itemMatches(final Item item, final XmlButtonElement buttonElement)
    {
        for (final String onClick : getOnClick(buttonElement))
        {
            if (StringUtils.contains(onClick, item.getId()))
            {
                return true;
            }
        }
        return false;
    }

    private ItemStack scanContentForItem(final List<XmlGuiElement> elements, final Item item)
    {
        for (final XmlGuiElement element : elements)
        {
            if (element instanceof XmlButtonElement)
            {
                final XmlButtonElement button = (XmlButtonElement) element;

                if (! this.itemMatches(item, button))
                {
                    continue;
                }

                return button.getIcon().createItemStack();
            }
            else if (element instanceof XmlContainerElement)
            {
                final XmlContainerElement container = (XmlContainerElement) element;
                final List<XmlGuiElement> content = XmlGuiElementReflect.getContent(container);

                // rekursywnie przeszukujemy kolejne kontenery
                return this.scanContentForItem(content, item);
            }
            // potrzebujemy obsluge conditional element?
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("iconMappingsXml", this.iconMappingsXml).toString();
    }
}

class XmlGuiElementReflect
{
    private static final MethodHandle content;
    private static final MethodHandle onClick;

    static
    {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try
        {
            final Class<XmlGuiElement> clazz = XmlGuiElement.class;

            final Field fieldContent = clazz.getDeclaredField("content");
            fieldContent.setAccessible(true);
            content = lookup.unreflectGetter(fieldContent);

            final Field fieldOnClick = clazz.getDeclaredField("onClick");
            fieldOnClick.setAccessible(true);
            onClick = lookup.unreflectGetter(fieldOnClick);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static List<XmlGuiElement> getContent(final XmlGuiElement xmlGuiElement)
    {
        try
        {
            //noinspection unchecked
            return (List<XmlGuiElement>) content.invokeExact(xmlGuiElement);
        }
        catch (final Throwable throwable)
        {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }

    public static List<String> getOnClick(final XmlGuiElement xmlGuiElement)
    {
        try
        {
            //noinspection unchecked
            return (List<String>) onClick.invokeExact(xmlGuiElement);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }
}

@XmlRootElement(name = "iconMappings")
@XmlAccessorType(XmlAccessType.FIELD)
class IconMappingsXml
{
    @XmlElement(name = "mapping")
    private List<Mapping> mappings = new ArrayList<>();

    public List<Mapping> getMappings()
    {
        return this.mappings;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mappings", this.mappings).toString();
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Mapping
{
    @XmlAttribute
    private String       groupId;
    @XmlElement(name = "gui")
    private List<String> guiList = new ArrayList<>();

    public String getGroupId()
    {
        return this.groupId;
    }

    public List<String> getGuiList()
    {
        return this.guiList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("groupId", this.groupId).append("guiList", this.guiList).toString();
    }
}