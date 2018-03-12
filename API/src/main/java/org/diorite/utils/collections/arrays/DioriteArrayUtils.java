/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.diorite.utils.collections.arrays;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import org.diorite.utils.collections.maps.ConcurrentIdentityHashMap;

/**
 * Contains some basic array utils
 */
public final class DioriteArrayUtils
{
    private static final Map<Class<?>, Object> arrayMaps      = new ConcurrentIdentityHashMap<>(20);
    /**
     * Empty array of boolean type.
     */
    public static final  boolean[]             EMPTY_BOOLEANS = getEmptyArrayByArrayClass(boolean[].class);
    /**
     * Empty array of char type.
     */
    public static final  char[]                EMPTY_CHARS    = getEmptyArrayByArrayClass(char[].class);
    /**
     * Empty array of byte type.
     */
    public static final  byte[]                EMPTY_BYTES    = getEmptyArrayByArrayClass(byte[].class);
    /**
     * Empty array of short type.
     */
    public static final  short[]               EMPTY_SHORTS   = getEmptyArrayByArrayClass(short[].class);
    /**
     * Empty array of int type.
     */
    public static final  int[]                 EMPTY_INTS     = getEmptyArrayByArrayClass(int[].class);
    /**
     * Empty array of long type.
     */
    public static final  long[]                EMPTY_LONGS    = getEmptyArrayByArrayClass(long[].class);
    /**
     * Empty array of float type.
     */
    public static final  float[]               EMPTY_FLOATS   = getEmptyArrayByArrayClass(float[].class);
    /**
     * Empty array of double type.
     */
    public static final  double[]              EMPTY_DOUBLES  = getEmptyArrayByArrayClass(double[].class);
    /**
     * Empty array of Object type.
     */
    public static final  Object[]              EMPTY_OBJECT   = getEmptyArrayByArrayClass(Object[].class);
    /**
     * Empty array of String type.
     */
    public static final  String[]              EMPTY_STRINGS  = getEmptyArrayByArrayClass(String[].class);

    private DioriteArrayUtils()
    {
    }

    /**
     * Returns empty array of given type, must be an object type, for prymitives use {@link #getEmptyArray(Class)} or {@link #getEmptyArrayByArrayClass(Class)} <br>
     * All arrays are cached, method will always return this same array for this same class.
     *
     * @param clazz type of array, must be class of object.
     * @param <T>   type of array.
     *
     * @return empty array of given type.
     *
     * @throws IllegalArgumentException if given class is primitive type. {@link Class#isPrimitive()}
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] getEmptyObjectArray(final Class<T> clazz) throws IllegalArgumentException
    {
        if (clazz.isPrimitive())
        {
            throw new IllegalArgumentException("Can't create array of primitive type: " + clazz);
        }
        final Object o = arrayMaps.get(clazz);
        if (o != null)
        {
            return (T[]) o;
        }
        final T[] array = (T[]) Array.newInstance(clazz, 0);
        arrayMaps.put(clazz, array);
        return array;
    }

    /**
     * Returns array of given type as object. (to support primitive types) <br>
     * All arrays are cached, method will always return this same array for this same class.
     *
     * @param clazz type of array.
     *
     * @return empty array of given type.
     */
    @SuppressWarnings("unchecked")
    public static Object getEmptyArray(final Class<?> clazz)
    {
        final Object o = arrayMaps.get(clazz);
        if (o != null)
        {
            return o;
        }
        final Object array = Array.newInstance(clazz, 0);
        arrayMaps.put(clazz, array);
        return array;
    }

    /**
     * Returns array of given type, given class must be type of array, like int[].class <br>
     * All arrays are cached, method will always return this same array for this same class.
     *
     * @param clazz type of array, must be class of array type.
     * @param <T>   type of array.
     *
     * @return empty array of given type.
     *
     * @throws IllegalArgumentException if given class isn't array. {@link Class#isArray()}
     */
    @SuppressWarnings("unchecked")
    public static <T> T getEmptyArrayByArrayClass(final Class<T> clazz) throws IllegalArgumentException
    {
        if (! clazz.isArray())
        {
            throw new IllegalArgumentException("Class must be array type: " + clazz);
        }
        final Object o = arrayMaps.get(clazz);
        if (o != null)
        {
            return (T) o;
        }
        final T array = (T) Array.newInstance(clazz.getComponentType(), 0);
        arrayMaps.put(clazz, array);
        return array;
    }

    /**
     * Returns array of given type, must be an object type, for prymitives use {@link #getEmptyArray(Class)} or {@link #getEmptyArrayByArrayClass(Class)} <br>
     * If given size is equals to 0, result of {@link #getEmptyArray(Class)} will be returned.
     *
     * @param clazz type of array, must be class of object.
     * @param size  size of array.
     * @param <T>   type of array.
     *
     * @return array of given type and size.
     *
     * @throws IllegalArgumentException if given class is primitive type. {@link Class#isPrimitive()}
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newObjectArray(final Class<T> clazz, final int size) throws IllegalArgumentException
    {
        if (size == 0)
        {
            return getEmptyObjectArray(clazz);
        }
        if (clazz.isPrimitive())
        {
            throw new IllegalArgumentException("Can't create array of primitive type: " + clazz);
        }
        return (T[]) Array.newInstance(clazz, size);
    }

    /**
     * Returns array of given type as object. (to support primitive types) <br>
     * If given size is equals to 0, result of {@link #getEmptyArray(Class)} will be returned.
     *
     * @param clazz type of array.
     * @param size  size of array.
     *
     * @return array of given type and size.
     */
    @SuppressWarnings("unchecked")
    public static Object newArray(final Class<?> clazz, final int size)
    {
        if (size == 0)
        {
            return getEmptyArray(clazz);
        }
        return Array.newInstance(clazz, size);
    }


