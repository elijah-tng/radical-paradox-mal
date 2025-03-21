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
import tripleo.elijah.lang.VariableStatement;
import tripleo.elijah.stages.gen_fn.TypeTableEntry;
import tripleo.elijah.stages.gen_fn.VariableTableEntry;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created 4/13/21 5:46 AM
 */
public class CantDecideType implements ElDiagnostic {
    private final VariableTableEntry vte;
    private final @NotNull Collection<TypeTableEntry> types;

    public CantDecideType(final VariableTableEntry aVte, @NotNull final Collection<TypeTableEntry> aTypes) {
        vte = aVte;
        types = aTypes;
    }

    @Override
    public @NotNull String code() {
        return "E1001";
    }

    @Override
    public @NotNull Severity severity() {
        return Severity.ERROR;
    }

    @Override
    public @NotNull ElLocatable primary() {
        @NotNull final VariableStatement vs = (VariableStatement) vte.getResolvedElement();
        return vs;
    }

    @Override
    public @NotNull List<ElLocatable> secondary() {
        @NotNull final Collection<ElLocatable> c = Collections2.transform(types, new Function<TypeTableEntry, ElLocatable>() {

            @Nullable
            @Override
            public ElLocatable apply(@org.jetbrains.annotations.Nullable final TypeTableEntry input) {
                //				return input.attached.getElement(); // TODO All elements should be Locatable
                //				return (TypeName)input.attached.getTypename();
                return null;
            }
        });

        return new ArrayList<ElLocatable>(c);
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
        return "Can't decide type";
    }
}

//
//
//
