package pl.north93.northplatform.discord;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@XmlRootElement(name = "discordbot")
@XmlAccessorType(XmlAccessType.NONE)
public class DiscordBotConfig
{
    @XmlElement
    private String token;

    @XmlElement
    private String rewardsChannel;
}
