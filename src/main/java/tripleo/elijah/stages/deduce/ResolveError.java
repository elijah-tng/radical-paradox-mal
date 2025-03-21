/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.deduce;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.lang.IdentExpression;
import tripleo.elijah.lang.LookupResult;
import tripleo.elijah.lang.LookupResultList;
import tripleo.elijah.lang.TypeName;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created 12/26/20 5:08 AM
 */
public class ResolveError extends Exception implements ElDiagnostic {
    final @org.jetbrains.annotations.Nullable IdentExpression ident;
    private final @org.jetbrains.annotations.Nullable TypeName typeName;
    private final LookupResultList lrl;

    public ResolveError(final TypeName typeName, final LookupResultList lrl) {
        this.typeName = typeName;
        this.lrl = lrl;
        this.ident = null;
    }

    public ResolveError(final IdentExpression aIdent, final LookupResultList aLrl) {
        ident = aIdent;
        lrl = aLrl;
        typeName = null;
    }

    @Override
    public @NotNull String code() {
        return "S1000";
    }

    @Override
    public @NotNull Severity severity() {
        return Severity.ERROR;
    }

    @Override
    public @NotNull ElLocatable primary() {
        if (typeName == null) {
            return ident;
        } else return typeName;
    }

    @Override
    public @NotNull List<ElLocatable> secondary() {
        @NotNull final Collection<ElLocatable> x = Collections2.transform(resultsList(), new Function<LookupResult, ElLocatable>() {
            @Nullable
            @Override
            public ElLocatable apply(@Nullable final LookupResult input) {
                if (input.getElement() instanceof ElLocatable) {
                    return (ElLocatable) input.getElement();
                }
                return null;
            }
        });
        return new ArrayList<ElLocatable>();
    }

    @Override
    public void report(@NotNull final PrintStream stream) {
        stream.printf("---[%s]---: %s%n", code(), message());
        // linecache.print(primary);
        for (final ElLocatable sec : secondary()) {
            // linecache.print(sec)
        }
        stream.flush();
    }

    private @NotNull String message() {
        if (resultsList().size() > 1) return "Can't choose between alternatives";
        else return "Can't resolve";
    }

    @NotNull
    public List<LookupResult> resultsList() {
        return lrl.results();
    }
}

//
//
//
