package pl.north93.northplatform.api.global.utils.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.north93.northplatform.api.global.utils.Result;

public class ResultTest
{
    private boolean shouldBeTrue;
    private boolean shouldBeFalse;
    
    @BeforeEach
    public void prepareTest()
    {
        shouldBeTrue = false;
        shouldBeFalse = false;
    }
    
    @Test
    public void testSuccess()
    {
        Result.SUCCESS.whenSuccess(this::thisShouldBeCalled).whenFailture(this::thisNeverShouldBeCalled);
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);
    }
    
    @Test
    public void testFailture()
    {
        Result.FAILTURE.whenSuccess(this::thisNeverShouldBeCalled).whenFailture(this::thisShouldBeCalled);
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);
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
