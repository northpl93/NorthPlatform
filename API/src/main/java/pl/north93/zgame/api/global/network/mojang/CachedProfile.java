package pl.north93.zgame.api.global.network.mojang;

import java.util.List;
import java.util.UUID;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity("profile_cache")
public final class CachedProfile
{
    @Id
    private UUID uuid;
    private String name;
    private List<CachedProfileProperty> properties;
}
