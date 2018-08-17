package pl.north93.zgame.api.global.network.mojang;

import java.time.Instant;
import java.util.UUID;

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
public final class UsernameDetails
{
    private String  username;
    private UUID    uuid;
    private Boolean isPremium;
    private Instant fetchTime;

    // no-premium constructor
    public UsernameDetails(final String username, final Instant fetchTime)
    {
        this.username = username;
        this.uuid = null;
        this.isPremium = false;
        this.fetchTime = fetchTime;
    }
}
