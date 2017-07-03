package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.raml.v2.api.model.v10.system.types.StatusCodeString;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created. There, you have it.
 */
public class YamlEmitter {

    private final static String SPACES = "                                                                     ";

    private final Writer writer;

    public YamlEmitter() {
        this.writer = new OutputStreamWriter(System.out);
    }

    private int indent;

    public void indent() {

        this.indent ++;
    }

    public void outdent() {
        this.indent ++;
    }

    public void write(String tag, String value) throws IOException {

        if ( value != null ) {
            writer.write(SPACES.substring(0, indent *2) + tag + ": \"" + value + "\"\n");
        }
        writer.flush();
    }

    public void write(String tag, int value) throws IOException {

        writer.write(SPACES.substring(0, indent*2) + tag + ": " + value + "\n");
        writer.flush();
    }

    public void write(String tag) throws IOException {

        writer.write(SPACES.substring(0, indent*2) + tag + ":\n");
        writer.flush();
    }

    public void write(String tag, MarkdownString description) throws IOException {

        if ( description != null ) {

            writer.write(SPACES.substring(0, indent*2) + tag + ":\"" + description.value() + "\"");
        }
    }

    public void write(StatusCodeString code) throws IOException {

        writer.write(code.value() + ":");
        writer.flush();
    }
}
