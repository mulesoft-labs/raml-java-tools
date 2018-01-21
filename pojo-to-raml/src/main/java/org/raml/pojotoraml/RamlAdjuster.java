package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public interface RamlAdjuster {

    RamlAdjuster NULL_ADJUSTER = new RamlAdjuster.Helper();

    class Helper implements RamlAdjuster {

        @Override
        public TypeBuilder adjustType(Type type, TypeBuilder builder) {
            return builder;
        }

        @Override
        public String adjustTypeName(Class<?> aClass, String name, ClassParser parser) {
            return name;
        }

        @Override
        public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            return typePropertyBuilder;
        }

        @Override
        public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            return typePropertyBuilder;
        }
    }

    TypeBuilder adjustType(Type type, TypeBuilder builder);
    String adjustTypeName(Class<?> aClass, String name, ClassParser parser);
    TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);
    TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);
}
