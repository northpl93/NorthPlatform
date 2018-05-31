package pl.north93.zgame.itemshop.controller.handlers;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.itemshop.shared.IDataHandler;

public class GroupDataHandler implements IDataHandler
{
    @Inject
    private Logger             logger;
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
            final Group oldGroup = playerObj.getGroup();
            final Group newGroup = this.permissionsManager.getGroupByName(data.get("group"));

            if (! this.canUpgradeToGroup(oldGroup, newGroup))
            {
                final Object[] logParams = {playerObj.getLatestNick(), oldGroup.getName(), newGroup.getName()};
                this.logger.log(Level.WARNING, "Ignored group upgrade for player {0}, old={1}, new={2}", logParams);
                return;
            }

            playerObj.setGroup(newGroup);

            if (playerObj.getGroupExpireAt() == 0 || ! oldGroup.equals(newGroup))
            {
                // jesli poprzednia grupa nigdy nie wygasa, lub zmieniamy grupe to odliczamy czas od teraz
                final long newExpireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
                playerObj.setGroupExpireAt(newExpireTime);
            }
            else
            {
                // wydluzamy czas posiadania tej samej grupy
                final long newExpireTime = playerObj.getGroupExpireAt() + TimeUnit.SECONDS.toMillis(seconds);
                playerObj.setGroupExpireAt(newExpireTime);
            }

            final Object[] logParams = {playerObj.getLatestNick(), newGroup.getName(), playerObj.getGroupExpireAt()};
            this.logger.log(Level.INFO, "Group of {0} is now {1} and will expire at {2}", logParams);
        });
    }

    private boolean canUpgradeToGroup(final Group oldGroup, final Group newGroup)
    {
        if (oldGroup.equals(newGroup))
        {
            return true;
        }

        final Collection<Group> inheritance = newGroup.getInheritance();
        if (inheritance.isEmpty())
        {
            return true;
        }
        return inheritance.contains(oldGroup);
    }
}
