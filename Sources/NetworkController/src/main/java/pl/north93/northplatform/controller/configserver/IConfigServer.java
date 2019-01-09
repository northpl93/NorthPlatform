package pl.north93.northplatform.controller.configserver;

import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.controller.configserver.source.IConfigSource;

public interface IConfigServer
{
    <T> IConfig<T> addConfig(String configId, IConfigSource<T> loader);
}
