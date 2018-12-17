package com.app.dumbo.iwater;

import org.junit.Test;

import java.text.DecimalFormat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void test(){
        String str="{\"recordTime\":\"2018-09-12 14:50:23\",\"siteId\":\"1\",\"ph\":0.00,\"temperature\":9.82,\"dissolvedOxygen\":0.00,\"conductivity\":0.00,\"turbidity\":4.70,\"latitude\":\"39.000000\",\"longitude\":\"117.000000\",\"lngType\":\"E\",\"latType\":\"N\"}";
        System.out.println(str.length());
    }
}