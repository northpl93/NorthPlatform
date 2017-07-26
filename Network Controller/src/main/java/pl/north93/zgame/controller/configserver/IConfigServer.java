package pl.north93.zgame.controller.configserver;

import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.controller.configserver.source.IConfigSource;

public interface IConfigServer
{
    <T> IConfig<T> addConfig(String configId, IConfigSource<T> loader);
}
