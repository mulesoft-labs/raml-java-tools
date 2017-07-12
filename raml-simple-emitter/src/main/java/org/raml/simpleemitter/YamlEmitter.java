package org.raml.simpleemitter;

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

    public void writeTag(String tag) throws IOException {

        writer.write(SPACES.substring(0, indent*depth) + tag + ":\n");
        writer.flush();
    }

    public void writeOneLine(String tag) throws IOException {

        writer.write(SPACES.substring(0, indent*depth) + tag + ": ");
        writer.flush();
    }

    private void writeNaked(String tag) throws IOException {

        writer.write(tag + "\n");
        writer.flush();
    }

    private void writeQuoted(String tag) throws IOException {

        writer.write("\"" + tag + "\"\n");
        writer.flush();
    }

    public void writeValue(SimpleTypeNode<?> node) throws IOException {

        if ( node instanceof StringNode) {
            writeQuoted(node.getLiteralValue());
        } else {
            writeNaked(node.getLiteralValue());
        }
    }

    public void writeValue(TypeExpressionNode node) throws IOException {

        writeQuoted(node.getTypeExpressionText());
    }

    /* temp */
    public void write(String s) throws IOException {

        writeQuoted(s);
    }
}
