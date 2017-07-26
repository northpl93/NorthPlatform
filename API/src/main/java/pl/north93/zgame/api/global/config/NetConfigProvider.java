package pl.north93.zgame.api.global.config;

import pl.north93.zgame.api.global.component.annotations.bean.DynamicBean;
import pl.north93.zgame.api.global.config.client.IConfigClient;

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
