package pl.north93.northplatform.api.minigame.server;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.shared.impl.arena.ArenaManager;

public class MiniGameServer extends Component
{
    @Inject
    private ArenaManager arenaManager;
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

    public ArenaManager getArenaManager()
    {
        return this.arenaManager;
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public <T extends IServerManager> T getServerManager()
    {
        //noinspection unchecked
        return (T) this.serverManager;
    }
}
