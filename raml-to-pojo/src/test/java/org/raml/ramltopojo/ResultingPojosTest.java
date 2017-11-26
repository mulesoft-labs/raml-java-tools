package org.raml.ramltopojo;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created. There, you have it.
 */
public class ResultingPojosTest extends UnitTest {

    @Mock
    private CreationResult result;

    @Mock
    private GenerationContextImpl generationContext;

    @Test
    public void createAllTypes() throws Exception {

        ResultingPojos pojos = new ResultingPojos(generationContext);
        pojos.addNewResult(result);

        pojos.createAllTypes("/tmp/fun");

        verify(generationContext).createTypes("/tmp/fun");
        verify(result).createType("/tmp/fun");
    }


    @Test
    public void createFoundTypes() throws Exception {

        ResultingPojos pojos = new ResultingPojos(generationContext);
        pojos.addNewResult(result);

        pojos.createFoundTypes("/tmp/fun");

        verify(result).createType("/tmp/fun");
        verify(generationContext, never()).createTypes("/tmp/fun");
    }
}