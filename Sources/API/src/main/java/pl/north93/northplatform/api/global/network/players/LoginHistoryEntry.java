package pl.north93.northplatform.api.global.network.players;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryEntry
{
    private String  nick;
    private boolean premium;
    private String  ip;
    private String  bungee;
    private Instant at;
}
