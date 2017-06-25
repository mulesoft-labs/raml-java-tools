package org.raml.query;

import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/28/17.
 * Just potential zeroes and ones
 */
public class Selectors {

    static TopResourceSelector topResources() {

        return new TopResourceSelector();
    }

    static Selector<Resource> allResources() {

        return new AllResourceSelector();
    }
}
