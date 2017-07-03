package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.raml.v2.api.model.v10.system.types.StatusCodeString;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class YamlEmitter {

    private static final int depth = 4;

    private final static String SPACES = "                                                                     ";
    private final Writer writer;
    private  final int indent;

    public YamlEmitter() {
        this(new OutputStreamWriter(System.out), 0);
    }

    public YamlEmitter(Writer writer, int i) {

        indent = i;
        this.writer = writer;
    }


    public YamlEmitter indent() {

        return new YamlEmitter(writer, indent + 1);
    }

    public void write(String tag, String value) throws IOException {

        if ( value != null ) {
            writer.write(SPACES.substring(0, indent *depth) + tag + ": \"" + value + "\"\n");
        }
        writer.flush();
    }

    public void write(String tag, Integer value) throws IOException {

        if ( value != null ) {
            writer.write(SPACES.substring(0, indent*depth) + tag + ": " + value + "\n");
        }
        writer.flush();
    }

    public void write(String tag) throws IOException {

        writer.write(SPACES.substring(0, indent*depth) + tag + ":\n");
        writer.flush();
    }

    public void write(String tag, MarkdownString description) throws IOException {

        if ( description != null ) {

            writer.write(SPACES.substring(0, indent*depth) + tag + ":\"" + description.value() + "\"");
        }
    }

    public void write(StatusCodeString code) throws IOException {

        writer.write(SPACES.substring(0, indent*depth) + code.value() + ":\n");
        writer.flush();
    }

    public void write(String tag, Boolean requiredValue) throws IOException {

        if ( requiredValue != null ) {

            writer.write(SPACES.substring(0, indent*depth) + tag + ": " + requiredValue + "\n");
        }
    }

    public void write(String tag, Double requiredValue) throws IOException {

        if ( requiredValue != null ) {

            writer.write(SPACES.substring(0, indent*depth) + tag + ": " + requiredValue + "\n");
        }
    }

    public void write(String s, List<?> list) throws IOException {

        if ( list.size() > 0 ) {

            write(s);
        }
    }
}
