package pl.north93.northplatform.api.global.storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionConfig
{
    @XmlElement
    private String mongoDbConnect;

    @XmlElement
    private String mongoMainDatabase;

    @XmlElement
    private String redisHost;

    @XmlElement
    private int redisPort;

    @XmlElement
    private int redisTimeout;

    @XmlElement
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
