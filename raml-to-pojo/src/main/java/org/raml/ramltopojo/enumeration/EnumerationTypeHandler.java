package org.raml.ramltopojo.enumeration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.EnumerationPluginContextImpl;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

import javax.lang.model.element.Modifier;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandler implements TypeHandler {

    private final String name;
    private final StringTypeDeclaration typeDeclaration;

    public EnumerationTypeHandler(String name, StringTypeDeclaration stringTypeDeclaration) {
        this.name = name;
        this.typeDeclaration = stringTypeDeclaration;
    }

    @Override
    public ClassName javaTypeName(GenerationContext generationContext, EventType type) {

        return ClassName.get(generationContext.defaultPackage(), Names.typeName(name));
    }

    @Override
    public CreationResult create(GenerationContext generationContext, CreationResult preCreationResult) {

        FieldSpec.Builder field = FieldSpec.builder(ClassName.get(String.class), "name").addModifiers(Modifier.PRIVATE);
        EnumerationPluginContext enumerationPluginContext = new EnumerationPluginContextImpl(generationContext, preCreationResult);

        ClassName className = preCreationResult.getJavaName(EventType.INTERFACE);

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(className);

        enumBuilder.addField(field.build())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(
                        MethodSpec.constructorBuilder().addParameter(ClassName.get(String.class), "name")
                                .addStatement("this.$N = $N", "name", "name")
                                .build()
                );
        generationContext.pluginsForEnumerations(typeDeclaration).classCreated(enumerationPluginContext, typeDeclaration, enumBuilder, EventType.INTERFACE);

        for (String value : typeDeclaration.enumValues()) {
            TypeSpec.Builder enumValueBuilder = TypeSpec.anonymousClassBuilder("$S", value);
            enumBuilder.addEnumConstant(Names.constantName(value),
                    enumValueBuilder.build());

            generationContext.pluginsForEnumerations(typeDeclaration).enumValue(enumerationPluginContext, typeDeclaration, enumValueBuilder, EventType.INTERFACE);
        }

        return preCreationResult.withInterface(enumBuilder.build());
    }
}
