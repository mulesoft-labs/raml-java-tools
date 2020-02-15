package org.raml.ramltopojo;

import org.junit.Before;
import webapi.WebApiParser;

import java.util.concurrent.ExecutionException;

/**
 * Created. There, you have it.
 */
public class UtilsTest {

    @Before
    public void before() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();
    }

}