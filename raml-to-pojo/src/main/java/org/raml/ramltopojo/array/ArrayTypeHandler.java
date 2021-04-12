package org.raml.ramltopojo.array;

import com.google.common.base.Optional;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ArrayPluginContext;
import org.raml.ramltopojo.extensions.ArrayPluginContextImpl;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;

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

        ArrayPluginContext arrayPluginContext = new ArrayPluginContextImpl(generationContext, null);
        return generationContext.pluginsForArrays(
                Utils.allParents(typeDeclaration, new ArrayList<TypeDeclaration>())
                        .toArray(new TypeDeclaration[0]))
                        .className(
                                arrayPluginContext,
                                typeDeclaration,
                                generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE), EventType.INTERFACE);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {

        if ( name.contains("[") || name.equals("array")) {
            String itemTypeName = typeDeclaration.items().name();
            if ("object".equals(itemTypeName)) {
                itemTypeName = typeDeclaration.items().type();
            }

            if ("object".equals(itemTypeName)) {

                throw new GenerationException("unable to create type array item of type object (or maybe an inline array type ?)");
            }

            return generationContext.pluginsForReferences(
                    Utils.allParents(typeDeclaration, new ArrayList<TypeDeclaration>()).toArray(new TypeDeclaration[0]))
                    .typeName(new ReferencePluginContext() {
                    }, typeDeclaration, ArrayTypeName.of(TypeDeclarationType.calculateTypeName(itemTypeName, typeDeclaration.items(), generationContext, type).box()));
        } else {

            // so we are an array declared in the types: section.
            return javaClassName(generationContext, type);
        }
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        ClassName className = preCreationResult.getJavaName(EventType.INTERFACE);
        ArrayPluginContext arrayPluginContext = new ArrayPluginContextImpl(generationContext, preCreationResult);

        TypeDeclaration items = typeDeclaration.items();

        TypeName itemsTypeName = ClassName.get(Object.class);
        if ( TypeDeclarationType.isNewInlineType(items) ){
            Optional<CreationResult> cr = TypeDeclarationType.createInlineType(className, preCreationResult.getJavaName(EventType.IMPLEMENTATION),  Names.typeName(items.type(), "type"), items, generationContext);
            if ( cr.isPresent() ) {
                preCreationResult.withInternalType(items.name(), cr.get());
                itemsTypeName = cr.get().getJavaName(EventType.INTERFACE);
            }
        }  else {

            itemsTypeName = findType(items.name(), items, generationContext).box();
        }

        TypeSpec.Builder arrayClassBuilder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC).superclass(ParameterizedTypeName.get(ClassName.get(ArrayList.class), itemsTypeName));
        arrayClassBuilder = generationContext.pluginsForArrays(typeDeclaration).classCreated(arrayPluginContext, typeDeclaration, arrayClassBuilder, EventType.INTERFACE);
        return Optional.of(preCreationResult.withInterface(arrayClassBuilder.build()));
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }

}
