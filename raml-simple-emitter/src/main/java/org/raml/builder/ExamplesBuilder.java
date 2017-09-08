package org.raml.builder;

/**
 * Created. There, you have it.
 */
public class ExamplesBuilder extends KeyValueNodeBuilder<ExamplesBuilder> implements NodeBuilder {

    private ExamplesBuilder(String name) {
        super(name);
    }

    static public ExamplesBuilder examples(String name) {

        return new ExamplesBuilder(name);
    }

}
