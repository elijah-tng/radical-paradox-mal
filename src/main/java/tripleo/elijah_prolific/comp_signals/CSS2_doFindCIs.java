package tripleo.elijah_prolific.comp_signals;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.i.CR_State;
import tripleo.elijah.comp.i.ICompilationBus;
import tripleo.elijah.comp.i.ICompilationRunner;
import tripleo.elijah.comp.internal.EDR_CompilationRunner;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;
import tripleo.vendor.org.apache.commons.lang3.tuple.Triple;

import java.util.List;

import static tripleo.elijah_fluffy.util.Helpers.List_of;

public class CSS2_doFindCIs implements CSS2_Signal {
	@SuppressWarnings("unused")
	private final Spacer intentional = new Spacer();

	@Override
	public void trigger(final Compilation compilation, final Object payload) {
		if (payload instanceof Pair) {
			final var payloadpair = (Pair<String[], ICompilationBus>) payload;
			final var args2       = payloadpair.getLeft();
			final var cb          = payloadpair.getRight();

			// TODO map + "extract"

			final CR_State st1 = compilation.getStartup().getCrStateEx();
			// var crap = compilation.getStartup().getCompilationRunner();
			// crap.then(cr1 -> {
			cb.add(new __CSS2_doFindCIs__CB_Process(), Triple.of(args2, cb, st1));
			// });
		}
	}

	public static class Spacer {
	}

	private static class CB_FindCIs implements ICompilationBus.CB_Action {
		private final ICompilationRunner.CR_Action action;
		private final CR_State                     st1;
		private final EDR_CompilationRunner        cr;

		public CB_FindCIs(final String[] args2, @NotNull final CR_State aCRState) {
			st1 = aCRState;
			cr     = aCRState.ca().getCompilation().get__cr();
			action = cr.new CR_FindCIs(args2);
		}

		@Override
		public String name() {
			return action.name();
		}

		@Override
		public void execute() {
			st1.setCur(this);
			action.attach(cr);
			action.execute(st1);
			st1.setCur(null);
		}

		@Contract(value = " -> new", pure = true)
		@Override
		public ICompilationBus.OutputString @NotNull [] outputStrings() {
			return new ICompilationBus.OutputString[0];
		}
	}

	private static class CB_AlmostComplete implements ICompilationBus.CB_Action {
		private final CR_State                     st;
		private       ICompilationRunner.CR_Action a;
		private       EDR_CompilationRunner        cr;

		public CB_AlmostComplete(@NotNull final CR_State aCRState) {
			st = aCRState;
			final var crp = aCRState.ca().getStartup().getCompilationRunner();
			crp.then(cr1 -> {
				// cr = aCRState.ca().getCompilation().__cr;
				cr = (EDR_CompilationRunner) cr1;
				a  = cr.new CR_AlmostComplete();
			});
		}

		@Override
		public String name() {
			return a.name();
		}

		@Override
		public void execute() {
			a.attach(cr);
			a.execute(st);
		}

		@Contract(value = " -> new", pure = true)
		@Override
		public ICompilationBus.OutputString @NotNull [] outputStrings() {
			return new ICompilationBus.OutputString[0];
		}
	}

	private static class __CSS2_doFindCIs__CB_Process implements ICompilationBus.CB_Process, CSS2_Advisable {
		private ICompilationBus.CB_Action a;
		private ICompilationBus.CB_Action b;

		public void adviseObject(final String[] argumentArray, final CR_State aCRState) {
			a = new CB_FindCIs(argumentArray, aCRState);
			b = new CB_AlmostComplete(aCRState);
		}

		@Override
		public @NotNull List<ICompilationBus.CB_Action> steps() {
			assert a != null;
			assert b != null;

			return List_of(a, b);
		}

		@Override
		public void adviseObject(final Object aPayload) {
			assert aPayload instanceof Triple;
			final var payloadtriple = (Triple<String[], ICompilationBus, CR_State>) aPayload;
			final var args2         = payloadtriple.getLeft();
			final var cb            = payloadtriple.getMiddle();
			final var st1           = payloadtriple.getRight();

			adviseObject(args2, st1);
		}
	}
}
