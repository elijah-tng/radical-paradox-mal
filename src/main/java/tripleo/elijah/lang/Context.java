/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.contexts.ModuleContext;

import java.util.ArrayList;
import java.util.List;

// TODO is this right, or should be interface??
//  nope
public abstract class Context {
    public Context() {}

    public LookupResultList lookup(@NotNull final String name) {
        final LookupResultList Result = new LookupResultList();
        return lookup(name, 0, Result, new ArrayList<Context>(), false);
    }

    public abstract LookupResultList lookup(
            String name, int level, LookupResultList Result, List<Context> alreadySearched, boolean one);

    public @NotNull Compilation compilation() {
        final OS_Module module = module();
        return module.parent;
    }

    public @NotNull OS_Module module() {
        Context ctx = this; // getParent();
        while (!(ctx instanceof ModuleContext)) {
            if (ctx == null) {
                throw new AssertionError();
            }
            ctx = ctx.getParent();
        }
        return ((ModuleContext) ctx).getCarrier();
    }

    public abstract @Nullable Context getParent();
}

//
//
//
