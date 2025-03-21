/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */

package tripleo.elijah.stages.generate;

import org.jdeferred2.DoneCallback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import tripleo.elijah.comp.AccessBus;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.factory.comp.CompilationFactory;
import tripleo.elijah.stages.gen_generic.GenerateResult;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.Helpers;
import tripleo.elijah_prolific.comp_signals.CSS2_doFindCIs;

public class ElSystemTest {
	private static final String                fut         = "test/basic1/backlink3";
	private final        CSS2_doFindCIs.Spacer intentional = new CSS2_doFindCIs.Spacer();
	private ElSystem    sys;
	private Compilation c;

	@Before
	public void setUp() {
		c = CompilationFactory.mkCompilation();

		final Eventual<ElSystem> stsP = c.getStartup().getSystem();
		sys = new ElSystem();
		sys.setCompilation(c);
		stsP.resolve(sys);

		// c.feedCmdLine(Helpers.List_of(fut));
	}

	@Ignore
	@Test
	public void generateOutputs() {
		final OutputStrategy os = new OutputStrategy();
		os.per(OutputStrategy.Per.PER_CLASS);
		sys.setOutputStrategy(os);

		final Eventual<AccessBus> abP = c.getStartup().getAccessBus();
		abP.then(new DoneCallback<AccessBus>() {
			@Override
			public void onDone(final AccessBus Sab) {
				final var grp = Sab.getGenerateResultPromise();
				grp.then(new DoneCallback<GenerateResult>() {
					@Override
					public void onDone(final GenerateResult result) {
						final int y = 2;
					}
				});

				Sab.subscribe_GenerateResult(new AccessBus.AB_GenerateResultListener() {
					@Override
					public void gr_slot(final GenerateResult gr) {
						sys.generateOutputs(gr);
					}
				});
			}
		});

		try {
			c.feedCmdLine(Helpers.List_of(fut));
		} catch (final Exception aE) {
			Assert.assertFalse("hit", true);
		}

		final int y = 2;
	}
}

//
//
//
