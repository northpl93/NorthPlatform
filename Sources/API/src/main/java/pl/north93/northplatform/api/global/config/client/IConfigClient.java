package pl.north93.northplatform.api.global.config.client;

import pl.north93.northplatform.api.global.config.IConfig;

public interface IConfigClient
{
    <T> IConfig<T> getConfig(Class<T> type, String configId);

    void reloadConfig(String configId);
}
