package pl.north93.northplatform.api.minigame.shared.api.status;

import java.util.UUID;

public class OfflineStatus implements IPlayerStatus
{
    public static final IPlayerStatus INSTANCE = new OfflineStatus();

    private OfflineStatus()
    {
    }

    @Override
    public UUID getServerId()
    {
        return null;
    }

    @Override
    public StatusType getType()
    {
        return StatusType.OFFLINE;
    }
}
