package com.mitdroid.kenshi.Main;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PathUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void arrayStringCanBePassed() throws Exception {
        String[]a = new String[]{"sample A", "sample B", "sample C"};
        String[]b = new String[a.length];
        for(int index = 0; index < a.length; index++)
            b[index] = a[index];
        assertArrayEquals(a, b);
    }

    @Test
    public void arrayString() throws Exception {
        String[]a = new String[]{"sample A", "sample B", "sample C"};
        ArrayList<String>b = new ArrayList<>();
        for(int index = 0; index < a.length; index++)
            b.add(a[index]);
        assertArrayEquals(a, b.toArray());
    }

}