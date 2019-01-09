package pl.arieals.minigame.goldhunter.classes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "classes")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlClasses
{
    @XmlElement(name = "class")
    private List<String> classes = new ArrayList<>();
    
    public List<String> getClasses()
    {
        return classes;
    }
}
