package pl.arieals.lobby.chest.animation;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.opening.ChestOpeningController;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class ChestIdleAnimation extends AbstractChestRotationAnimation
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
    void clicked()
    {
        final Player player = this.getPlayer();
        if (! this.openingController.beginChestOpening(player))
        {
            // wymusza odswiezenie widoku jak nie udalo sie rozpoczac otwierania
            this.openingController.nextChest(player);
            return;
        }

        final Location chestLocation = this.instance.getArmorStand().getLocation();
        // odpalamy dzwiek uderzenia skrzynki
        player.playSound(chestLocation, Sound.BLOCK_WOOD_HIT, 0.4f, 1);
        // uruchamiamy dzwiek elytry, wiatru
        player.playSound(chestLocation, Sound.ITEM_ELYTRA_FLYING, 1, 2);

        this.instance.setAnimation(new ChestOpenAnimation(this.instance));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currentRotation", this.currentRotation).toString();
    }
}
