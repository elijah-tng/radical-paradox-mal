package tripleo.elijah.stages.deduce.post_bytecode;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;
import tripleo.elijah_fluffy.util.NotImplementedException;

import java.io.PrintStream;
import java.util.List;

public class ZeroPotentialDiagnostic implements ElDiagnostic {
    @Override
    public String code() {
        NotImplementedException.raise();
        return null;
    }

    @Override
    public Severity severity() {
        NotImplementedException.raise();
        return null;
    }

    @Override
    public @NotNull ElLocatable primary() {
        NotImplementedException.raise();
        return null;
    }

    @Override
    public @NotNull List<ElLocatable> secondary() {
        NotImplementedException.raise();
        return null;
    }

    @Override
    public void report(final PrintStream stream) {
        NotImplementedException.raise();
        final int y = 2;
    }
}
