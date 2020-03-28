package pl.north93.northplatform.api.minigame.shared.api.status;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public final class InGameStatus implements IPlayerStatus
{
    private UUID serverId;
    private UUID arenaId;
    private GameIdentity game;

    @Override
    public StatusType getType()
    {
        return StatusType.GAME;
    }
}
