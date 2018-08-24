package pl.north93.zgame.api.bukkit.world;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class ChunkLocation
{
    private final int x;
    private final int z;
}
