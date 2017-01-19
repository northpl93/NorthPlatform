package pl.north93.zgame.auth.server;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.auth.sharedimpl.AuthManagerImpl;

public class AuthServerComponent extends Component
{
    private AuthManagerImpl authManager;

    @Override
    protected void enableComponent()
    {
        this.authManager = new AuthManagerImpl(this);
    }

    @Override
    protected void disableComponent()
    {

    }
}
