/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.ramltopojo;

import amf.client.model.domain.*;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;


/**
 * <p>
 * Names class.
 * </p>
 *
 * @author kor
 * @version $Id: $Id
 *
 */
public class Names {

  private static Pattern LEADING_UNDERSCORES = Pattern.compile("^_+");

  public static String typeName(String... name) {
    if (name.length == 1 && isBlank(name[0])) {

      return "Root";
    }

    List<String> values = new ArrayList<>();
    int i = 0;
    for (String s : name) {
      String value = buildPart(i, s, NameFixer.CAMEL_UPPER);
      values.add(value);
      i++;
    }
    return Joiner.on("").join(values);
  }

  public static String methodName(String... name) {

    return checkMethodName(smallCamel(name));
  }

  private static String checkMethodName(String s) {

    if ("getClass".equals(s)) {
      return "getClazz";
    }

    if ("setClass".equals(s)) {
      return "setClazz";
    }

    return s;
  }

  private static String smallCamel(String... name) {

    if (name.length == 1 && isBlank(name[0])) {

      return "root";
    }

    List<String> values = new ArrayList<>();
    for (int i = 0; i < name.length; i++) {
      String s = name[i];
      NameFixer format = NameFixer.CAMEL_LOWER;
      values.add(buildPart(i, s, format));
    }

    return Joiner.on("").join(values);
  }

  public static String variableName(String... name) {

    Matcher m = LEADING_UNDERSCORES.matcher(name[0]);
    if (m.find()) {

      return m.group() + smallCamel(name);
    } else {

      return checkForReservedWord(smallCamel(name));
    }
  }

  private static String checkForReservedWord(String name) {

    if (SourceVersion.isKeyword(name)) {
      return name + "Variable";
    } else {

      return name;
    }
  }


  public static String constantName(String value) {

    return buildJavaFriendlyName(value, NameFixer.ALL_UPPER, 0);
  }

  public static String enumName(String value) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, buildJavaFriendlyName(value, NameFixer.CAMEL_UPPER, 0));
  }

  public static String javaTypeName(amf.client.model.domain.EndPoint resource, AnyShape declaration) {
    return typeName(resource.path().value(), declaration.name().value());
  }

  public static String javaTypeName(EndPoint resource, Operation method, AnyShape declaration) {
    return typeName(resource.path().value(), method.method().value(), declaration.name().value());
  }

  public static String ramlTypeName(EndPoint resource, Operation method, AnyShape declaration) {
    return resource.path().value() + method.method().value() + declaration.name().value();
  }

  public static String ramlTypeName(EndPoint resource, AnyShape declaration) {
    return resource.path().value() + declaration.name().value();
  }

  public static String javaTypeName(EndPoint resource, Operation method, Response response,
                                    AnyShape declaration) {
    return typeName(resource.path().value(), method.method().value(), response.statusCode().value(),
                    declaration.name().value());
  }

  public static String ramlTypeName(EndPoint resource, Operation method, Response response,
                                    AnyShape declaration) {
    return resource.path().value() + method.method().value() + response.statusCode().value() +
            declaration.name().value();
  }


  private Names() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * buildJavaFriendlyName.
   * </p>
   *
   * @param source a {@link String} object.
   * @return a {@link String} object.
   */
  private static String buildJavaFriendlyName(final String source, NameFixer format,
                                              int currentIndex) {
    final String baseName =
        source.replaceAll("\\W+", "_").replaceAll("^_+", "").replaceAll("[^\\w_]", "");
    List<String> friendlyNameBits = new ArrayList<>();
    int i = currentIndex;
    for (String s : baseName.split("_")) {

      if (s.isEmpty()) {
        continue;
      }

      String friendlyName = firstOrOthers(format, i, s);

      if (isDigits(left(friendlyName, 1))) {

        friendlyName = "_" + friendlyName;
      }

      friendlyNameBits.add(friendlyName);
      i++;
    }

    return Joiner.on("").join(friendlyNameBits);
  }

  private static String buildPart(int i, String s, NameFixer format) {

    if ( i == 0 ) {

      // if this is the first name part, remove everything up until the last dot.
      s = s.replaceAll("^.*\\.", "");
    }

    String part;
    if (s.matches(".*[^a-zA-Z0-9].*")) {

      part = buildJavaFriendlyName(s, format, i);
    } else {
      part = firstOrOthers(format, i, s);
    }
    return part;
  }

  private static String firstOrOthers(NameFixer format, int i, String s) {
    if (i == 0) {
      return format.fixFirst(s);
    } else {

      return format.fixOthers(s);
    }
  }


  public static String ramlTypeName(EndPoint resource,
                                    Operation method, Payload typeDeclaration) {

    return resource.path().value() + method.method().value() + typeDeclaration.name().value();
  }

  public static String ramlTypeName(EndPoint resource,
                                    Operation method,
                                    Response response, Payload typeDeclaration) {

    return resource.path().value() + method.method() + response.statusCode().value()
        + typeDeclaration.name();
  }

  public static String javaTypeName(EndPoint resource,
                                    Operation method, Payload typeDeclaration) {
    return typeName(resource.path().value(), method.method().value(), typeDeclaration.name().value());
  }

  public static String javaTypeName(EndPoint resource,
                                    Operation method,
                                    Response response, Payload typeDeclaration) {
    return typeName(resource.path().value(), method.method().value(), response.statusCode().value(),
                    typeDeclaration.name().value());
  }

}
