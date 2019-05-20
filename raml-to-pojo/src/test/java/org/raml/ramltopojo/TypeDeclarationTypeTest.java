package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class TypeDeclarationTypeTest extends UnitTest {

    @Mock
    IntegerTypeDeclaration integerTypeDeclaration;

    @Mock
    NumberTypeDeclaration numberTypeDeclaration;

    @Mock
    GenerationContext context;

    @Mock
    private ReferenceTypeHandlerPlugin plugin;


    @Test
    public void internalIntIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "internalInt");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }


    @Test
    public void simpleObjectIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "unextended");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void extendedObjectIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "extendedFromOne");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void extendedObjectWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "extendedFromOneWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void objectWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "objectWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void multiInheritanceWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "multiInheritanceWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void multiInheritanceWithoutExtraPropertiesIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "multiInheritanceWithoutExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void arraySimpleType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-array-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("father", api.types());

        assertFalse(TypeDeclarationType.isNewInlineType(findProperty(decl, "others")));
        assertFalse(TypeDeclarationType.isNewInlineType(findProperty(decl, "some")));
    }

    @Test
    public void inlineArrayOfDifferentArrayType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-array-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("mother", api.types());

        assertTrue(TypeDeclarationType.isNewInlineType(findProperty(decl, "complicatedChildren")));
    }


    @Test
    public void integerType() {

        createIntegerTypeNameSetup(integerTypeDeclaration, TypeDeclarationType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), eq(TypeName.INT));
    }

    @Test
    public void integerTypeWithByteFormat() {

        when(integerTypeDeclaration.format()).thenReturn("int8");
        createIntegerTypeNameSetup(integerTypeDeclaration, TypeDeclarationType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), eq(TypeName.BYTE));
    }

    @Test
    public void integerTypeWithDoubleFormat() {

        when(integerTypeDeclaration.format()).thenReturn("double");
        createIntegerTypeNameSetup(integerTypeDeclaration, TypeDeclarationType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), eq(TypeName.DOUBLE));
    }

    @Test
    public void numberType() {

        createIntegerTypeNameSetup(numberTypeDeclaration, TypeDeclarationType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), eq(ClassName.get(Number.class)));
    }

    @Test
    public void numberTypeWithByteFormat() {

        when(numberTypeDeclaration.format()).thenReturn("int8");
        createIntegerTypeNameSetup(numberTypeDeclaration, TypeDeclarationType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), eq(TypeName.BYTE));
    }

    @Test
    public void numberTypeWithDoubleFormat() {

        when(numberTypeDeclaration.format()).thenReturn("double");
        createIntegerTypeNameSetup(numberTypeDeclaration, TypeDeclarationType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), eq(TypeName.DOUBLE));
    }

    private void createIntegerTypeNameSetup(TypeDeclaration integerTypeDeclaration, TypeDeclarationType typeDeclarationType) {
        when(context.pluginsForReferences(integerTypeDeclaration)).thenReturn(plugin);
        TypeHandler handler = typeDeclarationType.createHandler("foo", TypeDeclarationType.INTEGER, null /*integerTypeDeclaration*/);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
    }

    @Test @Ignore
    public void unionWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"), ".");
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "unionWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Deprecated() /* return optional.....*/
    protected TypeDeclaration findProperty(ObjectTypeDeclaration decl, final String propertyName) {
        return decl.properties().stream().filter(input -> propertyName.equals(input.name())).findFirst().orElse(null);
    }

}