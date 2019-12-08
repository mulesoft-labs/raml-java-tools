package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.extensions.*;
import webapi.WebApiDocument;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class CreationResultFactory {
    /**
     * Create the actual type.
     *
     * @param typeDeclaration
     * @param context
     * @return
     */
    public static Optional<CreationResult> createType(AnyShape typeDeclaration, GenerationContext context) {

        ShapeType shapeType = ShapeType.ramlToType(Utils.declarationType(typeDeclaration));

        TypeHandler handler = shapeType.createHandler(typeDeclaration.name().value(), shapeType, typeDeclaration);
        ClassName intf = handler.javaClassName(context, EventType.INTERFACE);
        ClassName impl = handler.javaClassName(context, EventType.IMPLEMENTATION);
        CreationResult creationResult = new CreationResult(context.defaultPackage(), intf, impl);
        context.newExpectedType(typeDeclaration.name().value(), creationResult);
        context.setupTypeHierarchy(typeDeclaration);
        return handler.create(context, creationResult);
    }

    /**
     * Create the actual type.
     *
     * @param typeDeclaration
     * @param context
     * @return
     */
    public static Optional<CreationResult> createNamedType(String name, AnyShape typeDeclaration, GenerationContext context) {

        ShapeType shapeType = ShapeType.ramlToType(Utils.declarationType(typeDeclaration));

        TypeHandler handler = shapeType.createHandler(name, shapeType, typeDeclaration);
        ClassName intf = handler.javaClassName(context, EventType.INTERFACE);
        ClassName impl = handler.javaClassName(context, EventType.IMPLEMENTATION);
        CreationResult creationResult = new CreationResult(context.defaultPackage(), intf, impl);
        context.newExpectedType(name, creationResult);
        context.setupTypeHierarchy(typeDeclaration);
        return handler.create(context, creationResult);
    }

    /**
     * Create the actual type.
     *
     * @param propertyShape
     * @param context
     * @return
     */
    public static Optional<CreationResult> createInlineType(ClassName containingClassName, ClassName containingImplementation, String name, PropertyShape propertyShape, final GenerationContext context) {

        ShapeType shapeType = ShapeType.ramlToType(Utils.declarationType(Utils.rangeOf(propertyShape)));

        TypeHandler handler = shapeType.createHandler(name, shapeType, Utils.rangeOf(propertyShape));
        ClassName intf = handler.javaClassName(new InlineGenerationContext(containingClassName, containingClassName, context),  EventType.INTERFACE);
        ClassName impl = handler.javaClassName(new InlineGenerationContext(containingClassName, containingImplementation, context), EventType.IMPLEMENTATION);
        CreationResult preCreationResult = new CreationResult("", intf, impl);
        return handler.create(context, preCreationResult);
    }

    public static Optional<CreationResult> createInlineType(ClassName containingClassName, ClassName containingImplementation, String name, AnyShape shape, final GenerationContext context) {

        ShapeType shapeType = ShapeType.ramlToType(Utils.declarationType(shape));

        TypeHandler handler = shapeType.createHandler(name, shapeType, shape);
        ClassName intf = handler.javaClassName(new InlineGenerationContext(containingClassName, containingClassName, context),  EventType.INTERFACE);
        ClassName impl = handler.javaClassName(new InlineGenerationContext(containingClassName, containingImplementation, context), EventType.IMPLEMENTATION);
        CreationResult preCreationResult = new CreationResult("", intf, impl);
        return handler.create(context, preCreationResult);
    }

    private static class InlineGenerationContext implements GenerationContext {
        private final ClassName containingDeclaration;
        private final ClassName containingImplementation;
        private final GenerationContext context;

        public InlineGenerationContext(ClassName containingDeclaration, ClassName containingImplementation, GenerationContext context) {
            this.containingDeclaration = containingDeclaration;
            this.containingImplementation = containingImplementation;
            this.context = context;
        }

        @Override
        public void createSupportTypes(String rootDirectory) throws IOException {
            context.createSupportTypes(rootDirectory);
        }

        @Override
        public TypeName createSupportClass(TypeSpec.Builder newSupportType) {
            return context.createSupportClass(newSupportType);
        }

        @Override
        public CreationResult findCreatedType(String typeName, Shape ramlType) {
            return context.findCreatedType(typeName, ramlType);
        }

        @Override
        public String defaultPackage() {
            return "";
        }

        @Override
        public void newExpectedType(String name, CreationResult creationResult) {

        }

        @Override
        public void createTypes(String rootDirectory) throws IOException {

        }

        @Override
        public ObjectTypeHandlerPlugin pluginsForObjects(Shape... typeDeclarations) {
            return context.pluginsForObjects(typeDeclarations);
        }

        @Override
        public EnumerationTypeHandlerPlugin pluginsForEnumerations(Shape... typeDeclarations) {
            return context.pluginsForEnumerations(typeDeclarations);
        }

        @Override
        public UnionTypeHandlerPlugin pluginsForUnions(Shape... typeDeclarations) {
            return context.pluginsForUnions(typeDeclarations);
        }

        @Override
        public ArrayTypeHandlerPlugin pluginsForArrays(Shape... typeDeclarations) {
            return context.pluginsForArrays(typeDeclarations);
        }

        @Override
        public WebApiDocument api() {
            return context.api();
        }

        @Override
        public Set<String> childClasses(String ramlTypeName) {
            return context.childClasses(ramlTypeName);
        }

        @Override
        public ClassName buildDefaultClassName(String name, EventType eventType) {
            if ( eventType == EventType.INTERFACE ) {
                return containingDeclaration.nestedClass(name);
            } else {
                return containingImplementation.nestedClass(name);
            }
        }

        @Override
        public ReferenceTypeHandlerPlugin pluginsForReferences(Shape... typeDeclarations) {
            return context.pluginsForReferences(typeDeclarations);
        }

        @Override
        public void setupTypeHierarchy(Shape typeDeclaration) {
            context.setupTypeHierarchy(typeDeclaration);
        }
    }

}
