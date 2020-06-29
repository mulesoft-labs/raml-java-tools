package org.raml.pojotoraml;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.ScalarShape;
import org.junit.Before;
import org.junit.Test;
import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.field.FieldClassParser;
import webapi.WebApiDocument;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
 /*   @Test
    public void simpleStuff() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), new AdjusterFactory() {
            @Override
            public RamlAdjuster createAdjuster(Class<?> clazz) {
                return new AdditionalPropertiesAdjuster();
            }
        });
        Result types =  pojoToRaml.classToRaml(Fun.class);

        WebApiDocument api = createApi(types);

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(3, buildTypes.size());
        assertEquals("Fun", buildTypes.get(0).name());
        assertEquals("SimpleEnum", buildTypes.get(1).name());
        assertEquals(9, ((ObjectTypeDeclaration)buildTypes.get(0)).properties().size());

        assertEquals("SubFun", buildTypes.get(2).name());
        assertEquals(1, ((ObjectTypeDeclaration)buildTypes.get(2)).properties().size());
    }

    @Test
    public void withInheritance() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(Inheriting.class);

        Api api = createApi(types);

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(2, buildTypes.size());
        assertEquals("Inheriting", buildTypes.get(0).name());
        assertEquals("Inherited", buildTypes.get(1).name());
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

        Api api = createApi(types);

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(3, buildTypes.size());
        assertEquals("MultipleInheriting", buildTypes.get(0).name());
        assertEquals("AnotherInherited", buildTypes.get(1).name());
        assertEquals("FirstInherited", buildTypes.get(2).name());
    }

    @Test
    public void scalarType() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(String.class);

        Api api = createApi(types);

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(0, buildTypes.size());

        Emitter emitter = new Emitter();
        emitter.emit(api);
    }
*/
    @Test
    public void enumeration() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(SimpleEnum.class);

        WebApiDocument api = createApi(types);

        List<AnyShape> buildTypes = api.declares().stream().map(x -> (AnyShape)x).collect(Collectors.toList());

        assertEquals(1, buildTypes.size());
        assertEquals("SimpleEnum", buildTypes.get(0).name());
       // assertArrayEquals(new String[] {"ONE", "TWO"}, ((ScalarShape) buildTypes.get(0)).dataType().toArray(new String[0]));
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
                .withTypes(types.allTypes().toArray(new DeclaredShapeBuilder[0]));

        WebApiDocument api = ramlDocumentBuilder.buildModel();

        return api;
    }
}