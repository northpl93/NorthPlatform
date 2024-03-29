package pl.north93.northplatform.api.global.network.mojang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class CachedMojangProfileProperty
{
    private String name;
    private String value;
    private String signature;
}
