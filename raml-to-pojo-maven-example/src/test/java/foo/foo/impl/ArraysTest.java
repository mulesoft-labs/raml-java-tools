package foo.foo.impl;

import foo.foo.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class ArraysTest {


    @Test
    public void simpleArrays() throws Exception {

        ChildArray childArray = new ChildArray();
        childArray.add(new ChildImpl());

        assertEquals(1, childArray.size());
    }

    @Test
    public void simpleArraysWithBrackets() throws Exception {

        ChildBracketArray childArray = new ChildBracketArray();
        childArray.add(new ChildBracketArray.ObjectTypeImpl());

        assertEquals(1, childArray.size());
    }


    @Test
    public void inlineArraysOfSpecificType() throws Exception {

        InlineChildren childArray = new InlineChildren();
        childArray.add(new InlineChildren.ChildTypeImpl());

        assertEquals(1, childArray.size());
    }


    @Test
    public void inlineArraysOfObjects() throws Exception {

        InlineStuff childArray = new InlineStuff();
        childArray.add(new InlineStuff.ObjectTypeImpl());

        assertEquals(1, childArray.size());
    }


    @Test
    public void inlinePropertiesArraysOfObjects() throws Exception {

        MotherImpl mi = new MotherImpl();
        Mother.SmallerType smaller = new Mother.SmallerType();
        mi.setSmaller(smaller);

        Mother.SmallerType.ObjectTypeImpl i = new Mother.SmallerType.ObjectTypeImpl();
        smaller.add(i);
        assertEquals(1, mi.getSmaller().size());
    }


    @Test
    public void inlinePropertiesComplicated() throws Exception {

        MotherImpl mi = new MotherImpl();
        Mother.ComplicatedChildrenType complicated = new Mother.ComplicatedChildrenType();
        mi.setComplicatedChildren(complicated);

        complicated.add("foo");
        assertEquals(1, mi.getComplicatedChildren().size());
    }

}
