package org.raml.ramltopojo;

import amf.client.validate.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class RamlLoaderException extends RuntimeException {
    private final List<ValidationResult> results = new ArrayList<>();

    public RamlLoaderException() {
    }

    public RamlLoaderException(String message) {
        super(message);
    }

    public RamlLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RamlLoaderException(Throwable cause) {
        super(cause);
    }

    public RamlLoaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RamlLoaderException(List<ValidationResult> results) {
        this.results.addAll(results);
    }
}
