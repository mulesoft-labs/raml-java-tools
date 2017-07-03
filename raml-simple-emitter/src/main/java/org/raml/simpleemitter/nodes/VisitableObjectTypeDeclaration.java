package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableObjectTypeDeclaration;
import org.raml.simpleemitter.api.ModifiableTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class VisitableObjectTypeDeclaration extends Helper implements Visitable {
    private ObjectTypeDeclaration declaration;

    public VisitableObjectTypeDeclaration(ObjectTypeDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public void visit(ApiVisitor v) {

        ModifiableObjectTypeDeclaration motd = Augmenter.augment(ModifiableObjectTypeDeclaration.class, declaration);
        v.visit(motd);
    }

    public List<TypeDeclaration> properties() {

        return toModifiable(declaration.properties(), ModifiableTypeDeclaration.class);
    }
}
