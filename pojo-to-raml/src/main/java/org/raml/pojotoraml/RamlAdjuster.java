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
        public TypeBuilder adjustType(Type type, String typeName, TypeBuilder builder) {
            return builder;
        }

        @Override
        public String adjustTypeName(Class<?> aClass, String name) {
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

        @Override
        public TypeBuilder adjustForUnknownType(Type type) {
            throw new IllegalArgumentException("cannot parse type " + type);
        }

        @Override
        public void adjustForUnknownTypeInProperty(Type type, TypeBuilder typeBuilder, TypeDeclarationBuilder builder, Property property) {

            throw new IllegalArgumentException("cannot parse property of type " + type + " for property " + property.name() + " of type " + property.type());

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
        public TypeBuilder adjustType(Type type, String typeName, TypeBuilder builder) {
            TypeBuilder val = builder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustType(type, typeName, val);
            }
            return val;
        }

        @Override
        public String adjustTypeName(Class<?> aClass, String name) {
            String val = name;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustTypeName(aClass, val);
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

        @Override
        public TypeBuilder adjustForUnknownType(Type type) {
            TypeBuilder val = null;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustForUnknownType(type);
            }
            return val;
        }

        @Override
        public void adjustForUnknownTypeInProperty(Type type, TypeBuilder typeBuilder, TypeDeclarationBuilder typeDeclarationBuilder, Property property) {
            for (RamlAdjuster adjuster : adjusters) {
                 adjuster.adjustForUnknownTypeInProperty(type, typeBuilder, typeDeclarationBuilder, property);
            }
        }
    }

    /**
     * If the type being adjusted is an enumeration, you may change the enumerated value's name.
     * @param type
     * @param name
     * @return
     */
    String adjustEnumValue(Class<?> type, String name);

    /**
     * Changes the type.  You may RAML information to the type builder (or change it entirely).
     * @param type
     * @param typeName
     * @param builder a suggested builder. You can add to it and return this builder, or build a new one.
     * @return
     */
    TypeBuilder adjustType(Type type, String typeName, TypeBuilder builder);

    /**
     * Allows you to change the name when used as a reference.  In most cases, it should match what comes out of
     * {@link #adjustType(Type, String, TypeBuilder)} should you overload both.
     * @param aClass
     * @param name a suggested type name.  You may return it or change it.  It may not be null.
     * @return
     */
    String adjustTypeName(Class<?> aClass, String name);

    /**
     * You may change the property definition for a given scalar type.
     * @param typeDeclaration
     * @param property
     * @param typePropertyBuilder a suggested builder. You can add to it and return this builder, or build a new one.
     * @return
     */
    TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);

    /**
     * You may change the property definition for a given composed type.
     * @param typeDeclaration
     * @param property
     * @param typePropertyBuilder a suggested builder. You can add to it and return this builder, or build a new one.
     * @return
     */
    TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);

    /**
     * Should you have a property than contains an unsupported type for RAML, you could handle here.
     * @param type
     * @return
     */
    TypeBuilder adjustForUnknownType(Type type);

    /**
     * Should you have a property than contains an unsupported type for RAML, you could handle here.
     * @param type
     * @param typeBuilder
     * @return
     */
    void adjustForUnknownTypeInProperty(Type type, TypeBuilder typeBuilder, TypeDeclarationBuilder builder, Property property);

}
