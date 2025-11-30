package tripleo.elijah.comp.internal;

import io.activej.eventloop.Eventloop;
import io.activej.inject.annotation.Inject;
import io.activej.inject.module.Module;
import io.activej.inject.module.Modules;
import io.activej.launcher.Launcher;
import io.activej.service.Service;
import io.activej.service.ServiceGraphModule;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.i.CompilerController;
import tripleo.elijah_prolific.comp_signals.io_activej_trigger.TriggersModule;
import tripleo.elijah_prolific.v.V;

import java.util.List;
import java.util.concurrent.CompletableFuture;

class ProlificCompilationLauncher extends Launcher {
    private final @NotNull Compilation compilation;
    private final @NotNull List<String> args;
    private final @NotNull CompilerController controller;

    /*
     * This requires a little bit of study.
     */

    @Inject
    CompilationControllerActiveJService compilationControllerActiveJService;
    public ProlificCompilationLauncher(
            final @NotNull Compilation aCompilation,
            final @NotNull List<String> aStringList,
            final @NotNull CompilerController aController) {
        compilation = aCompilation;
        args        = aStringList;
        controller  = aController;
    }

    private static void logProgress(final String x) {
        System.out.println("[240914 0068] ProlificCompilationLauncher::logProgress " + x);
    }

    @Override
    protected Module getModule() {
        return Modules.combine(TriggersModule.create(), ServiceGraphModule.create()); // lets not CompilationControllerActiveJService
    }

    @Override
    protected void run() {
        logProgress("|RUNNING|");
    }

    public void launch0() {
        try {
            Eventloop.builder().withCurrentThread().build();
            final var launcher = this;
            launcher.launch(new String[0]);
        } catch (final Throwable aE) {
            compilation.getErrSink().exception(aE);
        }
    }

    @Inject
    private class CompilationControllerActiveJService implements Service {

        @Override
        public @NotNull CompletableFuture<?> start() {
            logProgress("|SERVICE STARTING|");
            try {
                controller.setInputs(compilation, args);
                controller.processOptions();
                controller.runner();
            } finally {
                V.exit(compilation);
            }
            return CompletableFuture.completedFuture(null);
        }
        @Override
        public CompletableFuture<?> stop() {
            logProgress("|SERVICE STOPPING|");
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    protected void onStop() throws Exception {
        logProgress("|ON-STOP|");
    }
}
