/**
 *
 */
package tripleo.elijah.gen.nodes;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.GenBuffer;
import tripleo.elijah.gen.CompilerContext;
import tripleo.elijah.lang.IdentExpression;
import tripleo.elijah_fluffy.util.NotImplementedException;

import java.util.List;

/**
 * @author Tripleo(sb)
 *
 */
public class ClassDeclNode {

    public ClassDeclNode(final String string, final List modifiers, final List<Inherited> inheritance) {
        // TODO Auto-generated constructor stub
    }

    public void GenClassDecl(final CompilerContext cctx, final GenBuffer gbn) {
        // TODO Auto-generated method stub

    }

    public void CloseClassDecl(final CompilerContext cctx, final GenBuffer gbn) {
        // TODO Auto-generated method stub

    }

    public @NotNull IdentExpression type() {
        // TODO what is this
        throw new NotImplementedException();
    }
}
