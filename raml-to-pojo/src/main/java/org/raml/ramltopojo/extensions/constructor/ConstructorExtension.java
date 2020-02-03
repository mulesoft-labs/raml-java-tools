package org.raml.ramltopojo.extensions.constructor;

import java.util.Objects;

import javax.lang.model.element.Modifier;

import org.raml.ramltopojo.EcmaPattern;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.Names;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.google.common.base.Optional;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class ConstructorExtension extends ObjectTypeHandlerPlugin.Helper {
	
  @Override
  public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder typeSpec, EventType eventType) {
     
    if (eventType != EventType.IMPLEMENTATION) {
      return typeSpec;
    }
    
    boolean hasConstructorParams = false;
    TypeSpec clazz = typeSpec.build();
    MethodSpec.Builder fullConstructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
    Optional<String> discriminator = Optional.fromNullable(ramlType.discriminator());
    
    for (TypeDeclaration propertyDeclaration : ramlType.properties()) {

	    if (EcmaPattern.isSlashedPattern(propertyDeclaration.name()) || Objects.equals(propertyDeclaration.name(), discriminator.orNull())) {
	      continue;
	    }
	    	    
      fullConstructor
	      .addCode(CodeBlock.builder().addStatement("this." + Names.variableName(propertyDeclaration.name()) + " = " + Names.variableName(propertyDeclaration.name())).build())
	      .addParameter(findField(clazz, propertyDeclaration), Names.variableName(propertyDeclaration.name()));
      
      hasConstructorParams = true;
    }
    
    if (hasConstructorParams) {
      typeSpec.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
      typeSpec.addMethod(fullConstructor.build());
    }
  	
  	return typeSpec;
  }
  
  private TypeName findField(final TypeSpec clazz, final TypeDeclaration field) {
  	for (FieldSpec fieldSpec : clazz.fieldSpecs) {
  		if (Objects.equals(field.name(), fieldSpec.name)) {
  			return fieldSpec.type;
  		}
  	}
  	throw new IllegalArgumentException("There is no field of name: " + field.name());
  }
}
