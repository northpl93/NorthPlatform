package pl.arieals.minigame.goldhunter;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

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
