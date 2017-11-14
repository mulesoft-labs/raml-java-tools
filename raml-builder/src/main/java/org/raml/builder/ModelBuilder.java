package org.raml.builder;

import org.raml.yagi.framework.model.ModelBindingConfiguration;

/**
 * Created. There, you have it.
 */
public interface ModelBuilder<T> {

    ModelBindingConfiguration binding = Util.bindingConfiguration();
    T buildModel();
}
