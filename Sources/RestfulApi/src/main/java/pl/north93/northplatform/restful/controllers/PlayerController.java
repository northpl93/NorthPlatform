package pl.north93.northplatform.restful.controllers;

import static spark.Spark.halt;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.utils.Wrapper;
import pl.north93.northplatform.restful.models.PlayerModel;
import spark.Request;
import spark.Response;

public class PlayerController
{
    @Inject
    private IPlayersManager playersManager;

    public Object root(final Request request, final Response response)
    {
        final Wrapper<Object> myResponse = new Wrapper<>();
        this.playersManager.access(request.params(":nick"), online ->
        {
            myResponse.set(new PlayerModel(online.getUuid(), online.getNick(), true, online.getGroup().getName(), online.getGroupExpireAt(), online.getMetaStore()));
        }, offline ->
        {
            myResponse.set(new PlayerModel(offline.getUuid(), offline.getLatestNick(), false, offline.getGroup().getName(), offline.getGroupExpireAt(), offline.getMetaStore()));
        });

        if (myResponse.get() == null)
        {
            halt(404);
        }
        return myResponse.get();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
