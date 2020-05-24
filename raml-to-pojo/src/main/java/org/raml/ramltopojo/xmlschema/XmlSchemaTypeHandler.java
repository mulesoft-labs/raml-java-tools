package org.raml.ramltopojo.xmlschema;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.SchemaShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.XmlSchemaPluginContext;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class XmlSchemaTypeHandler implements TypeHandler {
    private final String name;
    private final ShapeType shapeType;
    private final SchemaShape schemaShape;

    public XmlSchemaTypeHandler(String name, ShapeType shapeType, AnyShape schemaShape) {

        this.name = name;
        this.shapeType = shapeType;
        this.schemaShape = (SchemaShape) schemaShape;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {
        ClassName currentSuggestion = generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE);
        return generationContext.pluginsForXmlSchemas(schemaShape)
                .typeName(new XmlSchemaPluginContext() {
                }, schemaShape, currentSuggestion);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return javaClassName(generationContext, type);
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {
        File schemaFile = null;
        try {
            File tmpFile = File.createTempFile("temp", "xsd");
            tmpFile.deleteOnExit();
            schemaFile = saveSchema(schemaShape.raw().value(), tmpFile);
            final JCodeModel codeModel = new JCodeModel();

            Map<String, JClass> generated =
                    generateClassesFromXmlSchemas(generationContext.defaultPackage(), schemaFile,
                            codeModel);

            return Optional.of(preCreationResult.withInterface((root, result) -> {
                try {
                    File rootDirectory = new File(root);
                    if (!rootDirectory.exists()) {
                        java.nio.file.Files.createDirectories(rootDirectory.toPath());
                    }

                    codeModel.build(rootDirectory);
                } catch (IOException e) {
                    throw new GenerationException(e);
                }
            }));
        } catch (Exception e) {

            throw new GenerationException(e);
        }
    }


    public static File saveSchema(String schema, File directory) throws IOException {

        File tmpFile = File.createTempFile("schema", ".xml", directory);
        FileWriter tmp = new FileWriter(tmpFile);
        tmp.write(schema);
        tmp.close();
        return tmpFile;
    }


    public static Map<String, JClass> generateClassesFromXmlSchemas(String pack, File file,
                                                                    JCodeModel codeModel) throws GenerationException {

        HashMap<String, JClass> classNameToKeyMap = new HashMap<>();

        List<JDefinedClass> classes = generateClassesFromXmlSchemas(pack, codeModel, file);
        for (JDefinedClass aClass : classes) {

            String className = aClass.name();
            classNameToKeyMap.put(className, aClass);
        }

        return classNameToKeyMap;
    }

    private static List<JDefinedClass> generateClassesFromXmlSchemas(String pack,
                                                                     JCodeModel codeModel, File schemaFile)
            throws GenerationException {
        try {
            ArrayList<JDefinedClass> classList = new ArrayList<JDefinedClass>();

            ArrayList<String> argList = new ArrayList<>();
            argList.add("-mark-generated");
            argList.add("-p");
            argList.add(pack);
            argList.add(schemaFile.getAbsolutePath());

            String[] args = argList.toArray(new String[argList.size()]);

            final Options opt = new Options();
            opt.setSchemaLanguage(Language.XMLSCHEMA);
            opt.parseArguments(args);

            ErrorReceiver receiver = new ErrorReceiverFilter() {

                @Override
                public void info(SAXParseException exception) {
                    if (opt.verbose)
                        super.info(exception);
                }

                @Override
                public void warning(SAXParseException exception) {
                    if (!opt.quiet)
                        super.warning(exception);
                }
            };

            Model model = ModelLoader.load(opt, codeModel, receiver);
            Outline outline = model.generateCode(opt, receiver);
            for (ClassOutline co : outline.getClasses()) {
                JDefinedClass cl = co.implClass;
                if (cl.outer() == null) {
                    classList.add(cl);
                }
            }
            return classList;
        } catch (Exception e) {
            throw new GenerationException(e);
        }
    }

}
