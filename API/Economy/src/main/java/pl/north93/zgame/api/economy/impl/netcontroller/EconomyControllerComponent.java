package pl.north93.zgame.api.economy.impl.netcontroller;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static spark.Spark.get;


import com.google.gson.Gson;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.cfg.EconomyConfig;
import pl.north93.zgame.api.economy.impl.netcontroller.rest.OperationInfo;
import pl.north93.zgame.api.economy.impl.shared.EconomyManagerImpl;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class EconomyControllerComponent extends Component
{
    private final Gson gson = new Gson();
    private EconomyConfig      config;
    @Inject
    private EconomyManagerImpl economyManager;

    @Override
    protected void enableComponent()
    {
        this.config = loadConfigFile(EconomyConfig.class, this.getApiCore().getFile("economy.yml"));
        this.economyManager.setConfig(this.config);

        get("player/:nick/money/:currency/add/:amount", (request, response) ->
        {
            final ICurrency currency = this.economyManager.getCurrency(request.params(":currency"));
            try (final ITransaction transaction = this.economyManager.openTransaction(currency, request.params(":nick")))
            {
                final double before = transaction.getAmount();
                final double after = before + Double.parseDouble(request.params("amount"));
                transaction.setAmount(after);
                return new OperationInfo(transaction.getAssociatedPlayer().getUuid(), before, after);
            }
        }, this.gson::toJson);
    }

    @Override
    protected void disableComponent()
    {
    }

    public EconomyConfig getConfig()
    {
        return this.config;
    }

    public EconomyManagerImpl getEconomyManager()
    {
        return this.economyManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("config", this.config).append("economyManager", this.economyManager).toString();
    }
}
