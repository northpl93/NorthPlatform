package pl.north93.zgame.itemshop.controller;

import static spark.Spark.post;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.itemshop.shared.DataEntry;
import pl.north93.zgame.itemshop.shared.DataModel;
import pl.north93.zgame.itemshop.shared.IDataHandler;
import spark.Request;

public class RequestHandler
{
    private static final MetaKey ONE_TIME_LIST = MetaKey.get("itemShop_oneTimeList");
    private final Gson                      gson     = new Gson();
    private final Map<String, IDataHandler> handlers = new HashMap<>();
    @Inject
    private Logger          logger;
    @Inject
    private INetworkManager networkManager;

    @Bean
    private RequestHandler()
    {
        post("itemshop/:nick", (request, response) ->
        {
            try
            {
                return this.handleItemShopRequest(request);
            }
            catch (final Exception e)
            {
                this.logger.log(Level.SEVERE, "Exception thrown while processing ItemShop request: " + request, e);
                return null;
            }
        });
    }

    @Aggregator(IDataHandler.class)
    public void registerHandler(final IDataHandler handler)
    {
        this.handlers.put(handler.getId(), handler);
    }

    private String handleItemShopRequest(final Request request)
    {
        final DataModel items = this.gson.fromJson(request.queryParams("items"), DataModel.class);

        final String nick = request.params(":nick");
        final Identity identity = this.networkManager.getPlayers().completeIdentity(Identity.create(null, nick));

        final List<DataEntry> entries = items.getEntries();
        for (final DataEntry entry : entries)
        {
            final Object[] params = new Object[] {entry, identity.getNick(), identity.getUuid()};
            this.logger.log(Level.INFO, "Processing DataEntry: {0} for {1}/{2}", params);

            this.handle(identity, entry);
        }

        final Object[] params = {identity.getNick(), identity.getUuid()};
        this.logger.log(Level.INFO, "Completed processing ItemShop request for {0}/{1}", params);
        return "ok";
    }

    private void handle(final Identity identity, final DataEntry dataEntry)
    {
        final IDataHandler handler = this.handlers.get(dataEntry.getType());
        if (handler == null)
        {
            this.logger.log(Level.WARNING, "Not found dataHandler for dataEntry type: {0}", dataEntry.getType());
            return;
        }

        final String oneTimeId = dataEntry.getOneTime();
        if (oneTimeId != null && this.markAsUsed(identity, oneTimeId))
        {
            this.logger.log(Level.INFO, "Skipped dataEntry {0} because it is already used by player", dataEntry.getType());
            return;
        }

        if (! handler.process(identity, dataEntry.getData()))
        {
            final Object[] params = {handler.getId(), identity.getNick(), identity.getUuid()};
            this.logger.log(Level.WARNING, "Handler {0} failed while processing dataEntry for player {1}/{2}", params);
        }
    }

    private boolean markAsUsed(final Identity identity, final String oneTimeId)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final MetaStore metaStore = t.getPlayer().getMetaStore();

            final List<String> oneTimeItemsList = Optional.ofNullable(metaStore.<List<String>>get(ONE_TIME_LIST)).orElseGet(() ->
            {
                final List<String> list = new ArrayList<>();
                metaStore.set(ONE_TIME_LIST, list);

                return list;
            });

            if (oneTimeItemsList.contains(oneTimeId))
            {
                return true;
            }
            else
            {
                oneTimeItemsList.add(oneTimeId);
                return false;
            }
        }
    }

    //        final ArrayList<DataEntry> objects = new ArrayList<>();
    //        final HashMap<String, String> stringStringHashMap = new HashMap<>();
    //        stringStringHashMap.put("dupa1", "dupa1");
    //        stringStringHashMap.put("dupa2", "dupa2");
    //
    //        objects.add(new DataEntry("chuj", stringStringHashMap));
    //        System.out.println(this.gson.toJson(new DataModel(objects)));

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
