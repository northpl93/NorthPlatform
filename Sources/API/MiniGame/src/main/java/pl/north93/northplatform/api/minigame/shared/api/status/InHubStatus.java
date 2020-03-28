package pl.north93.northplatform.api.minigame.shared.api.status;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Reprezentuje lokację w której znajduje się gracz
 */
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public final class InHubStatus implements IPlayerStatus
{
    private UUID serverId;
    private String hubId;

    @Override
    public StatusType getType()
    {
        return StatusType.HUB;
    }
}
