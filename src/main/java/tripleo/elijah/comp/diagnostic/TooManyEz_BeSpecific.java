package tripleo.elijah.comp.diagnostic;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

import java.io.PrintStream;
import java.util.List;

public class TooManyEz_BeSpecific implements ElDiagnostic {
    final String message = "Too many .ez files, be specific.";

    @Override
    public String code() {
        return "9997";
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
        stream.printf("%s %s%n", code(), message);
    }
}
