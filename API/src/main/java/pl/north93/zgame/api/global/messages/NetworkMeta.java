package pl.north93.zgame.api.global.messages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgCustomDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

import pl.north93.zgame.api.global.network.JoiningPolicy;

/**
 * Używane do wczytywania konfiguracji z yaml i do wysyłanie przez msg pack
 */
@CfgComments({"Konfiguracja sieci"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class NetworkMeta
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @CfgCustomDefault(JoiningPolicy.class)
    public @interface CfgJoiningPolicyDefault
    {
        JoiningPolicy value();
    }

    @CfgComment("Kto może wchodzić na serwer. Dostępne: EVERYONE, ONLY_ADMIN, NOBODY")
    @CfgJoiningPolicyDefault(JoiningPolicy.EVERYONE)
    public JoiningPolicy joiningPolicy; // kto może wchodzić na serwer

    @CfgComment("Wyświetlana maksymalna ilość graczy")
    @CfgIntDefault(1000)
    public Integer displayMaxPlayers; // wyswietlana maksymalna liczba graczy

    @CfgComment("Wiadomość na liście serwerów")
    @CfgStringDefault("Network Platform by NorthPL")
    public String serverListMotd; // Wiadomosc dnia na liscie serwerow

    @CfgComment("Domyślna grupa serwerów z którą zostanie połączony gracz")
    @CfgStringDefault("default")
    public String defaultServersGroup;

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("joiningPolicy", this.joiningPolicy).append("displayMaxPlayers", this.displayMaxPlayers).append("serverListMotd", this.serverListMotd).toString();
    }
}
