package org.raml.ramltopojo;

import amf.client.model.document.BaseUnit;
import amf.client.model.document.Module;
import amf.client.model.domain.DomainElement;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.Shape;
import org.junit.Before;
import org.junit.Test;
import org.raml.testutils.UnitTest;
import webapi.WebApiDocument;
import webapi.WebApiParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;

/**
 * Created. There, you have it.
 */
public class TypeFetchersTest extends UnitTest{

    @Before
    public void before() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();
    }

    @Test
    public void fromTypes() throws Exception {

        List<DomainElement> ts = Arrays.asList(
                new NodeShape().withName("t1"),
                new NodeShape().withName("t2"),
                new NodeShape().withName("t3")
        );

        WebApiDocument api = (WebApiDocument) new WebApiDocument()
                .withDeclares(ts);

        Shape shape = TypeFetchers.fromTypes().fetchType(api, "t1");
        assertNotNull(shape);
    }


    @Test(expected = GenerationException.class)
    public void fromTypesFail() throws Exception {

        List<DomainElement> ts = Arrays.asList(
                new NodeShape().withName("t1"),
                new NodeShape().withName("t2"),
                new NodeShape().withName("t3")
        );

        WebApiDocument api = (WebApiDocument) new WebApiDocument()
                .withDeclares(ts);

        TypeFetchers.fromTypes().fetchType(api, "nosuchtype");
    }

    @Test
    public void fromLibraries() throws Exception {

        List<DomainElement> ts = Arrays.asList(
                new NodeShape().withName("t1"),
                new NodeShape().withName("t2"),
                new NodeShape().withName("t3")
        );

        Module module = new Module();
        module.withDeclares(ts);
        List<BaseUnit> references = Collections.singletonList(module);
        WebApiDocument api = (WebApiDocument) new WebApiDocument()
                .withReferences(references);

        Shape typeDeclaration1 = TypeFetchers.fromLibraries().fetchType(api, "t1");
        Shape typeDeclaration2 = TypeFetchers.fromLibraries().fetchType(api, "t2");
        Shape typeDeclaration3 = TypeFetchers.fromLibraries().fetchType(api, "t3");

        assertNotNull(typeDeclaration1);
        assertNotNull(typeDeclaration2);
        assertNotNull(typeDeclaration3);

    }

    @Test
    public void fromAnywhere() throws Exception {

        List<DomainElement> ts = Arrays.asList(

                new NodeShape().withName("t4"),
                new NodeShape().withName("t5"),
                new NodeShape().withName("t6")
        );

        Module module1 = new Module();
        module1.withDeclares(Collections.singletonList(new NodeShape().withName("t1")));

        Module module3= new Module();
        module3.withDeclares(Collections.singletonList(new NodeShape().withName("t3")));

        Module module2= new Module();
        module2.withDeclares(Collections.singletonList(new NodeShape().withName("t2")));

        module2.withReferences(Collections.singletonList(module3));
        List<BaseUnit> references = Arrays.asList(module1, module2);

        WebApiDocument api = (WebApiDocument) new WebApiDocument()
                .withDeclares(ts);
        api.withReferences(references);

        Shape typeDeclaration1 = TypeFetchers.fromAnywhere().fetchType(api, "t1");
        Shape typeDeclaration2 = TypeFetchers.fromAnywhere().fetchType(api, "t2");
        Shape typeDeclaration3 = TypeFetchers.fromAnywhere().fetchType(api, "t3");
        Shape typeDeclaration4 = TypeFetchers.fromAnywhere().fetchType(api, "t4");
        Shape typeDeclaration5 = TypeFetchers.fromAnywhere().fetchType(api, "t5");
        Shape typeDeclaration6 = TypeFetchers.fromAnywhere().fetchType(api, "t6");

        assertNotNull(typeDeclaration1);
        assertNotNull(typeDeclaration2);
        assertNotNull(typeDeclaration3);
        assertNotNull(typeDeclaration4);
        assertNotNull(typeDeclaration5);
        assertNotNull(typeDeclaration6);
    }

    /*
    @Test(expected = GenerationException.class)
    public void failFromLibraries() throws Exception {

        when(api.types()).thenReturn(Arrays.asList(t4, t5, t6));

        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        TypeFetchers.fromLibraries().fetchType(api, "t4");

    }
*/

}