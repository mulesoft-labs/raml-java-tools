package org.raml.ramltopojo;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * Created. There, you have it.
 */
public class Main {

    public static void main(String[] args) throws Exception  {

        RamlModelResult ramlModelResult =
                new RamlModelBuilder().buildApi(
                        new InputStreamReader(Main.class.getResourceAsStream("/simple-api.raml")), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.err.println(validationResult.getMessage());
            }
            throw new Exception();
        }

        final Api api = ramlModelResult.getApiV10();
        RamlToPojo ramlToPojo = RamlToPojoBuilder.builder(api).inPackage("my.packaging").fetchTypesWith(new TypeFetcher() {
            @Override
            public TypeDeclaration fetchType(final String name) throws GenerationException {
                return FluentIterable.from(api.types()).firstMatch(new Predicate<TypeDeclaration>() {
                    @Override
                    public boolean apply(@Nullable TypeDeclaration input) {
                        return input.name().equals(name);
                    }
                }).or(new Supplier<TypeDeclaration>() {
                    @Override
                    public TypeDeclaration get() {
                        throw new GenerationException("type " + name + " not found");
                    }
                });
            }
        }).findTypesWith(new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes() {
                return Collections.singletonList(api.types().get(3));
            }
        }).build();

        ramlToPojo.buildPojos().createAllTypes("/tmp/foo");
    }
}
