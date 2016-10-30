package pl.north93.zgame.api.global.cfg;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

@CfgComments({"Konfiguracja połączeń: MySQL i Redis"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class ConnectionConfig
{
    @CfgComment("Czy włączyć tryb debugowania")
    @CfgBooleanDefault(false)
    private boolean debug;

    @CfgComment("Host bazy danych i port")
    @CfgStringDefault("localhost:3306")
    private String mysqlHost;

    @CfgComment("Nazwa użytkownika bazy danych")
    @CfgStringDefault("user")
    private String mysqlUser;

    @CfgComment("Hasło uzytkownika bazy danych")
    @CfgStringDefault("password")
    private String mysqlPassword;

    @CfgComment("Nazwa bazy danych")
    @CfgStringDefault("database")
    private String mysqlDatabase;

    @CfgComment("Host redisa")
    @CfgStringDefault("localhost")
    private String redisHost;

    @CfgComment("Port redisa")
    @CfgIntDefault(6379)
    private int redisPort;

    @CfgIntDefault(5000)
    private int redisTimeout;

    @CfgComment("Hasło do redisa")
    @CfgStringDefault("password")
    private String redisPassword;

    public boolean isDebug()
    {
        return this.debug;
    }

    public void setDebug(final boolean debug)
    {
        this.debug = debug;
    }

    public String getMysqlHost()
    {
        return this.mysqlHost;
    }

    public String getMysqlUser()
    {
        return this.mysqlUser;
    }

    public String getMysqlPassword()
    {
        return this.mysqlPassword;
    }

    public String getMysqlDatabase()
    {
        return this.mysqlDatabase;
    }

    public String getRedisHost()
    {
        return this.redisHost;
    }

    public int getRedisPort()
    {
        return this.redisPort;
    }

    public int getRedisTimeout()
    {
        return this.redisTimeout;
    }

    public String getRedisPassword()
    {
        return this.redisPassword;
    }
}
