package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.WildcardTypeName;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class CovariantListPlugin extends ObjectTypeHandlerPlugin.Helper {

    private final List<String> arguments;

    public CovariantListPlugin(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {

        if ( eventType == EventType.INTERFACE) {

            if (isNotTargetDefaultType(objectPluginContext, declaration, arguments)) return incoming;

                if ( arguments.size() == 1 ) {

                    // override.....
                    incoming.returns(
                            ParameterizedTypeName.get(
                                    ClassName.get(List.class), WildcardTypeName.subtypeOf(ClassName.bestGuess(arguments.get(0)))));
                    return incoming;
                }

            MethodSpec build = incoming.build();
            ParameterizedTypeName type = (ParameterizedTypeName) build.returnType;

            incoming.returns(ParameterizedTypeName.get(type.rawType, WildcardTypeName.subtypeOf(type.typeArguments.get(0))));
        }

        return incoming;
    }

    @Override
    public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {

        if ( eventType == EventType.INTERFACE) {

            if ( arguments.size() == 1 ) {
                return null;
            }

            if (isNotTargetDefaultType(objectPluginContext, declaration, arguments)) {
                return incoming;
            } else {

                return null;
            }
        } else {

            return incoming;
        }
    }

    private  boolean isNotTargetDefaultType(ObjectPluginContext objectPluginContext, PropertyShape declaration, List<String> arguments) {
        if (!(declaration.range() instanceof ArrayShape)) {

            return true;
        }

        ArrayShape arrayTypeDeclaration = (ArrayShape) declaration.range();
        Shape itemTypes = arrayTypeDeclaration.items();
        if (!(itemTypes instanceof NodeShape)) {

            return true;
        }
        return arguments.size() == 0 && objectPluginContext.childClasses(itemTypes.name().value()).isEmpty();
    }
}
