package org.raml.pojotoraml;

import org.raml.pojotoraml.field.SubFun;

import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class Fun {

    String one;
    int two;
    SubFun sub;
    List<String> listOfStrings;
    List<SubFun> listOfSubs;
    int[] arrayOfInts;
    SubFun[] arrayOfSubs;
    SimpleEnum enumeration;
    Map<String, String>  additional;

    public List<String> stringMethod() {
        return null;
    }

}
