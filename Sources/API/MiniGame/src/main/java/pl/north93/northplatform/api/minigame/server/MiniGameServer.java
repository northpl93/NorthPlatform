package pl.north93.northplatform.api.minigame.server;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class MiniGameServer extends Component
{
    @Inject
    private IServerManager serverManager;

    @Override
    protected void enableComponent()
    {
        this.serverManager.start();
    }

    @Override
    protected void disableComponent()
    {
        this.serverManager.stop();
    }
}
