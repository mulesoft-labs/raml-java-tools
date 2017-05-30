package ca.eloas.raml.query;

import ca.eloas.raml.testutils.UnitTest;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.v2.api.model.v10.api.Api;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Created by jpbelang on 2017-05-29.
 */
public class TopResourceSelectorTest extends UnitTest{

    @Mock
    Api api;

    @Test
    public void fromApi() throws Exception {

        TopResourceSelector topResourceSelector = new TopResourceSelector();
        topResourceSelector.fromApi(api);

        verify(api).resources();
    }

    @Test
    public void fromResource() throws Exception {
    }

}