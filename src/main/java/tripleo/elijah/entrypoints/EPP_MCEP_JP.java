package tripleo.elijah.entrypoints;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.FunctionDef;
import tripleo.elijah.stages.deduce.ClassInvocation;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.gen_fn.GenerateFunctions;
import tripleo.elijah.work.WorkList;

public class EPP_MCEP_JP implements EntryPointProcessor {

	private final MainClassEntryPoint ep;
	private final DeducePhase         deducePhase;
	private final WorkList            workList;
	private final GenerateFunctions   generateFunctions;
	private final EPP_MCEP p;

	public EPP_MCEP_JP(final MainClassEntryPoint aEp,
	                   final DeducePhase aDeducePhase,
	                   final WorkList aWorkList,
	                   final GenerateFunctions aGenerateFunctions) {
		ep          = aEp;
		deducePhase = aDeducePhase;
		workList          = aWorkList;
		generateFunctions = aGenerateFunctions;

		p = new EPP_MCEP(ep, deducePhase, workList, generateFunctions);
	}

	@Override
	public void process() {
		final @NotNull ClassStatement cs = ep.getKlass();
		final @NotNull FunctionDef    f  = ep.getMainFunction();
		final ClassInvocation         ci = deducePhase.registerClassInvocation(cs, null);

		assert ci != null;

		//
		p.process();
	}
}
