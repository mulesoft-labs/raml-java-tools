package org.raml.pojotoraml;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.pojotoraml.field.FieldClassParser;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class PojoToRamlTest {
    @Test
    public void pojoToRamlTypeBuilder() throws Exception {

        PojoToRaml pojoToRaml = new PojoToRaml();
        Map<String, TypeBuilder> types =  pojoToRaml.pojoToRamlTypeBuilder(new FieldClassParser(Fun.class), RamlAdjuster.NULL_ADJUSTER);

        TypeDeclarationBuilder[] typeDeclarationBuilders = FluentIterable.from(types.entrySet()).transform(new Function<Map.Entry<String,TypeBuilder>, TypeDeclarationBuilder>() {
            @Nullable
            @Override
            public TypeDeclarationBuilder apply(@Nullable Map.Entry<String, TypeBuilder> entry) {

                return TypeDeclarationBuilder.typeDeclaration(entry.getKey()).ofType(entry.getValue());
            }
        }).toArray(TypeDeclarationBuilder.class);

        RamlDocumentBuilder ramlDocumentBuilder = RamlDocumentBuilder
                .document()
                .baseUri("http://google.com")
                .title("hello")
                .version("1")
                .withTypes(typeDeclarationBuilders);

        Api api = ramlDocumentBuilder.buildModel();

        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
        Node node = ((NodeModel) api).getNode();
        grammarPhase.apply(node);

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(2, buildTypes.size());
        TypeDeclaration declaration = buildTypes.get(0);
        declaration.name();
 /*       assertEquals("Fun", buildTypes.get(0).type());
        assertEquals("SubFun", buildTypes.get(1).name());
 */   }

}