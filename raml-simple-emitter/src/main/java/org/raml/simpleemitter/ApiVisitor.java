package org.raml.simpleemitter;

import org.raml.parsertools.ExtensionFactory;
import org.raml.simpleemitter.api.*;
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

    void visit(ModifiableApi node);
    void visit(ModifiableResource resource);
    void visit(ModifiableTypeDeclaration typeDeclaration);
    void visit(ModifiableMethod method);
    void visit(ModifiableResponse response);
    void visit(ModifiableObjectTypeDeclaration motd);

    void visit(ModifiableStringTypeDeclaration std);

    void visit(ModifiableIntegerTypeDeclaration motd);
}
