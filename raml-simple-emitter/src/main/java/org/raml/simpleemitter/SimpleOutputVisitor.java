package org.raml.simpleemitter;

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


    public final YamlEmitter emitter = new YamlEmitter();

    @Override
    public void startBlock() {
        emitter.indent();
    }

    @Override
    public void endBlock() {

        emitter.outdent();
    }

    @Override
    public void visit(Api node)  {

        try {
            emitter.write("title", node.title().value());
            emitter.write("version", node.version().value());
        } catch (IOException e) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(Resource resource) {
        try {
            emitter.write(resource.resourcePath());
            emitter.write("displayName", resource.displayName().value());
        } catch (IOException e) {
            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(TypeDeclaration typeDeclaration) {

    }

    @Override
    public void visit(Response response) {

        try {
            emitter.write(response.code());
            emitter.write("description", response.description());
        } catch (IOException e ) {

            throw new VisitorException(e);
        }
    }

    @Override
    public void visit(Method method) {

        try {
            emitter.write(method.method());
            emitter.indent();
            emitter.write("protocols",  method.protocols().toString());
            emitter.write("description", method.description());
            emitter.write("is: " + method.is());
            emitter.write("securedby:" + method.securedBy());
            emitter.outdent();
        } catch (IOException e) {

            throw new VisitorException(e);
        }
    }
}
