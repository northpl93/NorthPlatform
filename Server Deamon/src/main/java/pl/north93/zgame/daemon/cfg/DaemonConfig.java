package pl.north93.zgame.daemon.cfg;

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
    @CfgComment("Identyfikator tego demona (maszyny)")
    @CfgStringDefault("please_set_name")
    public String daemonName;

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
}
