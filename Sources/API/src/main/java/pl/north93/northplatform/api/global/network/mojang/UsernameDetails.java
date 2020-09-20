package pl.north93.northplatform.api.global.network.mojang;

import java.time.Instant;
import java.util.UUID;

import lombok.Value;
import pl.north93.serializer.platform.annotations.NorthField;

/**
 * Represents cached information about username.
 */
@Value
public class UsernameDetails
{
    @NorthField(name = "_id")
    String username;
    UUID uuid;
    boolean isPremium;
    Instant fetchTime;
}
