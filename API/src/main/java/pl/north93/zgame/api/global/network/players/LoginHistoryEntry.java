package pl.north93.zgame.api.global.network.players;

import java.time.Instant;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity("join_history")
@Indexes({@Index(fields = {@Field("nick")}), @Index(fields = {@Field("ip")})})
public class LoginHistoryEntry
{
    private String  nick;
    private boolean premium;
    private String  ip;
    private String  bungee;
    private Instant at;
}
