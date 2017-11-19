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
package org.raml.testutils.matchers;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 3/4/17. Just potential zeroes and ones
 */
public class EntryMatchers {


  public static Matcher<Map.Entry<?, ?>> key(Matcher<?> matcher) {

    return new FeatureMatcher<Map.Entry<?, ?>, Object>((Matcher<? super Object>) matcher, "key", "key") {

      @Override
      protected Object featureValueOf(Map.Entry<?, ?> actual) {
        return actual.getKey();
      }

    };
  }
}
