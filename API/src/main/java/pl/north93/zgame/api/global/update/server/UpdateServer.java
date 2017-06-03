package pl.north93.zgame.api.global.update.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.update.api.IUpdateApiRpc;
import pl.north93.zgame.api.global.update.api.UpdateFile;

public class UpdateServer extends Component implements IUpdateApiRpc
{
    @Inject
    private IRpcManager rpcManager;

    @Override
    protected void enableComponent()
    {
        this.rpcManager.addRpcImplementation(IUpdateApiRpc.class, this);
    }

    @Override
    protected void disableComponent()
    {

    }

    @Override
    public boolean isControllerReady()
    {
        return this.getApiCore().getApiState().isEnabled();
    }

    @Override
    public void reloadConfig()
    {

    }

    @Override
    public UpdateFile[] getFilesFor(final Platform platform, final String clientId)
    {
        return new UpdateFile[0];
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
