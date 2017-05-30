package ca.eloas.raml.query;

import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/28/17.
 * Just potential zeroes and ones
 */
public class Targets {

    static TopResourceTarget topResources() {

        return new TopResourceTarget();
    }

    static Target<Resource> allResources() {

        return new AllResourceTarget();
    }
}
