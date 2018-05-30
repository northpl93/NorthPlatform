package pl.mcpiraci.world.properties.impl.xml;

import javax.xml.bind.annotation.XmlElement;

public class XmlWorldProtection
{
    @XmlElement(name = "build")
    private Boolean buildAllowed;
    
    @XmlElement(name = "interact")
    private Boolean interractAllowed;
    
    @XmlElement(name = "playersInvulnerable")
    private Boolean playersInvulnerable;
    
    public Boolean isBuildAllowed()
    {
        return buildAllowed;
    }
    
    public Boolean isInterractAllowed()
    {
        return interractAllowed;
    }
    
    public Boolean isPlayersInvulnerable()
    {
        return playersInvulnerable;
    }
}
