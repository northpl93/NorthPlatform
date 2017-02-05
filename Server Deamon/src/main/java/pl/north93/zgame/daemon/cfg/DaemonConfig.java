package pl.north93.zgame.daemon.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

@CfgComments({"Konfiguracja demona"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class DaemonConfig
{
    @CfgComment("Adres do którego ma się łączyć proxy")
    @CfgStringDefault("localhost")
    public String externalHost;

    @CfgComment("Na jaki adres mają nasłuchiwać serwery")
    @CfgStringDefault("0.0.0.0")
    public String listenHost;

    @CfgComment("Od jakiego numeru portu zaczynać uruchamianie serwerów")
    @CfgIntDefault(25570)
    public int portRangeStart;

    @CfgComment("Maksymalna ilosc pamieci (w MB) która może zostać użyta przez serwery")
    @CfgIntDefault(1024)
    public int maxMemory;

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("externalHost", this.externalHost).append("listenHost", this.listenHost).append("portRangeStart", this.portRangeStart).append("maxMemory", this.maxMemory).toString();
    }
}
