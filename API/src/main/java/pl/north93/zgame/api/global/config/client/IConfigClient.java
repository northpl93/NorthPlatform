package pl.north93.zgame.api.global.config.client;

import pl.north93.zgame.api.global.config.IConfig;

public interface IConfigClient
{
    <T> IConfig<T> getConfig(Class<T> type, String id);
}
