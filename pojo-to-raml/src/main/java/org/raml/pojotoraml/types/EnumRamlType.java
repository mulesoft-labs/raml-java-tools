package org.raml.pojotoraml.types;

import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.TypeShapeBuilder;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Created. There, you have it.
 */
public class EnumRamlType implements RamlType{

    private final Class<?> cls;
    private final String actualRamlName;

    public EnumRamlType(Class<?> cls, String actualRamlName) {

        this.cls = cls;
        this.actualRamlName = actualRamlName;
    }

    public static EnumRamlType forClass(Class<?> cls, String actualRamlName ) {

        return new EnumRamlType(cls, actualRamlName);
    }

    @Override
    public DeclaredShapeBuilder<?> getRamlSyntax(Function<Type, TypeShapeBuilder<?, ?>> adjuster) {

        Class<? extends Enum> c = (Class<? extends Enum>) this.type();
        TypeShapeBuilder typeBuilder = TypeShapeBuilder.enumeratedType().enumValues(
                Arrays.stream(c.getEnumConstants()).map(Enum::name).toArray(String[]::new));


        //adjuster.adjustType(this.type(), actualRamlName, typeBuilder);
        DeclaredShapeBuilder declaredShapeBuilder = DeclaredShapeBuilder.typeDeclaration(actualRamlName).ofType(typeBuilder);

        return declaredShapeBuilder;
    }

    @Override
    public boolean isScalar() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return true;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public Class<?> type() {
        return cls;
    }
}
