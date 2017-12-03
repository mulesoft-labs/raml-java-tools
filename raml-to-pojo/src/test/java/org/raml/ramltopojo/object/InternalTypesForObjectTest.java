package org.raml.ramltopojo.object;

import org.junit.Ignore;
import org.junit.Test;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContextImpl;
import org.raml.ramltopojo.RamlLoader;
import org.raml.v2.api.model.v10.api.Api;

import static org.raml.ramltopojo.RamlLoader.findTypes;

/**
 * Created. There, you have it.
 */
public class InternalTypesForObjectTest {

    @Test @Ignore
    public void simplest() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler(findTypes("foo", api.types()));

        CreationResult r = handler.create(new GenerationContextImpl(api));

    }
}
