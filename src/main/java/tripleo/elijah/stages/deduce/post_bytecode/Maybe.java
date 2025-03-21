package tripleo.elijah.stages.deduce.post_bytecode;

import org.jetbrains.annotations.Nullable;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;

public class Maybe<T> {
    public final           T            o;
    public final @Nullable ElDiagnostic exc;

    public Maybe(final T o, final ElDiagnostic exc) {
        if (o == null) {
            if (exc == null) {
                throw new IllegalStateException("Both o and exc are null!");
            }
        } else {
            if (exc != null) {
                throw new IllegalStateException("Both o and exc are null (2)!");
            }
        }

        this.o = o;
        this.exc = exc;
    }

    public boolean isException() {
        return exc != null;
    }
}
