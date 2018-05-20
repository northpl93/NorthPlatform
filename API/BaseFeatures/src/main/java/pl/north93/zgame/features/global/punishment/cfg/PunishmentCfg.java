package pl.north93.zgame.features.global.punishment.cfg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "punishment")
public class PunishmentCfg
{
    @XmlElementWrapper(name = "bans")
    @XmlElement(name = "ban")
    private List<PredefinedBanCfg> bans;

    public List<PredefinedBanCfg> getBans()
    {
        return this.bans;
    }
}
