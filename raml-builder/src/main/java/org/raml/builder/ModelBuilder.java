package org.raml.builder;



/**
 * Created. There, you have it.
 */
public interface ModelBuilder<T> {

    ModelBindingConfiguration binding = Util.bindingConfiguration();
    T buildModel();
}
