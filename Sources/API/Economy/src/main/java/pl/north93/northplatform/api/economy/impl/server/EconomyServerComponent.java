package pl.north93.northplatform.api.economy.impl.server;

import javax.xml.bind.JAXB;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import net.milkbowl.vault.economy.Economy;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.impl.client.EconomyComponent;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
public class EconomyServerComponent extends Component
{
    @Inject
    private EconomyComponent economy;
    private VaultConfig      config;

    @Override
    protected void enableComponent()
    {
        this.config = JAXB.unmarshal(this.getApiCore().getFile("vault.xml"), VaultConfig.class);
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
            log.warn("Vault integration is enabled but Vault is not present.");
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
