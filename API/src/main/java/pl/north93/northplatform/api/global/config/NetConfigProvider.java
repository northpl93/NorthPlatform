package pl.north93.northplatform.api.global.config;

import pl.north93.northplatform.api.global.config.client.IConfigClient;
import pl.north93.northplatform.api.global.component.annotations.bean.DynamicBean;

/**
 * Udostepnia beana dynamicznego ktory odpowiada za
 * zwracanie instanci IConfig<T>.
 *
 * {@code private @Inject @NetConfig(type=MojConfig.class,id="test") IConfig<MojConfig> nazwaFieldu; }
 */
public final class NetConfigProvider
{
    private NetConfigProvider()
    {
    }

    @DynamicBean
    public static <T> IConfig<T> getConfigInstance(final IConfigClient configClient, final NetConfig netConfig)
    {
        //noinspection unchecked
        return configClient.getConfig((Class<T>) netConfig.type(), netConfig.id());
    }
}
