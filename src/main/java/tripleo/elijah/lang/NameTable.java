package tripleo.elijah.lang;

import tripleo.elijah_fluffy.util.SimplePrintLoggerToRemoveSoon;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tripleo
 * <p/>
 * Created Mar 29, 2020 at 7:00:10 PM
 */
// NOTE something...
public class NameTable {

    final Map<String, TypedElement> members = new HashMap<>();

    public void add(final OS_Element element, final String name, final OS_Type dtype) {
        // element.setType(dtype);
        members.put(name, new TypedElement(element, dtype));
        SimplePrintLoggerToRemoveSoon.println_err2("[NameTable#add] " + members);
    }

    static class TypedElement {
        final OS_Element element;
        final OS_Type type;

        public TypedElement(final OS_Element element2, final OS_Type dtype) {
            element = element2;
            type    = dtype;
        }

        @Override
        public String toString() {
            return "TypedElement{" + "element=" + element + ", type=" + type + '}';
        }
    }
}

//
//
//
