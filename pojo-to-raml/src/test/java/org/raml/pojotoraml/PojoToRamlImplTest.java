package org.raml.pojotoraml;

import amf.client.model.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.field.FieldClassParser;
import org.raml.pojotoraml.plugins.AdditionalPropertiesAdjuster;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImplTest {

    @Before
    public void setup() {
        RamlDocumentBuilder ramlDocumentBuilder = RamlDocumentBuilder
                .document()
                .baseUri("http://google.com")
                .title("hello")
                .version("1");
        WebApiDocument d = ramlDocumentBuilder.buildModel();
    }

    @Test
    public void simpleStuff() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), clazz -> new AdditionalPropertiesAdjuster());
        Result types =  pojoToRaml.classToRaml(Fun.class);

        WebApiDocument api = createApi(types);

        List<AnyShape> buildTypes = api.declares().stream().map(x -> (AnyShape)x).collect(Collectors.toList());

        assertEquals(3, buildTypes.size());
        assertEquals("Fun", buildTypes.get(0).name().value());
        assertEquals("SimpleEnum", buildTypes.get(1).name().value());

        assertEquals("SubFun", buildTypes.get(2).name().value());

        assertEquals(9, ((NodeShape)buildTypes.get(0)).properties().size());
        assertEquals(1, ((NodeShape)buildTypes.get(2)).properties().size());
    }

    @Test
    public void withInheritance() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(Inheriting.class);

        WebApiDocument api = createApi(types);

        List<AnyShape> buildTypes = api.declares().stream().map(x -> (AnyShape)x).collect(Collectors.toList());

        assertEquals(2, buildTypes.size());
        assertEquals("Inheriting", buildTypes.get(0).name().value());
        assertEquals("Inherited", buildTypes.get(1).name().value());

        assertEquals(1, ((NodeShape)buildTypes.get(0)).properties().size());

    }


    @Test
    public void withMultipleInheritance() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(clazz -> new FieldClassParser() {
            @Override
            public Collection<Type> parentClasses(Class<?> sourceClass) {
                return Arrays.stream(clazz.getInterfaces()).collect(Collectors.toList());
            }
        }, AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(MultipleInheriting.class);

        WebApiDocument api = createApi(types);

        List<AnyShape> buildTypes = api.declares().stream().map(x -> (AnyShape)x).collect(Collectors.toList());

        assertEquals(3, buildTypes.size());
        assertEquals("MultipleInheriting", buildTypes.get(0).name().value());
        assertEquals("AnotherInherited", buildTypes.get(1).name().value());
        assertEquals("FirstInherited", buildTypes.get(2).name().value());
    }

    @Test
    public void scalarType() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(String.class);

        WebApiDocument api = createApi(types);

        List<AnyShape> buildTypes = api.declares().stream().map(x -> (AnyShape)x).collect(Collectors.toList());

        assertEquals(0, buildTypes.size());
    }

    @Test
    public void enumeration() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(SimpleEnum.class);

        WebApiDocument api = createApi(types);

        List<AnyShape> buildTypes = api.declares().stream().map(x -> (AnyShape)x).collect(Collectors.toList());

        assertEquals(1, buildTypes.size());
        assertEquals("SimpleEnum", buildTypes.get(0).name().value());
        assertArrayEquals(new String[] {"ONE", "TWO"}, buildTypes.get(0).values().stream().map(v -> ((ScalarNode)v).value().value()).toArray(String[]::new));
    }

    @Test
    public void name() throws Exception {



        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        TypeShapeBuilder builder = pojoToRaml.name(Fun.class.getMethod("stringMethod").getGenericReturnType());

        ArrayShape node = (ArrayShape) builder.buildNode();

        assertTrue(((ScalarShape)node.items()).dataType().value().contains("string"));
    }

    protected WebApiDocument createApi(Result types) throws IOException {
        RamlDocumentBuilder ramlDocumentBuilder = RamlDocumentBuilder
                .document()
                .baseUri("http://google.com")
                .title("hello")
                .version("1")
                .withTypes(() -> new ArrayList<>(types.allTypes()));

        WebApiDocument api = ramlDocumentBuilder.buildModel();
        try {
            System.err.println(Raml10.generateString(api).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return api;
    }
}