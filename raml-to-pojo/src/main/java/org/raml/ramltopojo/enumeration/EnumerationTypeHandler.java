package org.raml.ramltopojo.enumeration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.*;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

import javax.lang.model.element.Modifier;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandler implements TypeHandler {

    private final StringTypeDeclaration typeDeclaration;

    public EnumerationTypeHandler(StringTypeDeclaration stringTypeDeclaration) {

        this.typeDeclaration = stringTypeDeclaration;
    }

    @Override
    public ClassName javaTypeName(GenerationContext generationContext, EventType type) {

        return ClassName.get(generationContext.defaultPackage(), Names.typeName(typeDeclaration.name()));
    }

    @Override
    public CreationResult create(GenerationContext generationContext, CreationResult preCreationResult) {

        FieldSpec.Builder field = FieldSpec.builder(ClassName.get(String.class), "name").addModifiers(Modifier.PRIVATE);

        ClassName className = preCreationResult.getJavaName(EventType.INTERFACE);

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(className)
                .addField(field.build())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(
                        MethodSpec.constructorBuilder().addParameter(ClassName.get(String.class), "name")
                                .addStatement("this.$N = $N", "name", "name")
                                .build()
                );

        for (String value : typeDeclaration.enumValues()) {
            TypeSpec.Builder builder = TypeSpec.anonymousClassBuilder("$S", value);
            enumBuilder.addEnumConstant(Names.constantName(value),
                    builder.build());
        }

        return preCreationResult.withInterface(enumBuilder.build());
    }
}
