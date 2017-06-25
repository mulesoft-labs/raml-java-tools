package ca.eloas.raml.query;

import ca.eloas.raml.testutils.UnitTest;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Created by jpbelang on 2017-05-29.
 */
public class TopResourceSelectorTest extends UnitTest{

    @Mock
    Api api;

    @Mock
    Resource resource;

    @Test
    public void fromApi() throws Exception {

        TopResourceSelector topResourceSelector = new TopResourceSelector();
        FluentIterable<Resource> apiElements = topResourceSelector.fromApi(api);
        assertEquals(0, apiElements.size());
        verify(api).resources();
    }

    @Test
    public void fromResource() throws Exception {
        TopResourceSelector topResourceSelector = new TopResourceSelector();
        FluentIterable<Resource> resourceElements = topResourceSelector.fromResource(resource);
        assertEquals(0, resourceElements.size());
        verify(resource).resources();
    }

}
