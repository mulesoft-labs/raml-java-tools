package org.raml.simpleemitter;

import com.google.common.base.Joiner;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.raml.v2.api.model.v10.system.types.StatusCodeString;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.nodes.AbstractStringNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class YamlEmitter {

    private static final int depth = 4;

    private final Writer writer;
    private final int indent;

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


    // TODO: Fix this by getting the bullet in the emitter.  Maybe pullng out the class ?
    public YamlEmitter bulletListArray() {

        return new YamlEmitter(writer, indent + 1) {

            boolean firstwrite = true;
            public void writeTag(String tag) throws IOException {

                if ( firstwrite ) {
                    writer.write( tag + ": ");
                    firstwrite = false;
                } else {

                    writer.write(
                            "\n" + indentationString(indent)+ tag + ": ");
                }
                writer.flush();
            }

            @Override
            protected String indentationString(int indent) {
                if ( firstwrite ) {
                    return super.indentationString(indent);
                } else {
                    return "  " + super.indentationString(indent);
                }
            }
        };
    }

    public void writeTag(String tag) throws IOException {

        writer.write("\n" + indentationString(indent) + tag + ": ");
        writer.flush();
    }

    //String.format("%0" + n + "d", 0).replace("0",s)

    protected String indentationString(int indent) {

        return new String(new char[indent * depth]).replace("\0", " ");
    }

    private void writeNaked(String tag) throws IOException {

        writer.write(tag);
        writer.flush();
    }

    private void writeQuoted(String value) throws IOException {

        if (value.contains("\n")) {

            writer.write("|\n");
            writer.write(indentationString(indent + 1));
            String[] str = value.split("\n");
            writer.write(Joiner.on("\n" + indentationString(indent + 1)).join(str));
        } else {
            writer.write("\"" + value + "\"");
        }
        writer.flush();
    }

    public void writeValue(SimpleTypeNode<?> node) throws IOException {

        if (node instanceof StringNode) {
            writeQuoted(node.getLiteralValue());
        } else {
            writeNaked(node.getLiteralValue());
        }
    }

    public void writeObjectValue(String value) throws IOException {

        write(value);
    }

    /* temp */
    private void write(String s) throws IOException {

        writeQuoted(s);
    }

    public void writeSyntaxElement(String s) throws IOException {

        writer.write(s);
        writer.flush();
    }

    public void writeIndent() throws IOException {
        writer.write(indentationString(indent));
    }
}
