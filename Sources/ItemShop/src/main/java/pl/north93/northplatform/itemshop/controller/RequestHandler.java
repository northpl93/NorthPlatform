package pl.north93.northplatform.itemshop.controller;

import static spark.Spark.post;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.itemshop.shared.DataEntry;
import pl.north93.northplatform.itemshop.shared.DataModel;
import pl.north93.northplatform.itemshop.shared.IDataHandler;
import spark.Request;

@Slf4j
public class RequestHandler
{
    private static final MetaKey ONE_TIME_LIST = MetaKey.get("itemShop_oneTimeList");
    private final Gson gson = new Gson();
    private final Map<String, IDataHandler> handlers = new HashMap<>();
    @Inject
    private IPlayersManager playersManager;

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
                log.error("Exception thrown while processing ItemShop request: {}", request, e);
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
        final Identity identity = this.playersManager.completeIdentity(Identity.create(null, nick));

        final List<DataEntry> entries = items.getEntries();
        for (final DataEntry entry : entries)
        {
            final Object[] params = new Object[] {entry, identity.getNick(), identity.getUuid()};
            log.info("Processing DataEntry: {} for {}/{}", params);

            this.handleItemShopEntry(identity, entry);
        }

        log.info("Completed processing ItemShop request for {}/{}", identity.getNick(), identity.getUuid());
        return "ok";
    }

    private void handleItemShopEntry(final Identity identity, final DataEntry dataEntry)
    {
        final IDataHandler handler = this.handlers.get(dataEntry.getType());
        if (handler == null)
        {
            log.warn("Not found dataHandler for dataEntry type: {}", dataEntry.getType());
            return;
        }

        final String oneTimeId = dataEntry.getOneTime();
        if (oneTimeId != null && this.markAsUsed(identity, oneTimeId))
        {
            log.info("Skipped dataEntry {} because it is already used by player", dataEntry.getType());
            return;
        }

        if (! handler.process(identity, dataEntry.getData()))
        {
            final Object[] params = {handler.getId(), identity.getNick(), identity.getUuid()};
            log.warn("Handler {} failed while processing dataEntry for player {}/{}", params);
        }
    }

    private boolean markAsUsed(final Identity identity, final String oneTimeId)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
