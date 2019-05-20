package org.raml.ramltopojo.enumeration;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.TypeHandler;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.EnumerationPluginContextImpl;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandler implements TypeHandler {

    private final String name;
    private final Shape typeDeclaration;

    public EnumerationTypeHandler(String name, Shape stringTypeDeclaration) {
        this.name = name;
        this.typeDeclaration = stringTypeDeclaration;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        EnumerationPluginContext enumerationPluginContext = new EnumerationPluginContextImpl(generationContext, null);
// migration        return generationContext.pluginsForEnumerations(Utils.allParents(typeDeclaration, new ArrayList<TypeDeclaration>()).toArray(new TypeDeclaration[0])).className(enumerationPluginContext, typeDeclaration, generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE), EventType.INTERFACE);
        return null;
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return javaClassName(generationContext, type);
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

/*
        Class cls = (typeDeclaration instanceof StringTypeDeclaration)?String.class:Number.class;

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
            if ( value instanceof String) {
                enumValueBuilder= TypeSpec.anonymousClassBuilder("$S", value);
                enumValueBuilder = generationContext.pluginsForEnumerations(typeDeclaration).enumValue(enumerationPluginContext, typeDeclaration, enumValueBuilder, (String)value, EventType.INTERFACE);
            } else {

                enumValueBuilder= TypeSpec.anonymousClassBuilder("$L", value);
                enumValueBuilder = generationContext.pluginsForEnumerations(typeDeclaration).enumValue(enumerationPluginContext, typeDeclaration, enumValueBuilder, (Number)value, EventType.INTERFACE);
            }
            if ( enumValueBuilder == null ) {
                continue;
            }

            enumBuilder.addEnumConstant(Names.constantName(String.valueOf(value)),
                    enumValueBuilder.build());

        }

        return Optional.of(preCreationResult.withInterface(enumBuilder.build()));
*/
        return Optional.empty();
    }

    List pullEnumValues(TypeDeclaration typeDeclaration) {

        if ( typeDeclaration instanceof  IntegerTypeDeclaration ) {
            return ((IntegerTypeDeclaration)typeDeclaration).enumValues();
        } else  if (typeDeclaration instanceof NumberTypeDeclaration) {
            return ((NumberTypeDeclaration)typeDeclaration).enumValues();
        } else {
            return ((StringTypeDeclaration)typeDeclaration).enumValues();
        }
    }
}
