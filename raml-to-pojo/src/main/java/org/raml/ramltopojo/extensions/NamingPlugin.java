package org.raml.ramltopojo.extensions;

import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.EventType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface NamingPlugin<T extends GenericPluginContext, R extends TypeDeclaration> {

    ClassName className(T pluginContext, R ramlType, ClassName currentSuggestion, EventType eventType);

}
