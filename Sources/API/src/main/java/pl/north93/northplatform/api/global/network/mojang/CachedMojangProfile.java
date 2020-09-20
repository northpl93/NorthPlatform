package pl.north93.northplatform.api.global.network.mojang;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.serializer.platform.annotations.NorthField;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class CachedMojangProfile
{
    @NorthField(name = "_id") // uzywamy jako klucza w mongo
    private UUID uuid;
    private String name;
    private List<CachedMojangProfileProperty> properties;
}
