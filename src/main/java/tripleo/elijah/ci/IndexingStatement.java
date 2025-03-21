/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.ci;

import com.google.gson.annotations.Expose;
import tripleo.elijah.lang.ExpressionList;
import tripleo.vendor.antlr277.Token;

/**
 * @author Tripleo
 * <p>
 * Created 04/15/20 04:59:21 AM
 * <p>
 * Created 1/8/21 07:19 AM
 */
public class IndexingStatement {
    private final CompilerInstructions parent;
    @Expose
    private       Token                name;
    @Expose
    private       ExpressionList       exprs;

    public IndexingStatement(final CompilerInstructions module) {
        parent = module;
    }

    public void setName(final Token i1) {
        name = i1;
    }

    public void setExprs(final ExpressionList el) {
        exprs = el;
    }
}

//
//
//
