package org.raml.pojotoraml;

import org.junit.Test;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.pojotoraml.field.FieldClassParser;
import org.raml.pojotoraml.plugins.AdditionalPropertiesAdjuster;
import org.raml.simpleemitter.Emitter;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.phase.GrammarPhase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImplTest {
    @Test
    public void simpleStuff() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), new AdjusterFactory() {
            @Override
            public RamlAdjuster createAdjuster(Class<?> clazz) {
                return new AdditionalPropertiesAdjuster();
            }
        });
        Result types =  pojoToRaml.classToRaml(Fun.class);

        Api api = createApi(types);

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

    @Test
    public void enumeration() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        Result types =  pojoToRaml.classToRaml(SimpleEnum.class);

        Api api = createApi(types);

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(1, buildTypes.size());
        assertEquals("SimpleEnum", buildTypes.get(0).name());
        assertArrayEquals(new String[] {"ONE", "TWO"}, ((StringTypeDeclaration) buildTypes.get(0)).enumValues().toArray(new String[0]));

        Emitter emitter = new Emitter();
        emitter.emit(api);
    }

    @Test
    public void name() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), AdjusterFactory.NULL_FACTORY);
        TypeBuilder builder = pojoToRaml.name(Fun.class.getMethod("stringMethod").getGenericReturnType());

        ObjectNode node = builder.buildNode();

        assertEquals("type: array", node.getChildren().get(0).toString());
    }

    protected Api createApi(Result types) throws IOException {
        RamlDocumentBuilder ramlDocumentBuilder = RamlDocumentBuilder
                .document()
                .baseUri("http://google.com")
                .title("hello")
                .version("1")
                .withTypes(types.allTypes().toArray(new TypeDeclarationBuilder[0]));

        Api api = ramlDocumentBuilder.buildModel();

        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
        Node node = ((NodeModel) api).getNode();
        Node checked = grammarPhase.apply(node);
        List<ErrorNode> errors = checked.findDescendantsWith(ErrorNode.class);
        for (ErrorNode error : errors) {
            System.err.println("error: " + error.getErrorMessage());
        }

        Emitter emitter = new Emitter();
        emitter.emit(api);

        return api;
    }
}