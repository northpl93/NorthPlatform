package pl.north93.zgame.api.global.redis.rpc.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

public class ReflectUtil
{
    private static class MethodOffset implements Comparable<MethodOffset>
    {
        MethodOffset(Method _method, int _offset)
        {
            this.method = _method;
            this.offset = _offset;
        }

        @Override
        public int compareTo(final MethodOffset target)
        {
            return this.offset - target.offset;
        }

        Method method;
        int    offset;
    }

    static class ByLength implements Comparator<Method>
    {

        @Override
        public int compare(Method a, Method b)
        {
            return b.getName().length() - a.getName().length();
        }
    }

    /**
     * Grok the bytecode to get the declared order
     */
    public static Method[] getDeclaredMethodsInOrder(Class clazz)
    {
        Method[] methods = null;
        try
        {
            String resource = clazz.getName().replace('.', '/') + ".class";

            methods = clazz.getDeclaredMethods();

            InputStream is = clazz.getClassLoader().getResourceAsStream(resource);

            if (is == null)
            {
                return methods;
            }

            java.util.Arrays.sort(methods, new ByLength());
            ArrayList<byte[]> blocks = new ArrayList<byte[]>();
            int length = 0;
            for (; ; )
            {
                byte[] block = new byte[16 * 1024];
                int n = is.read(block);
                if (n > 0)
                {
                    if (n < block.length)
                    {
                        block = java.util.Arrays.copyOf(block, n);
                    }
                    length += block.length;
                    blocks.add(block);
                }
                else
                {
                    break;
                }
            }

            byte[] data = new byte[length];
            int offset = 0;
            for (byte[] block : blocks)
            {
                System.arraycopy(block, 0, data, offset, block.length);
                offset += block.length;
            }

            String sdata = new String(data, java.nio.charset.Charset.forName("UTF-8"));
            int lnt = sdata.indexOf("LineNumberTable");
            if (lnt != - 1)
            {
                sdata = sdata.substring(lnt + "LineNumberTable".length() + 3);
            }
            int cde = sdata.lastIndexOf("SourceFile");
            if (cde != - 1)
            {
                sdata = sdata.substring(0, cde);
            }

            MethodOffset mo[] = new MethodOffset[methods.length];


            for (int i = 0; i < methods.length; ++ i)
            {
                int pos = - 1;
                for (; ; )
                {
                    pos = sdata.indexOf(methods[i].getName(), pos);
                    if (pos == - 1)
                    {
                        break;
                    }
                    boolean subset = false;
                    for (int j = 0; j < i; ++ j)
                    {
                        if (mo[j].offset >= 0 &&
                                    mo[j].offset <= pos &&
                                    pos < mo[j].offset + mo[j].method.getName().length())
                        {
                            subset = true;
                            break;
                        }
                    }
                    if (subset)
                    {
                        pos += methods[i].getName().length();
                    }
                    else
                    {
                        break;
                    }
                }
                mo[i] = new MethodOffset(methods[i], pos);
            }
            java.util.Arrays.sort(mo);
            for (int i = 0; i < mo.length; ++ i)
            {
                methods[i] = mo[i].method;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return methods;
    }
}
