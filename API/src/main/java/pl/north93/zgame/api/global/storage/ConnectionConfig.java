package pl.north93.zgame.api.global.storage;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgIntDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

@CfgComments({"Konfiguracja połączeń: MongoDB i Redis"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class ConnectionConfig
{
    @CfgComment("Host bazy danych i port")
    @CfgStringDefault("mongodb://user:password@127.0.0.1:27017/?authSource=server")
    private String mongoDbConnect;

    @CfgComment("Baza danych z której ma korzystać API")
    @CfgStringDefault("server")
    private String mongoMainDatabase;

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

    public String getMongoDbConnect()
    {
        return this.mongoDbConnect;
    }

    public String getMongoMainDatabase()
    {
        return this.mongoMainDatabase;
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
