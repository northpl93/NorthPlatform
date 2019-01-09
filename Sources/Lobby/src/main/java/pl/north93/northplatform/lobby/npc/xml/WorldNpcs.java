package pl.north93.northplatform.lobby.npc.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import java.util.List;

@XmlRootElement(name = "npcs")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({LobbyShopperXmlNpc.class, PlayGameXmlNpc.class})
public class WorldNpcs
{
    @XmlAnyElement(lax = true)
    private List<XmlNpc> npcs;

    public List<XmlNpc> getNpcs()
    {
        return this.npcs;
    }
}
