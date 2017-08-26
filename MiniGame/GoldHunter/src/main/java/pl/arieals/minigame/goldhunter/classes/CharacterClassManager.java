package pl.arieals.minigame.goldhunter.classes;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import com.google.common.collect.Iterables;

import pl.arieals.minigame.goldhunter.classes.xml.XmlClassDesc;
import pl.arieals.minigame.goldhunter.classes.xml.XmlClasses;
import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class CharacterClassManager
{
    private final Map<String, CharacterClass> characterClasses = new LinkedHashMap<>();
    
    private final MessagesBox messages;
    
    @Bean
    public CharacterClassManager(@Messages("GoldHunter") MessagesBox messages)
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
        XmlClassDesc classDesc = JAXB.unmarshal(getClass().getResourceAsStream("/classes/" + className + ".xml"), XmlClassDesc.class);
        characterClasses.put(className, classDesc.toCharacterClass(messages));
    }
    
    public CharacterClass getByName(String name)
    {
        return characterClasses.get(name.toLowerCase());
    }
    
    public CharacterClass getDefaultClass()
    {
        return Iterables.getFirst(characterClasses.values(), null);
    }
}
