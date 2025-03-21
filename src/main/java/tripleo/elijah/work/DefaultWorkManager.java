/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.work;

import io.activej.csp.consumer.ChannelConsumer;
import io.activej.csp.supplier.ChannelSuppliers;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import org.jetbrains.annotations.Nullable;
import org.pcollections.TreePVector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.activej.common.exception.FatalErrorHandlers.rethrow;

/**
 * Created 4/26/21 4:22 AM
 */
public class DefaultWorkManager implements WorkManager {
	private final List<WorkList> jobs     = new ArrayList<>();
	private final Set<WorkJob>   doneJobs = new HashSet<>();

	public static Eventloop createEventloop() {
		// noinspection UnnecessaryLocalVariable
		final Eventloop eventLoop = Eventloop.builder()
				.withCurrentThread()
				.withFatalErrorHandler(rethrow())
				.build();
		return eventLoop;
	}

	@Override
	public void addJobs(final WorkList aList) {
		jobs.add(aList);
	}

	@Override
	public void drain() {
		final var _c = this;

		TreePVector<WorkList> jobs1 = TreePVector.empty();
		for (final WorkList workList : jobs) {
			if (workList.isDone()) {
				continue;
			}
			jobs1 = jobs1.plus(workList);
		}

		for (final WorkList jobList : jobs1) {
			for (final WorkJob job : jobList.getJobs()) {
				if (job.isDone()) {
					continue;
				}

				ChannelSuppliers.ofValue(job).streamTo(new ChannelConsumer<WorkJob>() {
					@Override
					public Promise<Void> accept(@Nullable final WorkJob w) {
						if (w != null) {
							if (!_c.doneJobs.contains(w)) {
								w.run(_c);
								if (w.isDone()) {
									_c.doneJobs.add(w);
								}
							}
						}
						return Promise.of(null);
					}

					@Override
					public void closeEx(final Exception e) {
						assert false;
					}
				});
			}
		}
	}
}

//
//
//
