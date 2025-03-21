package tripleo.elijah.stages.deduce;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.stages.gen_fn.BaseGeneratedFunction;
import tripleo.elijah.stages.gen_fn.IdentTableEntry;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

import java.io.PrintStream;
import java.util.List;

public class CantResolveElement implements ElDiagnostic {
    private final String message;
    private final IdentTableEntry identTableEntry;
    private final BaseGeneratedFunction generatedFunction;

    public CantResolveElement(
            final String aMessage,
            final IdentTableEntry aIdentTableEntry,
            final BaseGeneratedFunction aBaseGeneratedFunction) {
        message = aMessage;
        identTableEntry = aIdentTableEntry;
        generatedFunction = aBaseGeneratedFunction;
    }

    @Override
    public String code() {
        return null;
    }

    @Override
    public Severity severity() {
        return null;
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
        stream.println(message);
    }
}
