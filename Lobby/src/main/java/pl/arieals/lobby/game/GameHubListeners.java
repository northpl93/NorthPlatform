package pl.arieals.lobby.game;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public abstract class GameHubListeners implements AutoListener
{
    @Inject
    protected BukkitApiCore apiCore;

    public abstract boolean isMyHub(HubWorld hubWorld);
}
