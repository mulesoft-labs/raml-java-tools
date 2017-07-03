package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableIntegerTypeDeclaration;
import org.raml.simpleemitter.api.ModifiableStringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

/**
 * Created. There, you have it.
 */
public class VisitableIntegerTypeDeclaration extends Helper implements Visitable {
    private IntegerTypeDeclaration declaration;

    public VisitableIntegerTypeDeclaration(IntegerTypeDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public void visit(ApiVisitor v) {

        ModifiableIntegerTypeDeclaration motd = Augmenter.augment(ModifiableIntegerTypeDeclaration.class, declaration);
        v.visit(motd);
    }

}
