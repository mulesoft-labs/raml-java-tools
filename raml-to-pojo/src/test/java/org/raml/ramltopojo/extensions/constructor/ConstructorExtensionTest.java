package org.raml.ramltopojo.extensions.constructor;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static com.squareup.javapoet.Assertions.assertThat;
import static junit.framework.TestCase.assertNotNull;
import static org.raml.ramltopojo.RamlLoader.findTypes;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldName;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldType;
import static org.raml.testutils.matchers.FieldSpecMatchers.initializer;
import static org.raml.testutils.matchers.MethodSpecMatchers.codeContent;
import static org.raml.testutils.matchers.MethodSpecMatchers.methodName;
import static org.raml.testutils.matchers.MethodSpecMatchers.parameters;
import static org.raml.testutils.matchers.MethodSpecMatchers.returnType;
import static org.raml.testutils.matchers.ParameterSpecMatchers.type;
import static org.raml.testutils.matchers.TypeNameMatcher.typeName;
import static org.raml.testutils.matchers.TypeSpecMatchers.fields;
import static org.raml.testutils.matchers.TypeSpecMatchers.methods;
import static org.raml.testutils.matchers.TypeSpecMatchers.name;
import static org.raml.testutils.matchers.TypeSpecMatchers.superInterfaces;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContextImpl;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.RamlToPojo;
import org.raml.ramltopojo.RamlToPojoBuilder;
import org.raml.ramltopojo.TypeFetchers;
import org.raml.ramltopojo.TypeFinders;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.ramltopojo.object.ObjectTypeHandler;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.testutils.assertj.ListAssert;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class ConstructorExtensionTest {
	
	@Test
  public void empty() throws Exception {

    Api api = RamlLoader.load(this.getClass().getResourceAsStream("empty-type.raml"), ".");
    RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.constructor"));
    CreationResult r = ramlToPojo.buildPojos().creationResults().stream().filter(x -> x.getJavaName(EventType.INTERFACE).simpleName().equals("Foo")).findFirst().get();

    assertThat(r.getInterface()).hasName("Foo");
    
    System.err.println(r.getInterface().toString());
    System.err.println(r.getImplementation().toString());

    assertThat(r.getImplementation().get(), is(allOf(
	    name(equalTo("FooImpl")),
	    methods(containsInAnyOrder(
	      allOf(methodName(equalTo("getAdditionalProperties")), returnType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class)))),
	      allOf(methodName(equalTo("setAdditionalProperties")), parameters(contains(type(equalTo(TypeName.get(String.class))), type(equalTo(TypeName.get(Object.class))))))
	    ))                 
    )));
  }
	
	
	@Test
  public void simplest() throws Exception {

    Api api = RamlLoader.load(this.getClass().getResourceAsStream("simplest-type.raml"), ".");
    RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.constructor"));
    CreationResult r = ramlToPojo.buildPojos().creationResults().stream().filter(x -> x.getJavaName(EventType.INTERFACE).simpleName().equals("Foo")).findFirst().get();

    assertThat(r.getInterface()).hasName("Foo");
    
    System.err.println(r.getInterface().toString());
    System.err.println(r.getImplementation().toString());

    assertThat(r.getImplementation().get(), is(allOf(
	    name(equalTo("FooImpl")),
	    methods(containsInAnyOrder(
        allOf(methodName(equalTo("<init>"))),
        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get(String.class))), type(equalTo(ClassName.INT))))),	    	
	      allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
	      allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
	      allOf(methodName(equalTo("getAge")), returnType(equalTo(ClassName.INT))),
	      allOf(methodName(equalTo("setAge")), parameters(contains(type(equalTo(ClassName.INT)))))
	    ))                 
    )));
  }

  @Test
  public void composed() throws Exception {
    
  	Api api = RamlLoader.load(this.getClass().getResourceAsStream("composed-type.raml"), ".");
    RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.constructor"));
    CreationResult r = ramlToPojo.buildPojos().creationResults().stream().filter(x -> x.getJavaName(EventType.INTERFACE).simpleName().equals("Foo")).findFirst().get();

    assertThat(r.getInterface()).hasName("Foo");
    
    System.err.println(r.getInterface().toString());
    System.err.println(r.getImplementation().toString());

    assertThat(r.getImplementation().get(), is(allOf(
      name(equalTo("FooImpl")),
      methods(containsInAnyOrder(
        allOf(methodName(equalTo("<init>"))),
        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get("", "Composed")))))),
        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get("", "Composed")))),
        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get("", "Composed"))))))
      ))
    )));
  }

  @Test
  public void discriminator() throws Exception {

  	Api api = RamlLoader.load(this.getClass().getResourceAsStream("discriminator-type.raml"), ".");
    RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.constructor"));
    CreationResult r = ramlToPojo.buildPojos().creationResults().stream().filter(x -> x.getJavaName(EventType.INTERFACE).simpleName().equals("Foo")).findFirst().get();

    assertThat(r.getInterface()).hasName("Foo");
    
    System.err.println(r.getInterface().toString());
    System.err.println(r.getImplementation().toString());
    
	  assertThat(r.getImplementation().get(), is(allOf(
	    name(equalTo("FooImpl")),
	    methods(containsInAnyOrder(
        allOf(methodName(equalTo("<init>"))),
        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get(String.class))), type(equalTo(ClassName.get(String.class)))))),
	      allOf(methodName(equalTo("getAdditionalProperties")), returnType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class)))),
	      allOf(methodName(equalTo("setAdditionalProperties")), parameters(contains(type(equalTo(TypeName.get(String.class))), type(equalTo(TypeName.get(Object.class)))))),
	      allOf(methodName(equalTo("getKind")), returnType(equalTo(ClassName.get(String.class)))),
	      allOf(methodName(equalTo("getRight")), returnType(equalTo(ClassName.get(String.class)))),
	      allOf(methodName(equalTo("setRight")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
	      allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
	      allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))))
	    ))
    )));
  }
}
