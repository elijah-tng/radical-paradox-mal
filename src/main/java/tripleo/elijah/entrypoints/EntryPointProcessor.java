package tripleo.elijah.entrypoints;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.FunctionDef;
import tripleo.elijah.stages.deduce.ClassInvocation;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.deduce.FunctionInvocation;
import tripleo.elijah.stages.gen_fn.*;
import tripleo.elijah.stages.gen_generic.ICodeRegistrar;
import tripleo.elijah.work.WorkList;
import tripleo.elijah.world.i.LivingRepo;

public interface EntryPointProcessor {
	static @NotNull EntryPointProcessor dispatch(
			final EntryPoint ep,
			final DeducePhase aDeducePhase,
			final WorkList aWorkList,
			final GenerateFunctions aGenerateFunctions) {
		if (ep instanceof MainClassEntryPoint) {
			return new EPP_MCEP_JP((MainClassEntryPoint) ep, aDeducePhase, aWorkList, aGenerateFunctions);
			// return new EPP_MCEP((MainClassEntryPoint) ep, aDeducePhase, aWorkList, aGenerateFunctions);
		} else if (ep instanceof ArbitraryFunctionEntryPoint) {
			return new EPP_AFEP((ArbitraryFunctionEntryPoint) ep, aDeducePhase, aWorkList, aGenerateFunctions);
		}

		throw new IllegalStateException();
	}

	void process();

	class EPP_MCEP implements EntryPointProcessor {
		private final MainClassEntryPoint mcep;
		private final DeducePhase         deducePhase;
		private final WorkList            wl;
		private final GenerateFunctions   generateFunctions;

		public EPP_MCEP(
				final MainClassEntryPoint aEp,
				final DeducePhase aDeducePhase,
				final WorkList aWl,
				final GenerateFunctions aGenerateFunctions) {
			mcep        = aEp;
			deducePhase = aDeducePhase;
			wl                = aWl;
			generateFunctions = aGenerateFunctions;
		}

		@Override
		public void process() {
			final @NotNull ClassStatement cs = mcep.getKlass();
			final @NotNull FunctionDef    f  = mcep.getMainFunction();
			final ClassInvocation         ci = deducePhase.registerClassInvocation(cs, null);

			assert ci != null;

			final ICodeRegistrar codeRegistrar = new ICodeRegistrar() {
				final Compilation compilation = deducePhase._compilation();

				@Override
				public void registerNamespace(final GeneratedNamespace aNamespace) {
					compilation.get_repo().addNamespace(aNamespace, LivingRepo.Add.NONE);
				}

				@Override
				public void registerClass(final GeneratedClass aClass) {
					compilation.get_repo().addClass(aClass, LivingRepo.Add.MAIN_CLASS);
				}

				@Override
				public void registerFunction(final BaseGeneratedFunction aFunction) {
					compilation.get_repo().addFunction(aFunction, LivingRepo.Add.MAIN_FUNCTION);
				}
			};

			final @NotNull WlGenerateClass job = new WlGenerateClass(generateFunctions, ci, deducePhase.getGeneratedClasses(), codeRegistrar);
			wl.addJob(job);

			final @NotNull FunctionInvocation fi = new FunctionInvocation(f, null, ci, deducePhase.getGeneratePhase());
			// fi.setPhase(phase);
			final @NotNull WlGenerateFunction job1 = new WlGenerateFunction(generateFunctions, fi, codeRegistrar);
			wl.addJob(job1);
		}
	}

	class EPP_AFEP implements EntryPointProcessor {
		private final ArbitraryFunctionEntryPoint afep;
		private final DeducePhase                 deducePhase;
		private final WorkList                    wl;
		private final GenerateFunctions           generateFunctions;

		public EPP_AFEP(
				final ArbitraryFunctionEntryPoint aEp,
				final DeducePhase aDeducePhase,
				final WorkList aWl,
				final GenerateFunctions aGenerateFunctions) {
			afep        = aEp;
			deducePhase = aDeducePhase;
			wl                = aWl;
			generateFunctions = aGenerateFunctions;
		}

		@Override
		public void process() {
			final @NotNull FunctionDef     f             = afep.getFunction();
			final @NotNull ClassInvocation ci            = deducePhase.registerClassInvocation((ClassStatement) afep.getParent());
			final ICodeRegistrar           codeRegistrar = deducePhase.getCodeRegistrar();

			final WlGenerateClass job = new WlGenerateClass(generateFunctions, ci, deducePhase.getGeneratedClasses(), codeRegistrar);
			wl.addJob(job);

			final @NotNull FunctionInvocation fi = new FunctionInvocation(f, null, ci, deducePhase.getGeneratePhase());
			// fi.setPhase(phase);
			final WlGenerateFunction job1 = new WlGenerateFunction(generateFunctions, fi, codeRegistrar);
			wl.addJob(job1);
		}
	}
}