    /**
     * Returns new array of given type, given class must be type of array, like int[].class <br>
     * If given size is equals to 0, result of {@link #getEmptyArrayByArrayClass(Class)} will be returned.
     *
     * @param clazz type of array, must be class of array type.
     * @param size  size of array.
     * @param <T>   type of array.
     *
     * @return array of given type and size.
     *
     * @throws IllegalArgumentException if given class isn't array. {@link Class#isArray()}
     */
    @SuppressWarnings("unchecked")
    public static <T> T newArrayByArrayClass(final Class<T> clazz, final int size) throws IllegalArgumentException
    {
        if (size == 0)
        {
            return getEmptyArrayByArrayClass(clazz);
        }
        if (! clazz.isArray())
        {
            throw new IllegalArgumentException("Class must be array type: " + clazz);
        }
        return (T) Array.newInstance(clazz.getComponentType(), size);
    }

    /**
     * Joins arrays together, if only one array contains elements it will be returned without coping anything. <br>
     * If given array or arrays is empty, new empty array will be returned.
     *
     * @param arrayFunction function that create array of given size, just T[]::new.
     * @param arrays        arrays to join.
     * @param <T>           type of array.
     *
     * @return new joined array, or one of given ones if other arrays were empty.
     */
    @SafeVarargs
    public static <T> T[] join(final IntFunction<T[]> arrayFunction, final T[]... arrays)
    {
        if (arrays.length == 0)
        {
            return arrayFunction.apply(0);
        }
        if (arrays.length == 1)
        {
            return arrays[0];
        }
        if (arrays.length == 2)
        {
            return join(arrayFunction, arrays[0], arrays[1]);
        }
        T[] notNull = null;
        int finalSize = 0;
        int nullArrays = 0;
        final List<T> list = new ArrayList<>(arrays.length * 10);
        for (final T[] array : arrays)
        {
            if ((array == null) || (array.length == 0))
            {
                nullArrays++;
            }
            else
            {
                notNull = array;
                finalSize += array.length;
                Collections.addAll(list, array);
            }
        }
        if (nullArrays == arrays.length)
        {
            return arrayFunction.apply(0);
        }
        if (nullArrays == (arrays.length - 1))
        {
            return notNull;
        }
        return list.toArray(arrayFunction.apply(list.size()));
    }

    /**
     * Joins arrays together, if only one array contains elements it will be returned without coping anything. <br>
     * If given array or arrays is empty, new empty array will be returned.<br>
     * NOTE: this method use reflections!
     *
     * @param arrays        arrays to join.
     * @param <T>           type of array.
     *
     * @return new joined array, or one of given ones if other arrays were empty.
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> T[] join(final T[]... arrays)
    {
        return join(i -> (T[]) newArray(arrays.getClass().getComponentType().getComponentType(), i), arrays);
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayFunction function that create array of given size, just T[]::new.
     * @param arrayA        first array.
     * @param arrayB        second array.
     * @param <T>           type of array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    @SafeVarargs
    public static <T> T[] join(final IntFunction<T[]> arrayFunction, final T[] arrayA, final T... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final T[] array = arrayFunction.apply(arrayA.length + arrayB.length);
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything. <br>
     * NOTE: this method use reflections!
     *
     * @param arrayA first array.
     * @param arrayB second array.
     * @param <T>    type of array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> T[] join(final T[] arrayA, final T... arrayB)
    {
        return join(i -> (T[]) newArray(arrayA.getClass().getComponentType(), i), arrayA, arrayB);
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static boolean[] join(final boolean[] arrayA, final boolean... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final boolean[] array = new boolean[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static char[] join(final char[] arrayA, final char... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final char[] array = new char[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static byte[] join(final byte[] arrayA, final byte... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final byte[] array = new byte[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static short[] join(final short[] arrayA, final short... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final short[] array = new short[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static int[] join(final int[] arrayA, final int... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final int[] array = new int[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static long[] join(final long[] arrayA, final long... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final long[] array = new long[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static float[] join(final float[] arrayA, final float... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final float[] array = new float[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }

    /**
     * Joins 2 arrays together, if any array is null or empty then other array will be retuned without coping anything.
     *
     * @param arrayA first array.
     * @param arrayB second array.
     *
     * @return new joined array, or one of given ones if any of arrays was empty.
     */
    public static double[] join(final double[] arrayA, final double... arrayB)
    {
        if ((arrayB == null) || (arrayB.length == 0))
        {
            return arrayA;
        }
        if ((arrayA == null) || (arrayA.length == 0))
        {
            return arrayB;
        }
        final double[] array = new double[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, array, 0, arrayA.length);
        System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
        return array;
    }
}
