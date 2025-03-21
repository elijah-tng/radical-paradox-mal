/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: t; c-basic-offset: 4 -*- */
/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.i.PipelineMember;
import tripleo.elijah.entrypoints.EntryPoint;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.nextgen.inputtree.EIT_ModuleList;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.gen_fn.GenerateFunctions;
import tripleo.elijah.stages.gen_fn.GeneratedFunction;
import tripleo.elijah.stages.gen_fn.GeneratedNode;
import tripleo.elijah_fluffy.util.SimplePrintLoggerToRemoveSoon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created 8/21/21 10:10 PM
 */
public class DeducePipeline implements PipelineMember, AccessBus.AB_ModuleListListener {
	private final AccessBus       __ab;
	private       PipelineLogic   pipelineLogic;
	private       List<OS_Module> ms;

	public DeducePipeline(final @NotNull AccessBus ab) {
		__ab = ab;

		ab.subscribePipelineLogic(result -> pipelineLogic = result);
	}

	@Override
	public void run() {
		// TODO move this into a latch and wait for pipelineLogic and modules

		{
			// final List<OS_Module> ms1 = __ab.getCompilation().getModules();
			//
			// if (ms != null) SimplePrintLoggerToRemoveSoon.println_err2("ms.size() == " +
			//                                                                    ms.size());
			// else SimplePrintLoggerToRemoveSoon.println_err2("ms == null");
			// SimplePrintLoggerToRemoveSoon.println_err2("ms1.size() == " + ms1.size());
		}

		final Iterable<GeneratedNode> lgc = pipelineLogic.generatedClassesCopy();
		assert !lgc.iterator().hasNext();

		resolveMods();
		// todo vavr for
		final List<PL_Run2> run2_work = pipelineLogic.getMods().stream()
				.map(mod -> new PL_Run2(
						mod, mod.entryPoints._getMods(), pipelineLogic::getGenerateFunctions, pipelineLogic))
				.toList();

		final List<DeducePhase.GeneratedClasses> lgc2 = new ArrayList<>();
		for (final PL_Run2 plRun2 : run2_work) {
			final DeducePhase.GeneratedClasses run2 = plRun2.run2();
			lgc2.add(run2);
		}

		// NOTE json mod.filename and entrypoints

		// NOTE serialization: ...

		final List<GeneratedNode> lgc3 = new ArrayList<>();

		// TODO how to do this with streams
		for (final DeducePhase.GeneratedClasses generatedClasses : lgc2) {
			for (final GeneratedNode generatedClass : generatedClasses) {
				lgc3.add(generatedClass);
			}
		}

		__ab.resolveLgc(lgc3);
	}

	public void resolveMods() {
		// __ab.resolveModuleList(ms);
	}

	@Override
	public void mods_slot(final @NotNull EIT_ModuleList aModuleList) {
		final List<OS_Module> mods = aModuleList.getMods();

		ms = mods;
	}

	static class PL_Run2 {
		private final OS_Module                              mod;
		private final List<EntryPoint>                       entryPoints;
		private final Function<OS_Module, GenerateFunctions> mapper;
		private final PipelineLogic                          pipelineLogic;

		public PL_Run2(
				final OS_Module mod,
				final List<EntryPoint> entryPoints,
				final Function<OS_Module, GenerateFunctions> mapper,
				final PipelineLogic pipelineLogic) {
			this.mod           = mod;
			this.entryPoints   = entryPoints;
			this.mapper        = mapper;
			this.pipelineLogic = pipelineLogic;
		}

		protected DeducePhase.@NotNull GeneratedClasses run2() {
			final GenerateFunctions gfm         = mapper.apply(mod);
			final DeducePhase       deducePhase = pipelineLogic.getDp();

			gfm.generateFromEntryPoints(entryPoints, deducePhase);

			final @NotNull Iterable<GeneratedNode> lgc            = pipelineLogic.generatedClassesCopy();
			final @NotNull List<GeneratedNode>     resolved_nodes = new ArrayList<>();
			final @NotNull Coder                   coder          = new Coder(deducePhase.getCodeRegistrar());

			for (final GeneratedNode node : lgc) {
				coder.codeNodes(mod, resolved_nodes, node);
			}

			for (final GeneratedNode generatedNode : resolved_nodes) {
				coder.codeNode(generatedNode, mod);
			}

			deducePhase.deduceModule(mod, lgc, true, pipelineLogic.getVerbosity());

			if (false) {
				// it works somewhere else
				final var lgc1 = deducePhase.getGeneratedClasses(); // NOTE .clone/immutable, etc
				PipelineLogic.resolveCheck(lgc1);

				final var lgf = new ArrayList<GeneratedNode>();

				for (final GeneratedNode gn : lgf) {
					if (gn instanceof final GeneratedFunction gf) {
						SimplePrintLoggerToRemoveSoon.println2("----------------------------------------------------------");
						SimplePrintLoggerToRemoveSoon.println2(gf.name());
						SimplePrintLoggerToRemoveSoon.println2("----------------------------------------------------------");
						GeneratedFunction.printTables(gf);
						SimplePrintLoggerToRemoveSoon.println2("----------------------------------------------------------");
					}
				}
			}

			return deducePhase.getGeneratedClasses(); // NOTE .clone/immutable, etc
		}
	}
}

//
// vim:set shiftwidth=4 softtabstop=0 noexpandtab:
//
