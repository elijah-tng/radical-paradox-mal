package tripleo.elijah.comp.diagnostic;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class FileNotFoundDiagnostic implements ElDiagnostic {
    private final File f;

    public FileNotFoundDiagnostic(final File aLocal_prelude) {
        f = aLocal_prelude;
    }

    @Override
    public String code() {
        return "9004";
    }

    @Override
    public Severity severity() {
        return Severity.INFO;
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
        stream.println(code() + " File not found " + f.toString());
    }
}
