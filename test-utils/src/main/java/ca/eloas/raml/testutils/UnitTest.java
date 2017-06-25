package ca.eloas.raml.testutils;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by jpbelang on 2017-05-29.
 */
public class UnitTest {

    @Before
    public void mockito() {

        MockitoAnnotations.initMocks(this);
    }
}
