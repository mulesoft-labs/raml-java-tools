package org.raml.pojotoraml.types;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import org.raml.pojotoraml.AdjusterFactory;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.RamlAdjuster;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created. There, you have it.
 */
public class RamlTypeFactory {

    public static Optional<RamlType> forType(Type type, final ClassParser parser, final AdjusterFactory adjusterFactory) {

        if ( type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if ( Collection.class.isAssignableFrom((Class)parameterizedType.getRawType())) {

                if ( parameterizedType.getActualTypeArguments().length != 1 && ! (parameterizedType.getActualTypeArguments()[0] instanceof Class) ) {

                    throw new IllegalArgumentException("type " + type + " is not a simple enough type for system to handle:  too many parameters in type or parameter not a class");
                }

                final Class<?> cls = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                Optional<RamlType> ramlType =  ScalarType.fromType(cls);

                return Optional.<RamlType>of(CollectionRamlType.of(ramlType.or(new Supplier<RamlType>() {
                    @Override
                    public RamlType get() {

                        RamlAdjuster adjuster = adjusterFactory.createAdjuster(cls);
                        return ComposedRamlType.forClass(cls, adjuster.adjustTypeName(cls, cls.getSimpleName()));
                    }
                })));
            }
        }

        if ( type instanceof Class && ((Class)type).isArray() ) {

            final Class<?> cls = (Class<?>) type;
            Optional<RamlType> ramlType =  ScalarType.fromType(cls.getComponentType());

            return Optional.<RamlType>of(CollectionRamlType.of(ramlType.or(new Supplier<RamlType>() {
                @Override
                public RamlType get() {

                    RamlAdjuster adjuster = adjusterFactory.createAdjuster(cls.getComponentType());
                    return ComposedRamlType.forClass(cls.getComponentType(), adjuster.adjustTypeName(cls.getComponentType(), cls.getComponentType().getSimpleName()));
                }
            })));

        }
        if ( type instanceof Class && Enum.class.isAssignableFrom((Class<?>) type) ) {

            final Class<?> cls = (Class<?>) type;
            RamlAdjuster adjuster = adjusterFactory.createAdjuster(cls);
            return Optional.<RamlType>of(EnumRamlType.forClass(cls, adjuster.adjustTypeName(cls, cls.getSimpleName())));
        }

        if ( type instanceof Class ) {
            final Class<?> cls = (Class<?>) type;

            Optional<RamlType> ramlType =  ScalarType.fromType(cls);

            return Optional.of(ramlType.or(new Supplier<RamlType>() {
                @Override
                public RamlType get() {
                    RamlAdjuster adjuster = adjusterFactory.createAdjuster(cls);
                    return ComposedRamlType.forClass(cls, adjuster.adjustTypeName(cls, cls.getSimpleName()));
                }
            }));
        }

        return Optional.absent();
    }
}
