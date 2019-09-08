package org.raml.testutils;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import webapi.WebApiParser;

import java.util.concurrent.ExecutionException;

/**
 * Created by jpbelang on 2017-05-29.
 */
public class UnitTest {

    @Before
    public void mockito() throws ExecutionException, InterruptedException {

        MockitoAnnotations.initMocks(this);
        WebApiParser.init().get();
    }
}
