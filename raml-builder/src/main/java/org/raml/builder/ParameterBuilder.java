package org.raml.builder;

import amf.client.model.domain.Parameter;
import amf.client.model.domain.Shape;

import java.util.Optional;


/**
 * Created. There, you have it.
 */
public class ParameterBuilder extends DomainElementBuilder<Parameter, ParameterBuilder> {

    private final String name;
    private final String binding;
    private TypeShapeBuilder type;
    private String displayName;
    private String description;
    private Boolean required;

    private ParameterBuilder(String name, String binding  ) {
        super();
        this.name = name;
        this.binding = binding;
    }

    public static ParameterBuilder parameter(String name) {

        return new ParameterBuilder(name, "query");
    }

    public static ParameterBuilder queryParameter(String name) {

        return new ParameterBuilder(name, "query");
    }

    public static ParameterBuilder headerParameter(String name) {

        return new ParameterBuilder(name, "header");
    }

//    public ParameterBuilder ofType(String name) {
//
//        this.type = TypeShapeBuilder.simpleType(name);
//        return this;
//    }

    public ParameterBuilder ofType(TypeShapeBuilder name) {

        this.type = name;
        return this;
    }

    @Override
    protected Parameter buildNodeLocally() {

        Parameter node = new Parameter().withName(name);
        node.withBinding("query");
        Optional.ofNullable(description).ifPresent(node::withDescription);
        node.withRequired(Optional.ofNullable(required).orElse(false));
        node.withSchema((Shape) type.buildNode());

        return node;
    }

    public ParameterBuilder displayName(String displayName) {

        this.displayName = displayName;
        return this;
    }

    public ParameterBuilder description(String description) {

        this.description = description;
        return this;
    }

    public ParameterBuilder required(boolean required) {

        this.required = required;
        return this;
    }

    public boolean required() {
        return required;
    }
}
