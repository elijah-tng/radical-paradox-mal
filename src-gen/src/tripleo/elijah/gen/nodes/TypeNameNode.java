/**
 *
 */
package tripleo.elijah.gen.nodes;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.lang.IdentExpression;

/**
 * @author Tripleo(acer)
 *
 */
public class TypeNameNode {

    public String genType;

    public TypeNameNode(@NotNull final IdentExpression return_type) {
        // TODO Auto-generated constructor stub
        genType = return_type.getText(); // TODO wrong prolly
    }

    public String getText() {
        return null;
    }
}
