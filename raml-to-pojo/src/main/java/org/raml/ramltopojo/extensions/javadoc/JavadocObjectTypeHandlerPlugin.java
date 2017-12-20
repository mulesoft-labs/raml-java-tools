package org.raml.ramltopojo.extensions.javadoc;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */

/*

  private interface JavadocAdder {

    void addJavadoc(String format, Object... args);
  }
  @Override
  public void onGetterMethodDeclaration(CurrentBuild currentBuild, final MethodSpec.Builder typeSpec,
                                        TypeDeclaration typeDeclaration) {
    if (typeDeclaration.description() != null) {
      typeSpec.addJavadoc("$L\n", typeDeclaration.description().value());
    }

    javadocExamples(new JavadocAdder() {

      @Override
      public void addJavadoc(String format, Object... args) {
        typeSpec.addJavadoc(format, args);
      }
    }, typeDeclaration);
  }

  public void javadocExamples(JavadocAdder adder, TypeDeclaration typeDeclaration) {
    ExampleSpec example = typeDeclaration.example();
    if (example != null) {

      javadoc(adder, example);
    }

    for (ExampleSpec exampleSpec : typeDeclaration.examples()) {
      javadoc(adder, exampleSpec);
    }
  }

  public void javadoc(JavadocAdder adder, ExampleSpec exampleSpec) {
    adder.addJavadoc("Example:\n");

    if (exampleSpec.name() != null) {
      adder.addJavadoc(" $L\n", exampleSpec.name());
    }

    adder.addJavadoc(" $L\n", "<pre>\n{@code\n" + exampleSpec.value() + "\n}</pre>");
  }

 */
public class JavadocObjectTypeHandlerPlugin extends ObjectTypeHandlerPlugin.Helper {

    private interface JavadocAdder {

        void addJavadoc(String format, Object... args);
    }

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, final TypeSpec.Builder incoming, EventType eventType) {

        if (ramlType.description() != null) {
            incoming.addJavadoc("$L\n", ramlType.description().value());
        }

        javadocExamples(new JavadocAdder() {

            @Override
            public void addJavadoc(String format, Object... args) {

                incoming.addJavadoc(format, args);
            }
        }, ramlType);

        return incoming;
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration declaration, final MethodSpec.Builder incoming, EventType eventType) {
        if (declaration.description() != null) {
            incoming.addJavadoc("$L\n", declaration.description().value());
        }

        javadocExamples(new JavadocAdder() {

            @Override
            public void addJavadoc(String format, Object... args) {
                incoming.addJavadoc(format, args);
            }
        }, declaration);

        return incoming;
    }


    private void javadocExamples(JavadocAdder adder, TypeDeclaration typeDeclaration) {
        ExampleSpec example = typeDeclaration.example();
        if (example != null) {

            javadoc(adder, example);
        }

        for (ExampleSpec exampleSpec : typeDeclaration.examples()) {
            javadoc(adder, exampleSpec);
        }
    }

    private void javadoc(JavadocAdder adder, ExampleSpec exampleSpec) {
        adder.addJavadoc("Example:\n");

        if (exampleSpec.name() != null) {
            adder.addJavadoc(" $L\n", exampleSpec.name());
        }

        adder.addJavadoc(" $L\n", "<pre>\n{@code\n" + exampleSpec.value() + "\n}</pre>");
    }

}
