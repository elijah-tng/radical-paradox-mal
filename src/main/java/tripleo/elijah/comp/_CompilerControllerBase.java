package tripleo.elijah.comp;

import tripleo.elijah.comp.i.CompilerController;
import tripleo.elijah.comp.i.ICompilationBus;
import tripleo.elijah.comp.i.ICompilationRunner;
import tripleo.elijah.comp.i.OptionsProcessor;
import tripleo.elijah.comp.internal.EDR_CompilationBus;
import tripleo.elijah.comp.internal.EDR_CompilationRunner;
import tripleo.elijah_prolific.comp_signals.CSS2_doFindCIs;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class _CompilerControllerBase implements CompilerController {
    protected String[] args2;
    protected EDR_CompilationBus cb;
    protected Compilation c;
    protected List<String> args;

    @Override
    public void printUsage() {
        System.out.println("Usage: eljc [--showtree] [-sE|O] <directory or .ez file names>");
    }

    @Override
    public void processOptions() {
        final OptionsProcessor op = new ApacheOptionsProcessor();
        final CompilerInstructionsObserver cio = new CompilerInstructionsObserver(c, c.get_cis());

        final var cbp = c.getStartup().getCompilationBus();
        cb = new EDR_CompilationBus(c);
        cbp.resolve(cb);

        try {
            args2 = op.process(c, args, cb);
        } catch (final Exception e) {
            c.getErrSink().exception(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void runner() {
        try {
            final ICompilationRunner __cr = new EDR_CompilationRunner(c, cb);
            c.register(__cr);
            c.signal(new CSS2_doFindCIs(), Pair.of(args2, cb));
        } catch (final Exception e) {
            c.getErrSink().exception(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ICompilationBus getCB() {
        return this.cb;
    }
}
