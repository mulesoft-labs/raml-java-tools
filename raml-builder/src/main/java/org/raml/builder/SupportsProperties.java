package org.raml.builder;

/**
 * Created. There, you have it.
 */
public interface SupportsProperties<E extends NodeBuilder<?>> {
    E withPropertyValue(PropertyValueBuilder values);
}
