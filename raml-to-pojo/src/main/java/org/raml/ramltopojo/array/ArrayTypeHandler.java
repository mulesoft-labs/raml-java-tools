package org.raml.ramltopojo.array;

import com.google.common.base.Optional;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ArrayPluginContext;
import org.raml.ramltopojo.extensions.ArrayPluginContextImpl;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ArrayTypeHandler implements TypeHandler {

    private final String name;
    private final ArrayTypeDeclaration typeDeclaration;

    public ArrayTypeHandler(String name, ArrayTypeDeclaration arrayTypeDeclaration) {
        this.name = name;
        this.typeDeclaration = arrayTypeDeclaration;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        ArrayPluginContext enumerationPluginContext = new ArrayPluginContextImpl(generationContext, null);
        return generationContext.pluginsForArrays(Utils.allParents(typeDeclaration, new ArrayList<TypeDeclaration>()).toArray(new TypeDeclaration[0])).className(enumerationPluginContext, typeDeclaration, generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE), EventType.INTERFACE);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        String itemTypeName = typeDeclaration.items().name();
        if ( "object".equals(itemTypeName)) {
            itemTypeName = typeDeclaration.items().type();
        }

        if ( "object".equals(itemTypeName)) {

            throw new GenerationException("unable to create type array item of type object (or maybe an inline array type ?)");
        }

        return ParameterizedTypeName.get(ClassName.get(List.class), TypeDeclarationType.calculateTypeName(itemTypeName, typeDeclaration.items(), generationContext, type).box());
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        ClassName className = preCreationResult.getJavaName(EventType.INTERFACE);
        ArrayPluginContext arrayPluginContext = new ArrayPluginContextImpl(generationContext, preCreationResult);

        TypeDeclaration items = typeDeclaration.items();
        TypeName itemsTypeName = findType(items.name(), items, generationContext).box();
        TypeSpec.Builder arrayClassBuilder = TypeSpec.classBuilder(className).superclass(ParameterizedTypeName.get(ClassName.get(ArrayList.class), itemsTypeName));

        arrayClassBuilder = generationContext.pluginsForArrays(typeDeclaration).classCreated(arrayPluginContext, typeDeclaration, arrayClassBuilder, EventType.INTERFACE);
        return Optional.of(preCreationResult.withInterface(arrayClassBuilder.build()));
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }

}
