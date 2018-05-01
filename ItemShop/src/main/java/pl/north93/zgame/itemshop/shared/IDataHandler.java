package pl.north93.zgame.itemshop.shared;

import java.util.Map;

import pl.north93.zgame.api.global.network.players.Identity;

public interface IDataHandler
{
    String getId();

    boolean process(Identity player, Map<String, String> data);
}
