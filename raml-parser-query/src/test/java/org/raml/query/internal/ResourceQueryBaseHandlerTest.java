package org.raml.query.internal;

import org.raml.query.Selector;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/30/17.
 * Just potential zeroes and ones
 */
public class ResourceQueryBaseHandlerTest extends UnitTest {

    @Mock
    Resource resource;

    @Mock
    private Selector<Resource> selector;

    @Mock
    private com.google.common.collect.FluentIterable<Resource> fluentIterator;

    @Test
    public void queryFor() throws Exception {

        when(selector.fromResource(resource)).thenReturn(fluentIterator);
        ResourceQueryBaseHandler handler = new ResourceQueryBaseHandler(resource);
        FluentIterable<Resource> iterable = handler.queryFor(selector);

        assertNotNull(iterable);
        verify(selector).fromResource(resource);
    }

}
