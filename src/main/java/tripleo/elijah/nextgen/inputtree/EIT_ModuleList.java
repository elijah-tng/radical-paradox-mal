package tripleo.elijah.nextgen.inputtree;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Coder;
import tripleo.elijah.comp.PipelineLogic;
import tripleo.elijah.entrypoints.EntryPointList;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.gen_fn.GenerateFunctions;
import tripleo.elijah.stages.gen_fn.GeneratedNode;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.work.WorkManager;
import tripleo.elijah_fluffy.util.NotImplementedException;
import tripleo.elijah_fluffy.util.SimplePrintLoggerToRemoveSoon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EIT_ModuleList {
	private final List<OS_Module> mods;
	//	private PipelineLogic __pl;

	@Contract(pure = true)
	public EIT_ModuleList(final List<OS_Module> aMods) {
		mods = aMods;
	}

	public List<OS_Module> getMods() {
		return mods;
	}

	public void process__PL(final Function<OS_Module, GenerateFunctions> ggf,
	                        final PipelineLogic pipelineLogic) {
		// noinspection unused
		final int y = 2;

		for (final OS_Module mod : mods) {
			final @NotNull EntryPointList epl = mod.entryPoints;

			if (epl.size() == 0) {
				continue;
			}

			final GenerateFunctions gfm = ggf.apply(mod);

			final DeducePhase deducePhase = pipelineLogic.getDp();
			// final DeducePhase.@NotNull GeneratedClasses lgc =deducePhase.getGeneratedClasses;

			final _ProcessParams plp = new _ProcessParams(mod, pipelineLogic, gfm, epl, deducePhase);

			__process__PL__each(plp);
		}
	}

	private void __process__PL__each(final @NotNull _ProcessParams plp) {
		final List<GeneratedNode>          resolved_nodes = new ArrayList<>();
		final OS_Module                    mod            = plp.getMod();
		final DeducePhase.GeneratedClasses lgc            = plp.getLgc();

		final int size = lgc.size();
		if (size != 0) {
			NotImplementedException.raise();
			SimplePrintLoggerToRemoveSoon.println_err(String.format("lgc.size() != 0: %d", size));
		}

		plp.generate();

		if (lgc.size() != plp.epl.size()) {
			SimplePrintLoggerToRemoveSoon.println_err(String.format("lgc.size() != epl.size(): %d", size));
		}

		final Coder coder = new Coder(plp.deducePhase.getCodeRegistrar());

		for (final GeneratedNode generatedNode : lgc) {
			coder.codeNodes(mod, resolved_nodes, generatedNode);
		}

		for (final GeneratedNode generatedNode : resolved_nodes) {
			coder.codeNode(generatedNode, mod);
		}

		plp.deduceModule();

		PipelineLogic.resolveCheck(lgc);

		// for (final GeneratedNode gn : lgf) {
		//     if (gn instanceof GeneratedFunction) {
		//         GeneratedFunction gf = (GeneratedFunction) gn;
		//         tripleo.elijah.util.Stupidity.println2("----------------------------------------------------------");
		//         tripleo.elijah.util.Stupidity.println2(gf.name());
		//         tripleo.elijah.util.Stupidity.println2("----------------------------------------------------------");
		//         GeneratedFunction.printTables(gf);
		//         tripleo.elijah.util.Stupidity.println2("----------------------------------------------------------");
		//     }
		// }
	}

	public Stream<OS_Module> stream() {
		return mods.stream();
	}

	public void add(final OS_Module m) {
		mods.add(m);
	}

	private static class _ProcessParams {
		private final OS_Module         mod;
		private final PipelineLogic     pipelineLogic;
		private final GenerateFunctions gfm;

		@NotNull
		private final EntryPointList epl;

		private final DeducePhase deducePhase;

		private _ProcessParams(
				@NotNull final OS_Module aModule,
				@NotNull final PipelineLogic aPipelineLogic,
				@NotNull final GenerateFunctions aGenerateFunctions,
				@NotNull final EntryPointList aEntryPointList,
				@NotNull final DeducePhase aDeducePhase) {
			mod           = aModule;
			pipelineLogic = aPipelineLogic;
			gfm = aGenerateFunctions;
			epl         = aEntryPointList;
			deducePhase = aDeducePhase;
		}

		@Contract(pure = true)
		public OS_Module getMod() {
			return mod;
		}

		public void generate() {
			epl.generate(gfm, deducePhase, getWorkManagerSupplier());
		}

		@Contract(pure = true)
		public @NotNull Supplier<WorkManager> getWorkManagerSupplier() {
			return () -> pipelineLogic.getGeneratePhase().getWm();
		}

		public void deduceModule() {
			deducePhase.deduceModule(mod, getLgc(), getVerbosity());
		}

		@Contract(pure = true)
		public DeducePhase.GeneratedClasses getLgc() {
			return deducePhase.getGeneratedClasses();
		}

		@Contract(pure = true)
		public ElLog.@NotNull Verbosity getVerbosity() {
			return pipelineLogic.getVerbosity();
		}
	}
}
