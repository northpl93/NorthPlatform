package pl.north93.zgame.api.economy.impl.server;

import static pl.north93.zgame.api.global.utils.ConfigUtils.loadConfigFile;


import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.milkbowl.vault.economy.Economy;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class EconomyServerComponent extends Component
{
    @Inject
    private EconomyComponent economy;
    private VaultConfig      config;

    @Override
    protected void enableComponent()
    {
        this.config = loadConfigFile(VaultConfig.class, this.getApiCore().getFile("vault.yml"));
        if (this.config.isEnableVaultIntegration())
        {
            this.enableIntegration();
        }
    }

    private void enableIntegration()
    {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        final Plugin vault = pluginManager.getPlugin("Vault");
        if (vault == null)
        {
            this.getLogger().warning("Vault integration is enabled but Vault is not present.");
            return;
        }

        final IEconomyManager economyManager = this.economy.getEconomyManager();
        final NorthPlatformEconomy connector = new NorthPlatformEconomy(economyManager, economyManager.getCurrency(this.config.getCurrencyName()));
        Bukkit.getServicesManager().register(Economy.class, connector, vault, ServicePriority.Normal);
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("economy", this.economy).append("config", this.config).toString();
    }
}
