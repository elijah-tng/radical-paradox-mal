/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.gen_fn;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.PipelineLogic;
import tripleo.elijah.comp.internal.EDR_Compilation;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.work.WorkJob;
import tripleo.elijah.work.WorkList;
import tripleo.elijah.work.WorkManager;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.Ok;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created 5/16/21 12:35 AM
 */
public class GeneratePhase {
	private final Map<OS_Module, GenerateFunctions> generateFunctions;
	private final ElLog.Verbosity                   verbosity;
	private final PipelineLogic                     pipelineLogic;
	private final Compilation                       compilation;
	private final WorkManager                       wm;

	public GeneratePhase(
			final ElLog.Verbosity aVerbosity,
			final PipelineLogic aPipelineLogic,
			final Compilation aCompilation) {
		verbosity     = aVerbosity;
		pipelineLogic = aPipelineLogic;
		compilation       = aCompilation;
		generateFunctions = new HashMap<>();
		wm                = new _GeneratePhaseWorkManager();
	}

	public @NotNull GenerateFunctions getGenerateFunctions(@NotNull final OS_Module mod) {
		final GenerateFunctions Result;
		if (generateFunctions.containsKey(mod)) {
			Result = generateFunctions.get(mod);
		} else {
			Result = new GenerateFunctions(this, mod, pipelineLogic);
			generateFunctions.put(mod, Result);
		}
		return Result;
	}

	public ElLog.Verbosity getVerbosity() {
		return verbosity;
	}

	public WorkManager getWm() {
		return wm;
	}

	private class _GeneratePhaseWorkManager implements WorkManager {
		private final Eventual<Ok>                     allDone      = new Eventual<>("GeneratePhase::WorkManager::allDone");
		private final AtomicBoolean                    fakespinlock = new AtomicBoolean(false);
		private final EDR_Compilation.ExecutorService_ cne          = compilation.newExecutor();
		private final Map<WorkJob, Object>             map          = new HashMap<>();
		private int totalSize = 0;
		private CountDownLatch cdl;
		// private final Lock      lock = new ReentrantLock();
		// private final Condition cond = lock.newCondition();

		@Override
		public void addJobs(final WorkList aList) {
			final var                    _c   = this;
			final ImmutableList<WorkJob> jobs = aList.getJobs();
			totalSize += jobs.size();
			cne.submit(() -> __submitHelper(jobs, _c));
		}

		@Override
		public void drain() {
			if (cdl == null) {
				cdl = new CountDownLatch(totalSize);
			}

			if (cdl.getCount() > 0) {
				cne.submit(() -> {
					for (final WorkJob workJob : map.keySet()) {
						workJob.run(this);
						cdl.countDown();
					}
				});

				try {
					cdl.await(3, TimeUnit.SECONDS);
				} catch (final InterruptedException aE) {
					throw new RuntimeException(aE);
				}
			}

			if (false) {
				int i = 0;
				while (!fakespinlock.get()) {
					if (allDone.isResolved() || i++ == 5) {
						fakespinlock.set(true);
					}

					// FIXME Condition.await()
					try {
						Thread.sleep(100);
					} catch (final InterruptedException aE) {
						throw new RuntimeException(aE);
					}
				}
			}
		}

		private void __submitHelper(final ImmutableList<WorkJob> j, final _GeneratePhaseWorkManager _c) {
			for (final WorkJob workJob : j) {
				if (workJob instanceof final WlGenerateClass wlgc) {
					wlgc.run(_c);
					final var tt = wlgc.getResult();
					_c.map.put(wlgc, tt);
				} else {
					workJob.run(_c);
				}
			}
			if (false) {
				// _c.cond.signal();
			}
			_c.allDone.resolve(Ok.instance());
		}

		@Override
		public String toString() {
			// noinspection StringBufferReplaceableByString
			final StringBuilder sb = new StringBuilder("_GeneratePhaseWorkManager{");
			sb.append(" totalSize=").append(totalSize); //
			sb.append(", completed=").append(map.size()); //
			sb.append('}');
			return sb.toString();
		}
	}
}

//
//
//
