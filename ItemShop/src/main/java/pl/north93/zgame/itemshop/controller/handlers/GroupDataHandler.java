package pl.north93.zgame.itemshop.controller.handlers;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.itemshop.shared.IDataHandler;

public class GroupDataHandler implements IDataHandler
{
    @Inject
    private INetworkManager    networkManager;
    @Inject
    private PermissionsManager permissionsManager;

    @Override
    public String getId()
    {
        return "group";
    }

    @Override
    public boolean process(final Identity player, final Map<String, String> data)
    {
        final int seconds = Integer.parseInt(data.get("time"));

        return this.networkManager.getPlayers().access(player, playerObj ->
        {
            playerObj.setGroup(this.permissionsManager.getGroupByName(data.get("group")));

            if (playerObj.getGroupExpireAt() == 0)
            {
                final long newExpireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
                playerObj.setGroupExpireAt(newExpireTime);
            }
            else
            {
                final long newExpireTime = playerObj.getGroupExpireAt() + TimeUnit.SECONDS.toMillis(seconds);
                playerObj.setGroupExpireAt(newExpireTime);
            }
        });
    }
}
