package pl.north93.northplatform.lobby.chest.animation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.lobby.chest.opening.ChestOpeningController;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public final class ChestIdleAnimation extends AbstractChestRotationAnimation
{
    @Inject
    private ChestOpeningController openingController;
    private float                  currentRotation;

    public ChestIdleAnimation(final AnimationInstance instance)
    {
        super(instance);
    }

    @Override
    void tick()
    {
        if (this.currentRotation >= 360)
        {
            this.currentRotation = 0;
        }

        this.setRotation(this.currentRotation += 0.5);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currentRotation", this.currentRotation).toString();
    }
}
