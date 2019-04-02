package org.diorite.commons.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;

import org.junit.jupiter.api.Test;

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
        
        assertFalse(DioriteFileUtils.contains(f1, f1));
        assertFalse(DioriteFileUtils.contains(f1, f2));
        assertTrue(DioriteFileUtils.contains(f1, f3));
        assertTrue(DioriteFileUtils.contains(f1, f4));
        assertFalse(DioriteFileUtils.contains(f1, f5));
    }
}
