package pl.north93.zgame.itemshop.controller;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;

@IncludeInScanning("pl.north93.zgame.itemshop.shared")
public class ItemShopController extends Component
{
    @Override
    protected void enableComponent()
    {
        new Handler();
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
