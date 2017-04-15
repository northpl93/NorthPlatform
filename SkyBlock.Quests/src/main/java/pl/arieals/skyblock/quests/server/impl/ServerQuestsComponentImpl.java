package pl.arieals.skyblock.quests.server.impl;

import pl.arieals.skyblock.quests.server.api.IServerQuestsComponent;
import pl.arieals.skyblock.quests.server.api.IServerQuestsManager;
import pl.arieals.skyblock.quests.shared.api.IQuestsController;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class ServerQuestsComponentImpl extends Component implements IServerQuestsComponent
{
    @InjectComponent("")
    private IRpcManager rpcManager;
    private IQuestsController questsController;

    @Override
    protected void enableComponent()
    {
        this.questsController = this.rpcManager.createRpcProxy(IQuestsController.class, Targets.networkController());
    }

    @Override
    protected void disableComponent()
    {

    }

    @Override
    public IQuestsController getQuestsController()
    {
        return this.questsController;
    }

    @Override
    public IServerQuestsManager getServerQuestsManager()
    {
        return null;
    }
}
