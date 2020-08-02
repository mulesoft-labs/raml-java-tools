package org.raml.pojotoraml.types;

import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.RamlAdjuster;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Arrays;

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
    public DeclaredShapeBuilder<?> getRamlSyntax(RamlAdjuster adjuster) {

        Class<? extends Enum> c = (Class<? extends Enum>) this.type();
        TypeShapeBuilder typeBuilder = TypeShapeBuilder.enumeratedType().enumValues(
                Arrays.stream(c.getEnumConstants()).map(new java.util.function.Function<Enum, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Enum o) {
                        return adjuster.adjustEnumValue(EnumRamlType.this.type(), o.name());
                    }
                }).toArray(String[]::new));


        adjuster.adjustType(this.type(), actualRamlName, typeBuilder);
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
    public Class<?> type() {
        return cls;
    }
}
