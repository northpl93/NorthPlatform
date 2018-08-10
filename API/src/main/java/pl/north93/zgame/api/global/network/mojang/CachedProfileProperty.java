package pl.north93.zgame.api.global.network.mojang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class CachedProfileProperty
{
    private String name;
    private String value;
    private String signature;
}
