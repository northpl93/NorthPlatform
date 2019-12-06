package pl.north93.northplatform.api.global.network.mojang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.serializer.platform.annotations.NorthField;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class CachedProfile
{
    @NorthField(name = "_id") // uzywamy jako klucza w mongo
    private UUID uuid;
    private String name;
    private List<CachedProfileProperty> properties;
}
