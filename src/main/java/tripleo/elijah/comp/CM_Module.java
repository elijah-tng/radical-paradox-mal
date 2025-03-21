package tripleo.elijah.comp;

import org.jdeferred2.DoneCallback;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.internal.EDR_USE;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.util.Mode;
import tripleo.elijah_fluffy.diagnostic.ExceptionDiagnostic;
import tripleo.elijah_fluffy.util.Eventual;

import java.io.File;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CM_Module {
    private final Eventual<CM_Module> ev = new Eventual<>();
    private Compilation compilation;
    private String f;
    private File file;
    private boolean doOut;
    private Exception exception;
    private Operation<OS_Module> operation;
    private EDR_USE use1;

    public void advise(final Compilation aCompilation, final EDR_USE aUse) {
        compilation = aCompilation;
        this.use1 = aUse;
    }

    public void advise(final String aF, final @NotNull File aFile, final boolean aDoOut) {
        f = aF;
        file = aFile;
        doOut = aDoOut;
    }

    public void action() {
        try {
            final Operation<OS_Module> osModuleOperation = use1.realParseElijjahFile(f, file, doOut);
            if (osModuleOperation.mode() == Mode.SUCCESS) ev.resolve(this); // osModuleOperation.success());
            if (osModuleOperation.mode() == Mode.FAILURE) ev.reject(new ExceptionDiagnostic(osModuleOperation.failure()));
            this.operation = osModuleOperation;
        } catch (final Exception aE) {
            this.exception = aE;
        }
    }

    public Operation<OS_Module> getOperation() {
        if (this.exception == null && this.operation == null) action();
        return this.operation;
    }

    public void then(final DoneCallback<CM_Module> cb) {
        ev.then(cb);
    }

    public OS_Module getModule() {
        assert this.operation.mode() == Mode.SUCCESS;
        assert this.ev.isResolved();
        return this.operation.success();
        // EventualExtract.of(this.ev).getModule();
    }
}
