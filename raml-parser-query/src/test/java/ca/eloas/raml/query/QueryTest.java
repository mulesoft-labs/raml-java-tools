package ca.eloas.raml.query;

import ca.eloas.raml.testutils.UnitTest;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/30/17.
 * Just potential zeroes and ones
 */
public class QueryTest extends UnitTest {

    @Mock
    private QueryBase base;

    @Mock
    private Selector<Resource> selector;

    @Mock
    private FluentIterable<Resource> fluentList;

    @Test
    public void select() throws Exception {

        when(base.queryFor(selector)).thenReturn(fluentList);

        Query query = new Query(base);
        FluentIterable<Resource> resourceList = query.select(selector);

        assertSame(resourceList, fluentList);
    }

}
