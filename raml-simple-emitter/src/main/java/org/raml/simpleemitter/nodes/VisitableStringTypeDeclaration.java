package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableObjectTypeDeclaration;
import org.raml.simpleemitter.api.ModifiableStringTypeDeclaration;
import org.raml.simpleemitter.api.ModifiableTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class VisitableStringTypeDeclaration extends Helper implements Visitable {
    private StringTypeDeclaration declaration;

    public VisitableStringTypeDeclaration(StringTypeDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public void visit(ApiVisitor v) {

        ModifiableStringTypeDeclaration motd = Augmenter.augment(ModifiableStringTypeDeclaration.class, declaration);
        v.visit(motd);
    }

}
