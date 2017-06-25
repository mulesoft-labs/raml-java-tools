package org.raml.query;

import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Collections;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jpbelang on 2017-05-29.
 */
public class AllResourceSelectorTest extends UnitTest {

    @Mock
    Api api;

    @Mock
    Resource resource;

    @Mock
    Resource subResource;

    @Test
    public void fromApi() throws Exception {

        when(api.resources()).thenReturn(Collections.singletonList(resource));
        when(resource.resources()).thenReturn(Collections.singletonList(subResource));

        AllResourceSelector allResourceSelector = new AllResourceSelector();
        FluentIterable<Resource> apiElements = allResourceSelector.fromApi(api);
        assertThat(apiElements, containsInAnyOrder(resource, subResource));

        verify(api).resources();
        verify(resource).resources();
        verify(subResource).resources();
    }

    @Test
    public void fromResource() throws Exception {
        when(resource.resources()).thenReturn(Collections.singletonList(subResource));

        AllResourceSelector allResourceSelector = new AllResourceSelector();
        FluentIterable<Resource> apiElements = allResourceSelector.fromResource(resource);

        assertThat(apiElements, containsInAnyOrder(subResource));
        verify(resource).resources();
        verify(subResource).resources();
    }

}
