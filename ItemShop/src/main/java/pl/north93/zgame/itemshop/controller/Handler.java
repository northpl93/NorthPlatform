package pl.north93.zgame.itemshop.controller;

import static spark.Spark.post;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.impl.netcontroller.EconomyControllerComponent;
import pl.north93.zgame.api.economy.impl.shared.EconomyManagerImpl;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.itemshop.shared.DataEntry;
import pl.north93.zgame.itemshop.shared.DataModel;
import pl.north93.zgame.itemshop.shared.ReceiveStorage;

public class Handler
{
    private final Gson gson = new Gson();
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector           storageConnector;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager            networkManager;
    @InjectComponent("API.Economy.Controller")
    private EconomyControllerComponent economyController;
    @InjectComponent("API.MinecraftNetwork.PermissionsManager")
    private PermissionsManager         permissionsManager;
    private ReceiveStorage             receiveStorage;

    public Handler()
    {
        this.receiveStorage = new ReceiveStorage();
        post("itemshop/:nick", (request, response) ->
        {
            try
            {
                final String nick = request.params(":nick");
                final DataModel items = this.gson.fromJson(request.queryParams("items"), DataModel.class);
                final List<DataEntry> entries = items.getEntries();

                for (final DataEntry entry : entries)
                {
                    this.handle(nick, entry);
                }

                return "ok";
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                return null;
            }
        });
    }

    private void handle(final String nick, final DataEntry entry)
    {
        final Map<String, String> properties = entry.getProperties();
        final UUID playerId = this.networkManager.getPlayers().getUuidFromNick(nick);

        switch (entry.getDataType())
        {
            case MONEY:
            {
                final String currency = properties.get("currency");
                final double amount = Double.parseDouble(properties.get("amount"));

                final EconomyManagerImpl economyManager = this.economyController.getEconomyManager();
                final ICurrency currencyObj = economyManager.getCurrency(currency);
                try (final ITransaction transaction = economyManager.openTransaction(currencyObj, nick))
                {
                    transaction.add(amount);
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
                break;
            }

            case GROUP:
            {
                final int seconds = Integer.parseInt(properties.get("time"));

                this.networkManager.getPlayers().access(playerId, player ->
                {
                    player.setGroup(this.permissionsManager.getGroupByName(properties.get("group")));
                    if(player.getGroupExpireAt() == 0) {
                        player.setGroupExpireAt( System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds) );
                    }
                    else
                    {
                        player.setGroupExpireAt( player.getGroupExpireAt() + TimeUnit.SECONDS.toMillis(seconds) );
                    }
                });
                break;
            }

            case ITEM:
            {
                this.receiveStorage.addReceiveContentFor(playerId, entry);
                break;
            }

            case HEAD:
            {
                this.receiveStorage.addReceiveContentFor(playerId, entry);
                break;
            }

            case PERMISSION:
            {
                // TODO
                break;
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
