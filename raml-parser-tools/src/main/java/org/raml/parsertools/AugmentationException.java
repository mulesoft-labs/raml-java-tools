package org.raml.parsertools;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public class AugmentationException extends RuntimeException {
    public AugmentationException(String message) {
        super(message);
    }

    public AugmentationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AugmentationException(Throwable cause) {
        super(cause);
    }
}
