package tripleo.elijah.stages.deduce;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.stages.deduce.post_bytecode.DefaultStateful;
import tripleo.elijah.stages.deduce.post_bytecode.State;
import tripleo.elijah.stages.deduce.post_bytecode.Stateful;

public interface IStateRunnable extends Stateful {
	void run();

	class ST {
		public static State EXIT_RUN;

		public static void register(final @NotNull DeducePhase aDeducePhase) {
			EXIT_RUN = aDeducePhase.register(new ExitRunState());
		}

		private static class ExitRunState implements State {
			private boolean runAlready;
			private int     identity;

			@Override
			public void apply(final DefaultStateful element) {
				if (element instanceof final StatefulBool statefulBool) {
					final boolean b = statefulBool.getValue();
					System.err.println("24 " + b);
				} else {
					// throw new AssertionError();
				}

				if (!runAlready) {
					runAlready = true;
					if (element instanceof StatefulRunnable) {
						((StatefulRunnable) element).run();
					} else {
						throw new AssertionError();
					}
				}
			}

			@Override
			public void setIdentity(final int aId) {
				this.identity = aId;
				;
			}

			@Override
			public boolean checkState(final DefaultStateful aElement3) {
				final int y = 2;
				return true;
			}
		}
	}
}
