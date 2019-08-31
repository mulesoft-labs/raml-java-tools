package org.raml.ramltopojo;

import amf.client.model.domain.NodeShape;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.raml.testutils.UnitTest;
import webapi.WebApiParser;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertThat;

/**
 * Created. There, you have it.
 */
public class GenerationContextImplTest extends UnitTest{

    NodeShape type1, type2, type3, type4;

    @Test
    public void setupTypeHierarchy() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();

        type1 = (NodeShape) new NodeShape().withInheritsObject("object").withName("type1");
        type2 = (NodeShape) new NodeShape().withInheritsObject("object").withName("type2");
        type3 = (NodeShape) new NodeShape().withInheritsObject("object").withName("type3");
        type4 = (NodeShape) new NodeShape().withInheritsObject("object").withName("type4");

        type1 = (NodeShape) type1.withInherits(Arrays.asList(type2, type3));
        type2 = (NodeShape) type2.withInherits(Arrays.asList(type3, type4));

        GenerationContextImpl impl = new GenerationContextImpl(null);
        impl.setupTypeHierarchy(type1);

        assertThat(impl.childClasses("type2"), Matchers.contains(Matchers.equalTo("type1")));

        assertThat(impl.childClasses("type3"), Matchers.contains(Matchers.equalTo("type1"), Matchers.equalTo("type2")));
    }
}