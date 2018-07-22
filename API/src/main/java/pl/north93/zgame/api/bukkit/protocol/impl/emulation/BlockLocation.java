package pl.north93.zgame.api.bukkit.protocol.impl.emulation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class BlockLocation
{
    private final int x;
    private final int y;
    private final int z;
}
