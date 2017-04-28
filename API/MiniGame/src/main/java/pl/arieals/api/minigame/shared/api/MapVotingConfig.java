package pl.arieals.api.minigame.shared.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

public class MapVotingConfig
{
    @CfgComment("Czy wlaczone")
    private Boolean enabled;
    @CfgComment("Ilosc map do wyboru podczas glosowania.")
    private Integer numberOfMaps;

    public Boolean getEnabled()
    {
        return this.enabled;
    }

    public Integer getNumberOfMaps()
    {
        return this.numberOfMaps;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enabled", this.enabled).append("numberOfMaps", this.numberOfMaps).toString();
    }
}
