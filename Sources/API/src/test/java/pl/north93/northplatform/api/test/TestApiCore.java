package pl.north93.northplatform.api.test;

import pl.north93.northplatform.api.global.API;
import pl.north93.northplatform.api.global.Platform;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.standalone.StandaloneApiCore;
import pl.north93.northplatform.api.standalone.cfg.EnvironmentCfg;

/**
 * Prosta klasa umożliwiająca uruchomienie API
 * na platformie {@link Platform#STANDALONE}
 * w celach testowych.
 * <p>
 * W trybie testowym niedostępny jest klient redefiniowania kodu
 * więc automatyczne injekcie nie działają przez {@code new}
 */
public class TestApiCore extends StandaloneApiCore
{
    public static TestApiCore ensureEnvironment()
    {
        if (API.getApiCore() != null)
        {
            return (TestApiCore) API.getApiCore();
        }

        System.out.println("North API is running testing environment");
        final TestApiCore testApiCore = new TestApiCore();
        testApiCore.startCore();
        return testApiCore;
    }

    @Override
    protected void init() throws Exception
    {
        this.getApiLogger().info("Initialising testing environment");
        this.environmentCfg = new EnvironmentCfg("testenv");
    }

    /**
     * Zwraca komponent o podanej nazwie.
     * @param name nazwa komponentu.
     * @param <T> typ klasy głównej komponentu.
     * @return komponent o podanej nazwie.
     */
    public <T extends Component> T getComponent(final String name)
    {
        return this.getComponentManager().getComponent(name);
    }
}
