package org.raml.ramltopojo.enumeration;

import amf.client.model.domain.ScalarNode;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.EnumerationPluginContextImpl;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandler implements TypeHandler {

    private final String name;
    private final ScalarShape typeDeclaration;

    public EnumerationTypeHandler(String name, ScalarShape stringTypeDeclaration) {
        this.name = name;
        this.typeDeclaration = stringTypeDeclaration;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        EnumerationPluginContext enumerationPluginContext = new EnumerationPluginContextImpl(generationContext, null);
        return generationContext.pluginsForEnumerations(Utils.allParents(typeDeclaration, new ArrayList<>()).toArray(new Shape[0])).className(enumerationPluginContext, typeDeclaration, generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE), EventType.INTERFACE);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return javaClassName(generationContext, type);
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        Class cls = typeDeclaration.dataType().value().endsWith("string")?String.class:Number.class;

        FieldSpec.Builder field = FieldSpec.builder(ClassName.get(cls), "name").addModifiers(Modifier.PRIVATE);
        EnumerationPluginContext enumerationPluginContext = new EnumerationPluginContextImpl(generationContext, preCreationResult);

        ClassName className = preCreationResult.getJavaName(EventType.INTERFACE);

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(className);

        enumBuilder.addField(field.build())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(
                        MethodSpec.constructorBuilder().addParameter(ClassName.get(cls), "name")
                                .addStatement("this.$N = $N", "name", "name")
                                .build()
                );
        enumBuilder = generationContext.pluginsForEnumerations(typeDeclaration).classCreated(enumerationPluginContext, typeDeclaration, enumBuilder, EventType.INTERFACE);
        if ( enumBuilder == null ) {
            return Optional.empty();
        }

        for (Object value : pullEnumValues(typeDeclaration)) {
            TypeSpec.Builder enumValueBuilder;
            if ( cls.equals(String.class)) {
                enumValueBuilder= TypeSpec.anonymousClassBuilder("$S", value);
                enumValueBuilder = generationContext.pluginsForEnumerations(typeDeclaration).enumValue(enumerationPluginContext, typeDeclaration, enumValueBuilder, (String)value, EventType.INTERFACE);
            } else {

                enumValueBuilder= TypeSpec.anonymousClassBuilder("$L", value);
                enumValueBuilder = generationContext.pluginsForEnumerations(typeDeclaration).enumValue(enumerationPluginContext, typeDeclaration, enumValueBuilder, Integer.parseInt((String) value), EventType.INTERFACE);
            }
            if ( enumValueBuilder == null ) {
                continue;
            }

            enumBuilder.addEnumConstant(Names.constantName(String.valueOf(value)),
                    enumValueBuilder.build());

        }

        return Optional.of(preCreationResult.withInterface(enumBuilder.build()));
    }

    // todo fix the double enumeration
    List<String> pullEnumValues(ScalarShape typeDeclaration) {

        return typeDeclaration.values().stream().map(x -> (ScalarNode) x).map(ScalarNode::value).collect(Collectors.toList());
    }
}
