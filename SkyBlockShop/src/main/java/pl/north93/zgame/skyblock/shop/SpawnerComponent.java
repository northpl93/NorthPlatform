package pl.north93.zgame.skyblock.shop;


import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.cfg.RootConfigs;

import pl.north93.zgame.skyblock.shop.cfg.RootConfigs.SpawnerConfig;

import java.io.File;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;

public class SpawnerComponent extends Component
{
    private SpawnerManager spawnerManager;

    @Override
    protected void enableComponent() {
        final File shop = this.getApiCore().getFile("shop");
        if (! shop.exists())
        {
            shop.mkdir();
        }

        final SpawnerConfig spawnerConfig = loadConfigFile(SpawnerConfig.class, new File(shop, "spawner.yml"));
        this.spawnerManager = new SpawnerManager(spawnerConfig.getMobs());
    }

    public SpawnerManager getSpawnerManager()
    {
        return this.spawnerManager;
    }

    @Override
    protected void disableComponent() {}
}
