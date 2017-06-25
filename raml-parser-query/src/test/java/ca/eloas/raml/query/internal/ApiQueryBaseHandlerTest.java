package ca.eloas.raml.query.internal;

import ca.eloas.raml.query.Selector;
import ca.eloas.raml.testutils.UnitTest;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/30/17.
 * Just potential zeroes and ones
 */
public class ApiQueryBaseHandlerTest extends UnitTest {

    @Mock
    Api api;

    @Mock
    private Selector<Resource> selector;

    @Mock
    private FluentIterable<Resource> fluentIterator;

    @Test
    public void queryFor() throws Exception {

        when(selector.fromApi(api)).thenReturn(fluentIterator);
        ApiQueryBaseHandler handler = new ApiQueryBaseHandler(api);
        FluentIterable<Resource> iterable = handler.queryFor(selector);

        assertNotNull(iterable);
        verify(selector).fromApi(api);
    }

}
