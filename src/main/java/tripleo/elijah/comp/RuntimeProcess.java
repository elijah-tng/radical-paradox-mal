/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.DebugFlags;
import tripleo.elijah.comp.i.ICompilationAccess;
import tripleo.elijah.comp.internal.EDR_ProcessRecord;
import tripleo.elijah.lang.OS_Module;
import tripleo.vendor.mal.stepA_mal;
import tripleo.vendor.mal.types;

public interface RuntimeProcess {
    void run();

    void postProcess();

    void prepare() throws Exception;

    //
    //
    //

    class StageToRuntime {
        @Contract("_, _, _ -> new")
        @NotNull
        public static RuntimeProcesses get(
                final @NotNull Stages stage,
                final @NotNull ICompilationAccess ca,
                final @NotNull EDR_ProcessRecord aPr) {
            final RuntimeProcesses r = new RuntimeProcesses(ca, aPr);

            r.add(stage.getProcess(ca, aPr));

            return r;
        }
    }

    class RuntimeProcesses {
        private final ICompilationAccess ca;
        private final EDR_ProcessRecord pr;
        private RuntimeProcess process;

        public RuntimeProcesses(final @NotNull ICompilationAccess aca, final @NotNull EDR_ProcessRecord aPr) {
            ca = aca;
            pr = aPr;
        }

        public void add(final RuntimeProcess aProcess) {
            process = aProcess;
        }

        public int size() {
            return process == null ? 0 : 1;
        }

        public void run_better() throws Exception {
            // do nothing. job over
            if (ca.getStage() == Stages.E) {
                return;
            }

            // rt.prepare();
            if (DebugFlags.lgJan25) {
                System.err.println("***** RuntimeProcess [prepare] named " + process);
            }
            process.prepare();

            // rt.run();
            if (DebugFlags.lgJan25) {
                System.err.println("***** RuntimeProcess [run    ] named " + process);
            }
            process.run();

            // rt.postProcess(pr);
            if (DebugFlags.lgJan25) {
                System.err.println("***** RuntimeProcess [postProcess] named " + process);
            }
            process.postProcess();

            if (DebugFlags.lgJan25) {
                System.err.println("***** RuntimeProcess^ [postProcess/writeLogs]");
            }
            pr.writeLogs(ca);
        }
    }

    final class EmptyProcess implements RuntimeProcess {
        public EmptyProcess(final ICompilationAccess ignoredACompilationAccess, final EDR_ProcessRecord ignoredAPr) {}

        @Override
        public void run() {}

        @Override
        public void postProcess() {}

        @Override
        public void prepare() {}
    }

    class DStageProcess implements RuntimeProcess {
        private final ICompilationAccess ca;
        private final EDR_ProcessRecord pr;

        @Contract(pure = true)
        public DStageProcess(final ICompilationAccess aCa, final EDR_ProcessRecord aPr) {
            ca = aCa;
            pr = aPr;
        }

        @Override
        public void run() {
            final int y = 2;
        }

        @Override
        public void postProcess() {}

        @Override
        public void prepare() {
            assert ca.getStage() == Stages.D;
        }
    }

    class OStageProcess implements RuntimeProcess {
        final stepA_mal.MalEnv2 env;
        private final EDR_ProcessRecord pr;
        private final ICompilationAccess ca;

        OStageProcess(final ICompilationAccess aCa, final EDR_ProcessRecord aPr) {
            ca = aCa;
            pr = aPr;

            env = new stepA_mal.MalEnv2(null); // TODO what does null mean?

            Preconditions.checkNotNull(pr.ab);
            env.set(new types.MalSymbol("add-pipeline"), new _AddPipeline__MAL(pr.ab));
        }

        @Override
        public void run() {
            final AccessBus ab = pr.ab;

            ab.subscribePipelineLogic((pl) -> {
                final Compilation comp = ca.getCompilation();
                final Pipeline ps = comp.getPipelines();

                try {
                    ps.run();

                    ab.writeLogs();
                } catch (final Exception ex) {
                    //				Logger.getLogger(OStageProcess.class.getName()).log(Level.SEVERE, "Error during Piplines#run
                    // from OStageProcess", ex);
                    comp.getErrSink().exception(ex);
                }
            });
        }

        @Override
        public void postProcess() {}

        @Override
        public void prepare() throws Exception {
            Preconditions.checkNotNull(pr);
            Preconditions.checkNotNull(pr.ab.getGr());

            final AccessBus ab = pr.ab;

            // env.re("(def! GeneratePipeline 'native)");

            // FIXME note moved from ...
            final var xx = env.re("(add-pipeline 'DeducePipeline)");
            System.err.println("(add-pipeline 'DeducePipeline) --> " + xx);

            env.re("(add-pipeline 'GeneratePipeline)");
            env.re("(add-pipeline 'WritePipeline)");
            env.re("(add-pipeline 'WriteMesonPipeline)");

            ab.subscribePipelineLogic(pl -> {
                final Compilation comp = ca.getCompilation();

                for (final OS_Module module : comp.getMod().modules) {
                    pl.addModule(module);
                }
            });
        }

        private static class _AddPipeline__MAL extends types.MalFunction {
            private final AccessBus ab;

            public _AddPipeline__MAL(final AccessBus aAb) {
                ab = aAb;
            }

            @Override
            public types.MalVal apply(final types.MalList args) {
                final types.MalVal a0 = args.nth(0);

                if (a0 instanceof final types.MalSymbol pipelineSymbol) {
                    // 0. accessors
                    final String pipelineName = pipelineSymbol.getName();

                    // 1. observe side effect
                    final EDR_ProcessRecord.PipelinePlugin pipelinePlugin = ab.getPipelinePlugin(pipelineName);
                    if (pipelinePlugin == null) {
                        return types.False;
                    }

                    // 2. produce effect
                    ab.add(pipelinePlugin::instance);
                    return types.True;
                } else {
                    // TODO exception? errSink??
                    return types.False;
                }
            }
        }
    }
}

//
//
//
