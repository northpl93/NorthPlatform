package pl.north93.zgame.api.global.data.players;

import java.util.UUID;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.network.NetworkPlayer;

public class PlayersDao extends Component
{
    @Override
    protected void enableComponent()
    {

    }

    @Override
    protected void disableComponent()
    {

    }

    public NetworkPlayer loadPlayer(final UUID uuid, final String name)
    {
        return null;
    }
}
