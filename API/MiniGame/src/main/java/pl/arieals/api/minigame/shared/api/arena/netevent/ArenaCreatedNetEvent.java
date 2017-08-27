package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameIdentity;

public class ArenaCreatedNetEvent implements IArenaNetEvent
{
    private UUID         uuid;
    private GameIdentity miniGame;

    public ArenaCreatedNetEvent()
    {
    }

    public ArenaCreatedNetEvent(final UUID uuid, final GameIdentity miniGame)
    {
        this.uuid = uuid;
        this.miniGame = miniGame;
    }

    @Override
    public UUID getArenaId()
    {
        return this.uuid;
    }

    @Override
    public GameIdentity getMiniGame()
    {
        return this.miniGame;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("miniGame", this.miniGame).toString();
    }
}
