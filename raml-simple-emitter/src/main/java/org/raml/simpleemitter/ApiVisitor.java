package org.raml.simpleemitter;

import org.raml.parsertools.ExtensionFactory;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.nodes.KeyValueNode;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public interface ApiVisitor {

    void startBlock();
    void endBlock();

    void visit(Api node);
    void visit(Resource resource);
    void visit(TypeDeclaration typeDeclaration);
    void visit(Method method);
    void visit(Response response);
}
