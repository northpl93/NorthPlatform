package pl.north93.zgame.skyblock.api.utils;

import java.util.Iterator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.SkipInjections;

/**
 * Klasa iterująca po spirali do zewnątrz.
 * Startuje w punkcie 0,0.
 * Implementuje Iteratora i Iterable.
 */
@SkipInjections
public class SpiralOut implements Iterable<Coords2D>, Iterator<Coords2D>
{
    private int layer = 1;
    private int leg;
    private int x;
    private int z;

    private void goNext()
    {
        switch (this.leg)
        {
            case 0:
                ++ this.x;
                if (this.x == this.layer)
                {
                    ++ this.leg;
                }
                break;

            case 1:
                ++ this.z;
                if (this.z == this.layer)
                {
                    ++ this.leg;
                }
                break;

            case 2:
                -- this.x;
                if (- this.x == this.layer)
                {
                    ++ this.leg;
                }
                break;
            case 3:
                -- this.z;
                if (- this.z == this.layer)
                {
                    this.leg = 0;
                    ++ this.layer;
                }
                break;
        }
    }

    public Coords2D getNextStep()
    {
        this.goNext();
        return new Coords2D(this.x, this.z);
    }

    @Override
    public Iterator<Coords2D> iterator()
    {
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return true;
    }

    @Override
    public Coords2D next()
    {
        return this.getNextStep();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("layer", this.layer).append("leg", this.leg).append("x", this.x).append("z", this.z).toString();
    }
}
