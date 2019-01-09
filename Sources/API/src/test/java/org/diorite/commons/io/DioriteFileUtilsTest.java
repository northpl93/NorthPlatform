package org.diorite.commons.io;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class DioriteFileUtilsTest
{
    @Test
    public void testContains()
    {
        File f1 = new File("test/path");
        File f2 = new File("test/another");
        File f3 = new File("test/path/inner");
        File f4 = new File("test/path/inner2/something");
        File f5 = new File("test");
        
        Assert.assertFalse(DioriteFileUtils.contains(f1, f1));
        Assert.assertFalse(DioriteFileUtils.contains(f1, f2));
        Assert.assertTrue(DioriteFileUtils.contains(f1, f3));
        Assert.assertTrue(DioriteFileUtils.contains(f1, f4));
        Assert.assertFalse(DioriteFileUtils.contains(f1, f5));
    }
}
