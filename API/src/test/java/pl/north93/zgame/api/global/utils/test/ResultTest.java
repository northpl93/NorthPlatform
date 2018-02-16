package pl.north93.zgame.api.global.utils.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.north93.zgame.api.global.utils.Result;

public class ResultTest
{
    private boolean shouldBeTrue;
    private boolean shouldBeFalse;
    
    @Before
    public void prepareTest()
    {
        shouldBeTrue = false;
        shouldBeFalse = false;
    }
    
    @Test
    public void testSuccess()
    {
        Result.SUCCESS.whenSuccess(this::thisShouldBeCalled).whenFailture(this::thisNeverShouldBeCalled);
        Assert.assertTrue(shouldBeTrue);
        Assert.assertFalse(shouldBeFalse);
    }
    
    @Test
    public void testFailture()
    {
        Result.FAILTURE.whenSuccess(this::thisNeverShouldBeCalled).whenFailture(this::thisShouldBeCalled);
        Assert.assertTrue(shouldBeTrue);
        Assert.assertFalse(shouldBeFalse);
    }
    
    private void thisShouldBeCalled()
    {
        shouldBeTrue = true;
    }
    
    private void thisNeverShouldBeCalled()
    {
        shouldBeFalse = true;
    }
}
