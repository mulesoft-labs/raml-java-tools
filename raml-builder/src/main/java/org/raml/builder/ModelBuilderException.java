package org.raml.builder;


import amf.client.validate.ValidationReport;

/**
 * Created. There, you have it.
 */
public class ModelBuilderException extends RuntimeException {


    private final ValidationReport report;

    public ModelBuilderException(Exception e) {
        super(e);
        report = null;
    }

    public ModelBuilderException(ValidationReport report) {
        this.report = report;
    }

    @Override
    public String getMessage() {

        if ( report != null ) {
            return report.toString();
        } else {
            return super.getMessage();
        }
    }
}
