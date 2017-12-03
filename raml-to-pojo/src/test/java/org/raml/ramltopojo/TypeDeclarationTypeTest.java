package org.raml.ramltopojo;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.junit.Ignore;
import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class TypeDeclarationTypeTest {


    @Test
    public void internalIntIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "internalInt");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }


    @Test
    public void simpleObjectIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "unextended");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void extendedObjectIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "extendedFromOne");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void extendedObjectWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "extendedFromOneWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void objectWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "objectWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void multiInheritanceWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "multiInheritanceWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void multiInheritanceWithoutExtraPropertiesIsNotNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "multiInheritanceWithoutExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test @Ignore
    public void unionWithExtraPropertiesIsNewInlineType() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-types.raml"));
        ObjectTypeDeclaration decl = RamlLoader.findTypes("foo", api.types());
        TypeDeclaration property = findProperty(decl, "unionWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    protected TypeDeclaration findProperty(ObjectTypeDeclaration decl, final String propertyName) {
        return FluentIterable.from(decl.properties()).firstMatch(new Predicate<TypeDeclaration>() {
            @Override
            public boolean apply(@Nullable TypeDeclaration input) {
                return propertyName.equals(input.name());
            }
        }).get();
    }

}