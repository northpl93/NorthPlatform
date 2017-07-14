package pl.north93.zgame.api.standalone.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

public class EnvironmentCfg
{
    @CfgComment("Identyfikator tej instancji API")
    @CfgStringDefault("unknown")
    private String id;

    public EnvironmentCfg()
    {
    }

    public EnvironmentCfg(final String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).toString();
    }
}
