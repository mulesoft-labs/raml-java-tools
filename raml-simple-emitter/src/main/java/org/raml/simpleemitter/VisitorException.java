package org.raml.simpleemitter;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public class VisitorException extends RuntimeException {
    public VisitorException(IOException e) {
        super(e);
    }
}
