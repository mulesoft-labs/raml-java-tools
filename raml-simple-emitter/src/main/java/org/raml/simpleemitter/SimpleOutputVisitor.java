package org.raml.simpleemitter;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.api.*;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
class SimpleOutputVisitor implements ApiVisitor {


    public final YamlEmitter emitter;

    public SimpleOutputVisitor() {
        this(new YamlEmitter());
    }

    public SimpleOutputVisitor(YamlEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void visit(ModifiableApi api)  {

        try {
            emitter.write("title", api.title().value());
            emitter.write("version", api.version().value());
            emitter.write("types");

            for (TypeDeclaration typeDeclaration : api.types()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(emitter.indent());
                ((ModifiableTypeDeclaration)typeDeclaration).visit(v);
            }


        } catch (IOException e) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(ModifiableResource resource) {
        try {
            emitter.write(resource.relativeUri().value());
            YamlEmitter newEmitter = emitter.indent();

            newEmitter.write("displayName", resource.displayName().value());
            for (Method method : resource.methods()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(newEmitter);
                ((ModifiableMethod)method).visit(v);
            }


            for (Resource res : resource.resources()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(emitter.indent());
                ((ModifiableResource)res).visit(v);
            }
        } catch (IOException e) {
            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(ModifiableTypeDeclaration typeDeclaration) {

    }

    @Override
    public void visit(ModifiableResponse response) {

        try {
            emitter.write(response.code());
            YamlEmitter newEmitter = emitter.indent();

            newEmitter.write("description", response.description());
            newEmitter.write("headers:", response.headers());
            for (TypeDeclaration typeDeclaration : response.headers()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(newEmitter.indent());
                ((ModifiableTypeDeclaration)typeDeclaration).visit(v);
            }

            for (TypeDeclaration typeDeclaration : response.body()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(newEmitter.indent());
                ((ModifiableTypeDeclaration)typeDeclaration).visit(v);
            }


        } catch (IOException e ) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(ModifiableMethod method) {

        try {
            emitter.write(method.method());
            YamlEmitter newEmitter = emitter.indent();
            newEmitter.write("protocols",  method.protocols().toString());
            newEmitter.write("description", method.description());
            newEmitter.write("is: " + method.is());
            newEmitter.write("securedby:" + method.securedBy());

            for (Response res : method.responses()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(emitter.indent());
                ((ModifiableResponse)res).visit(v);
            }
        } catch (IOException e) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(ModifiableObjectTypeDeclaration motd) {

        try {

            emitter.write(motd.name(), motd.type());
            YamlEmitter newEmitter = emitter.indent();

            newEmitter.write("defaultValue", motd.defaultValue());
            newEmitter.write("discriminator", motd.discriminator());
            newEmitter.write( "discriminatorValue", motd.discriminatorValue());
            newEmitter.write( "maxProperties", motd.maxProperties());
            newEmitter.write( "minProperties", motd.minProperties());
            newEmitter.write( "maxProperties", motd.maxProperties());
            newEmitter.write( "required", motd.required());

            newEmitter.write("properties");
            for (TypeDeclaration typeDeclaration : motd.properties()) {

                SimpleOutputVisitor v = new SimpleOutputVisitor(newEmitter.indent());
                ((ModifiableTypeDeclaration)typeDeclaration).visit(v);
            }

        } catch (IOException e) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(ModifiableIntegerTypeDeclaration itd) {

        try {
            emitter.write(itd.name(), itd.type());
            YamlEmitter newEmitter = emitter.indent();

            newEmitter.write("defaultValue", itd.defaultValue());
            newEmitter.write( "maximum", itd.maximum());
            newEmitter.write( "minimum", itd.minimum());
            newEmitter.write( "required", itd.required());
        } catch (IOException e) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(ModifiableStringTypeDeclaration itd) {

        try {
            emitter.write(itd.name(), itd.type());
            YamlEmitter newEmitter = emitter.indent();

            newEmitter.write("defaultValue", itd.defaultValue());
            newEmitter.write( "required", itd.required());

        } catch (IOException e) {
            throw new VisitorException(e);
        }
    }
}
