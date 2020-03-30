package pl.north93.northplatform.minigame.goldhunter.classes;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.minigame.goldhunter.GoldHunterLogger;
import pl.north93.northplatform.minigame.goldhunter.classes.xml.XmlClassDesc;
import pl.north93.northplatform.minigame.goldhunter.classes.xml.XmlClasses;

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
        XmlClasses classes = JaxbUtils.unmarshal(getClass().getResourceAsStream("/classes.xml"), XmlClasses.class);
        
        classes.getClasses().forEach(this::loadCharacterClass);
    }
    
    private void loadCharacterClass(String className)
    {
        try
        {
            XmlClassDesc classDesc = JaxbUtils.unmarshal(getClass().getResourceAsStream("/classes/" + className + ".xml"), XmlClassDesc.class);
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
