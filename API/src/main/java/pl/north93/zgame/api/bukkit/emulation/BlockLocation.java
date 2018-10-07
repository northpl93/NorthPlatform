package pl.north93.zgame.api.bukkit.emulation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BlockLocation
{
    private final int x;
    private final int y;
    private final int z;
}
