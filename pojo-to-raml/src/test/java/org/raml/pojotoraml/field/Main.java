package org.raml.pojotoraml.field;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.pojotoraml.PojoToRaml;
import org.raml.pojotoraml.RamlAdjuster;
import org.raml.simpleemitter.Emitter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        PojoToRaml pojoToRaml = new PojoToRaml();

        Map<String, TypeBuilder> types = pojoToRaml.pojoToRamlTypeBuilder(new FieldClassParser(Fun.class), RamlAdjuster.NULL_ADJUSTER);
        System.err.println(types);

        Emitter emitter = new Emitter();

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
        emitter.emit(ramlDocumentBuilder.buildModel());
    }

}
