package com.fun.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created. There, you have it.
 */
public class EaterImplTest {


    @Test
    public void theEquals() {

        EaterImpl first = new EaterImpl();
        first.setName("One");

        EaterImpl second = new EaterImpl();
        second.setName("Two");

        EaterImpl firstPrime = new EaterImpl();
        firstPrime.setName("One");

        assertEquals(first, firstPrime);
        assertNotEquals(first, second);
    }

    @Test
    public void theToString() {

        EaterImpl first = new EaterImpl();
        first.setName("One");
        first.setCount(7);
        assertEquals("name = One, count = 7, other = [null]", first.toString());
    }

}