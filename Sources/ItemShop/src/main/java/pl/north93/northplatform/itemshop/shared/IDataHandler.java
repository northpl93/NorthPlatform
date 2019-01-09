package pl.north93.northplatform.itemshop.shared;

import java.util.Map;

import pl.north93.northplatform.api.global.network.players.Identity;

public interface IDataHandler
{
    String getId();

    boolean process(Identity player, Map<String, String> data);
}
