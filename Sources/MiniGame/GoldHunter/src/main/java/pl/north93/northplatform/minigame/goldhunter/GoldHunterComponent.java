package pl.north93.northplatform.minigame.goldhunter;

import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class GoldHunterComponent extends Component
{
    @Inject
    private GoldHunter goldHunter;
    
    @Override
    protected void enableComponent()
    {
        goldHunter.enable();
    }

    @Override
    protected void disableComponent()
    {
        goldHunter.disable();
    }
}
