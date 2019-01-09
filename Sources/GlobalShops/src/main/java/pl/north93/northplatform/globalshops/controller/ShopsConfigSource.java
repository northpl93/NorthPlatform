package pl.north93.northplatform.globalshops.controller;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.globalshops.controller.cfg.ItemsGroupCfg;
import pl.north93.northplatform.globalshops.shared.ItemsDataContainer;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.controller.configserver.source.IConfigSource;

public class ShopsConfigSource implements IConfigSource<ItemsDataContainer>
{
    @Inject
    private ApiCore apiCore;

    @Override
    public Class<ItemsDataContainer> getType()
    {
        return ItemsDataContainer.class;
    }

    @Override
    public ItemsDataContainer load()
    {
        final File storage = this.apiCore.getFile("global_shops");
        final File[] files = storage.listFiles();
        if (files == null)
        {
            return new ItemsDataContainer(new ArrayList<>(0));
        }

        return new ItemsDataContainer(Stream.of(files).map(this::loadGroup).collect(Collectors.toList()));
    }

    private ItemsGroupCfg loadGroup(final File file)
    {
        return JAXB.unmarshal(file, ItemsGroupCfg.class);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
