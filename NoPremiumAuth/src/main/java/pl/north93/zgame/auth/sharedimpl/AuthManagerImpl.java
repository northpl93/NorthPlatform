package pl.north93.zgame.auth.sharedimpl;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.redis.observable.Cache;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.auth.api.IAuthManager;
import pl.north93.zgame.auth.server.AuthServerComponent;

public class AuthManagerImpl implements IAuthManager
{
    @InjectComponent("NoPremiumAuth.Server")
    private AuthServerComponent  authServer;
    @InjectComponent("")
    private IObservationManager  observer;
    private Cache<UUID, Boolean> logInStatus;

    @PostInject
    private void init()
    {
        this.logInStatus = this.observer.cacheBuilder(UUID.class, Boolean.class)
                                        .name("authstatus")
                                        .keyMapper(key -> new ObjectKey(key.toString()))
                                        .build();
    }

    @Override
    public boolean isLoggedIn(final UUID uuid)
    {
        final Boolean isAuth = this.logInStatus.getValue(uuid).get();
        if (isAuth == null)
        {
            return false;
        }
        return isAuth;
    }

    @Override
    public void setLoggedInStatus(final UUID uuid, final boolean status)
    {
        this.logInStatus.put(uuid, status);
    }

    @Override
    public void deleteStatus(final UUID uuid)
    {
        this.logInStatus.remove(uuid);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
