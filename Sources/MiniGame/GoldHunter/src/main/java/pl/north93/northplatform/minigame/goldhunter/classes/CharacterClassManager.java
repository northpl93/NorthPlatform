package pl.north93.northplatform.minigame.goldhunter.classes;

import javax.xml.bind.JAXB;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import pl.north93.northplatform.minigame.goldhunter.GoldHunterLogger;
import pl.north93.northplatform.minigame.goldhunter.classes.xml.XmlClassDesc;
import pl.north93.northplatform.minigame.goldhunter.classes.xml.XmlClasses;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class CharacterClassManager
{
    @Inject
    @GoldHunterLogger
    public static Logger logger;
    
    private final Map<String, CharacterClass> characterClasses = new LinkedHashMap<>();
    
    private final MessagesBox messages;
    
    @Bean
    public CharacterClassManager(@Messages("GoldHunter_classes") MessagesBox messages)
    {
        this.messages = messages;
    }
    
    public void initClasses()
    {
        XmlClasses classes = JAXB.unmarshal(getClass().getResourceAsStream("/classes.xml"), XmlClasses.class);
        
        classes.getClasses().forEach(this::loadCharacterClass);
    }
    
    private void loadCharacterClass(String className)
    {
        try
        {
            XmlClassDesc classDesc = JAXB.unmarshal(getClass().getResourceAsStream("/classes/" + className + ".xml"), XmlClassDesc.class);
            characterClasses.put(className, classDesc.toCharacterClass(messages));
        }
        catch ( Exception e )
        {
            logger.error("Erorr when load class {}", className, e);
        }
    }
    
    public CharacterClass getByName(String name)
    {
        return characterClasses.get(name.toLowerCase());
    }
    
    public CharacterClass getDefaultClass()
    {
        return characterClasses.get("berserker");
    }
}
