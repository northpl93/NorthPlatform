package pl.north93.zgame.api.global.messages;

import org.junit.Assert;
import org.junit.Test;

public class ChatColorUtilsTest
{
    @Test
    public void testGetLastColorWithOnlyColor()
    {
        Assert.assertEquals("§d", ChatColorUtils.getLastColors("§cSomething§dlastColor"));
        Assert.assertEquals("§d", ChatColorUtils.getLastColors("§cSomething§d§zsdssss"));
        Assert.assertEquals("§d", ChatColorUtils.getLastColors("§cSoemthidn§e§d"));
    }
    
    @Test
    public void testGetLastColorWithFormatting()
    {
        Assert.assertEquals("§d§l§m", ChatColorUtils.getLastColors("§c§n§dsss§lmmm§m"));
    }
}
