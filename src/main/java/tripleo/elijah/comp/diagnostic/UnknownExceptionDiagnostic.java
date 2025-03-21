package tripleo.elijah.comp.diagnostic;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.util.Operation2;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

import java.io.PrintStream;
import java.util.List;

public class UnknownExceptionDiagnostic implements ElDiagnostic {
    private final Operation2<OS_Module> m;

    public UnknownExceptionDiagnostic(final Operation2<OS_Module> aM) {
        m = aM;
    }

    @Override
    public String code() {
        return "9002";
    }

    @Override
    public Severity severity() {
        return Severity.ERROR;
    }

    @Override
    public @NotNull ElLocatable primary() {
        return null;
    }

    @Override
    public @NotNull List<ElLocatable> secondary() {
        return null;
    }

    @Override
    public void report(final PrintStream stream) {
        stream.printf("%s Some error %s%n", code(), m.failure());
    }
}
