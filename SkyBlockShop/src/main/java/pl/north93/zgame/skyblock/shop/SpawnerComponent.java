package pl.north93.zgame.skyblock.shop;


import static pl.north93.zgame.api.global.utils.ConfigUtils.loadConfigFile;


import java.io.File;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.skyblock.shop.cfg.RootConfigs.SpawnerConfig;

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
