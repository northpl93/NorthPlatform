package pl.north93.northplatform.api.global.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

public class ChatColorUtilsTest
{
    @Test
    public void testGetLastColorWithOnlyColor()
    {
        assertEquals("§d", ChatColorUtils.getLastColors("§cSomething§dlastColor"));
        assertEquals("§d", ChatColorUtils.getLastColors("§cSomething§d§zsdssss"));
        assertEquals("§d", ChatColorUtils.getLastColors("§cSoemthidn§e§d"));
    }
    
    @Test
    public void testGetLastColorWithFormatting()
    {
        assertEquals("§d§l§m", ChatColorUtils.getLastColors("§c§n§dsss§lmmm§m"));
    }
}
