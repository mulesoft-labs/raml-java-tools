package org.raml.simpleemitter;

import org.raml.parsertools.Extension;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by jpbelang on 2017-06-25.
 */

@Extension(handler = EmissionHandler.class)
public interface RamlEmitter {

    void emit(Writer w) throws IOException;
}
