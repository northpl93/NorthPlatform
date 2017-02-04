package pl.north93.zgame.itemshop.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.itemshop.shared.ReceiveStorage;

@IncludeInScanning("pl.north93.zgame.itemshop.shared")
public class ItemShopServer extends Component
{
    private ReceiveStorage receiveStorage;

    @Override
    protected void enableComponent()
    {
        this.receiveStorage = new ReceiveStorage();
    }

    @Override
    protected void disableComponent()
    {
    }

    public ReceiveStorage getReceiveStorage()
    {
        return this.receiveStorage;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("receiveStorage", this.receiveStorage).toString();
    }
}
