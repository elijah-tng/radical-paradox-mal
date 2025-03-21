package tripleo.elijah.factory.comp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.IO;
import tripleo.elijah.comp.StdErrSink;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.comp.internal.EDR_Compilation;
import tripleo.elijah.testing.comp.IFunctionMapHook;

import java.util.List;

public class CompilationFactory {

    public static @NotNull EDR_Compilation mkCompilation2(final List<IFunctionMapHook> aMapHooks) {
        final StdErrSink errSink = new StdErrSink();
        final IO io = new IO();

        final @NotNull EDR_Compilation c = mkCompilation(errSink, io);

        c.testMapHooks(aMapHooks);

        return c;
    }

    @Contract("_, _ -> new")
    public static @NotNull EDR_Compilation mkCompilation(final ErrSink eee, final IO io) {
        return new EDR_Compilation(eee, io);
    }

    @Contract(" -> new")
    public static @NotNull Compilation mkCompilation() {
        return mkCompilation(new StdErrSink(), new IO());
    }
}
