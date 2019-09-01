package org.raml.ramltopojo.array;

import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ArrayPluginContext;
import org.raml.ramltopojo.extensions.ArrayPluginContextImpl;
import org.raml.ramltopojo.extensions.ReferencePluginContext;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class ArrayTypeHandler implements TypeHandler {

    private final String name;
    private final ArrayShape typeDeclaration;

    public ArrayTypeHandler(String name, ArrayShape arrayTypeDeclaration) {
        this.name = name;
        this.typeDeclaration = arrayTypeDeclaration;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        ArrayPluginContext arrayPluginContext = new ArrayPluginContextImpl(generationContext, null);
        return generationContext.pluginsForArrays(
                Utils.allParents(typeDeclaration, new ArrayList<Shape>())
                        .toArray(new Shape[0]))
                .className(
                        arrayPluginContext,
                        typeDeclaration,
                        generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE), EventType.INTERFACE);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {

        if (name.contains("[") || name.equals("array")) {
            String itemTypeName = typeDeclaration.items().name().value();
            if ("object".equals(itemTypeName)) {
                itemTypeName = typeDeclaration.items().name().value();
            }

            if ("object".equals(itemTypeName)) {

                throw new GenerationException("unable to create type array item of type object (or maybe an inline array type ?)");
            }

            List<Shape> shapes = Utils.allParents(typeDeclaration, new ArrayList<>());
            ReferencePluginContext referencePluginContext = new ReferencePluginContext() {
            };
            return generationContext.pluginsForReferences(
                    shapes.toArray(new Shape[0])).typeName(
                    referencePluginContext,
                    typeDeclaration,
                    ParameterizedTypeName.get(
                            ClassName.get(List.class),
                            ShapeType.calculateTypeName(itemTypeName, typeDeclaration.items(), generationContext, type).box())
            );
        } else {

            // so we are an array declared in the types: section.
            return javaClassName(generationContext, type);
        }
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        ClassName className = preCreationResult.getJavaName(EventType.INTERFACE);
        ArrayPluginContext arrayPluginContext = new ArrayPluginContextImpl(generationContext, preCreationResult);

        Shape items = typeDeclaration.items();

        TypeName itemsTypeName = ClassName.get(Object.class);
        if (ShapeType.isNewInlineType(items)) {
            Optional<CreationResult> cr = CreationResultFactory.createInlineType(className, preCreationResult.getJavaName(EventType.IMPLEMENTATION), Names.typeName(items.name().value(), "type"), null /*items*/, generationContext);
            if (cr.isPresent()) {
                preCreationResult.withInternalType(items.name().value(), cr.get());
                itemsTypeName = cr.get().getJavaName(EventType.INTERFACE);
            }
        } else {

            itemsTypeName = findType(items.name().value(), items, generationContext).box();
        }

        TypeSpec.Builder arrayClassBuilder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC).superclass(ParameterizedTypeName.get(ClassName.get(ArrayList.class), itemsTypeName));
        arrayClassBuilder = generationContext.pluginsForArrays(typeDeclaration).classCreated(arrayPluginContext, typeDeclaration, arrayClassBuilder, EventType.INTERFACE);
        return Optional.of(preCreationResult.withInterface(arrayClassBuilder.build()));
    }

    private TypeName findType(String typeName, Shape type, GenerationContext generationContext) {

        return ShapeType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }

}
