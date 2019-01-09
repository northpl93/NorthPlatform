package pl.north93.northplatform.datashare.sharedimpl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.datashare.api.IDataShareManager;
import pl.north93.northplatform.datashare.sharedimpl.basemcdata.BaseMcDataPersistence;
import pl.north93.northplatform.datashare.sharedimpl.basemcdata.BaseMcDataSerialization;

public class PlayerDataShareComponent extends Component
{
    private IDataShareManager dataShareManager;

    @Override
    protected void enableComponent()
    {
        this.dataShareManager = new DataShareManagerImpl();
        this.dataShareManager.registerDataUnit("basemcdata", new BaseMcDataSerialization(), new BaseMcDataPersistence());
    }

    @Override
    protected void disableComponent()
    {
    }

    public IDataShareManager getDataShareManager()
    {
        return this.dataShareManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dataShareManager", this.dataShareManager).toString();
    }
}
