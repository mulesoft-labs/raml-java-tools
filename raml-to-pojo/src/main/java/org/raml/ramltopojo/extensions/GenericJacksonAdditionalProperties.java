package org.raml.ramltopojo.extensions;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.raml.ramltopojo.EventType;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class GenericJacksonAdditionalProperties extends ObjectTypeHandlerPlugin.Helper {

    private static final ParameterizedTypeName ADDITIONAL_PROPERTIES_TYPE = ParameterizedTypeName.get(
            Map.class, String.class,
            Object.class);

    private final Class<? extends Annotation> jsonAnyGetterAnnotation;
    private final Class<? extends Annotation> jsonAnySetterAnnotation;
    private final Class<? extends Annotation> jsonIgnore;

    public GenericJacksonAdditionalProperties(Class<? extends Annotation> jsonAnyGetterAnnotation, Class<? extends Annotation> jsonAnySetterAnnotation, Class<? extends Annotation> jsonIgnore) {
        this.jsonAnyGetterAnnotation = jsonAnyGetterAnnotation;
        this.jsonAnySetterAnnotation = jsonAnySetterAnnotation;
        this.jsonIgnore = jsonIgnore;
    }


    @Override
    public MethodSpec.Builder additionalPropertiesGetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType anInterface) {
        return incoming.addAnnotation(jsonAnyGetterAnnotation);
    }

    @Override
    public MethodSpec.Builder additionalPropertiesSetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType) {

        return incoming.addAnnotation(jsonAnySetterAnnotation);
    }

    @Override
    public FieldSpec.Builder additionalPropertiesFieldBuilt(ObjectPluginContext objectPluginContext, FieldSpec.Builder incoming, EventType eventType) {

        return incoming.addAnnotation(jsonIgnore);
    }
}
