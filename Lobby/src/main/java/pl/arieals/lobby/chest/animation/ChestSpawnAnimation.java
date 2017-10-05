package pl.arieals.lobby.chest.animation;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

class ChestSpawnAnimation extends ChestAnimation
{
    private static final double MAX_DISTANCE = 3;
    private static final double STEP_DISTANCE = 0.10;
    private double currentDistance;

    public ChestSpawnAnimation(final AnimationInstance instance)
    {
        super(instance);
    }

    @Override
    void tick()
    {
        if (this.currentDistance >= MAX_DISTANCE)
        {
            this.endAnimation();
            return;
        }

        this.processAnimation();
    }

    @Override
    void clicked()
    {
    }

    private void endAnimation()
    {
        // gdy zakonczy sie animacja wejscia przelaczamy na
        // animacje spoczynku
        this.instance.setAnimation(new ChestIdleAnimation(this.instance));
    }

    private void processAnimation()
    {
        final ArmorStand armorStand = this.getArmorStand();

        final Location location = armorStand.getLocation();
        location.setY(location.getY() + STEP_DISTANCE);
        this.currentDistance += STEP_DISTANCE;

        armorStand.teleport(location);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currentDistance", this.currentDistance).toString();
    }
}
