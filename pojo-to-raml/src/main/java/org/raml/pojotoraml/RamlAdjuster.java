package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created. There, you have it.
 */
public interface RamlAdjuster {

    RamlAdjuster NULL_ADJUSTER = new RamlAdjuster.Helper();

    class Helper implements RamlAdjuster {

        @Override
        public String adjustEnumValue(Class<?> type, String name) {
            return name;
        }

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

    class Composite implements RamlAdjuster {

        private final  Collection<RamlAdjuster> adjusters;

        public Composite(Collection<RamlAdjuster> adjusters) {
            this.adjusters = adjusters;
        }

        @Override
        public String adjustEnumValue(Class<?> type, String name) {

            String val = name;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustEnumValue(type, val);
            }
            return val;
        }

        @Override
        public TypeBuilder adjustType(Type type, TypeBuilder builder) {
            TypeBuilder val = builder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustType(type, val);
            }
            return val;
        }

        @Override
        public String adjustTypeName(Class<?> aClass, String name, ClassParser parser) {
            String val = name;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustTypeName(aClass, val, parser);
            }
            return val;
        }

        @Override
        public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            TypePropertyBuilder val = typePropertyBuilder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustScalarProperty(typeDeclaration, property, val);
            }
            return val;
        }

        @Override
        public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            TypePropertyBuilder val = typePropertyBuilder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustComposedProperty(typeDeclaration, property, val);
            }
            return val;
        }
    }
    String adjustEnumValue(Class<?> type, String name);
    TypeBuilder adjustType(Type type, TypeBuilder builder);
    String adjustTypeName(Class<?> aClass, String name, ClassParser parser);
    TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);
    TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);
}
