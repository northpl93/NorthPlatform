package pl.north93.zgame.api.global.network.players;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import org.mongodb.morphia.annotations.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Przedstawia zcachowane informacje o danej nazwie uzytkownika,
 * jej statusie premium, mapowaniu na uuid.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity("username_cache")
public final class UsernameDetails
{
    private String  validSpelling;
    private UUID    uuid;
    private Boolean isPremium;
    private Instant fetchTime;

    public UsernameDetails(final String validSpelling, final Instant fetchTime)
    {
        this.validSpelling = validSpelling;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + validSpelling).getBytes(StandardCharsets.UTF_8));
        this.isPremium = false;
        this.fetchTime = fetchTime;
    }
}
