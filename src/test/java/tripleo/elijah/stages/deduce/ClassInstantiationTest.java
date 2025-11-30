/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.deduce;

import org.junit.Test;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.factory.comp.CompilationFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static tripleo.elijah_fluffy.util.Helpers.List_of;

/**
 * Created 3/5/21 4:32 AM
 */
public class ClassInstantiationTest {

	@Test
	public void classInstantiation() {
		final String f = "test/basic1/class_instantiation/";
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of(f));

		assertThat(c.errorCount()) //
				// .isEqualTo(2);
				.isEqualTo(1);

	}

	@Test
	public void classInstantiation2() {
		final String f = "test/basic1/class_instantiation2/";
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of(f));

		assertThat(c.errorCount()) //
				// .isEqualTo(2);
				.isEqualTo(1);

	}

	@Test
	public void classInstantiation3() {
		final String f = "test/basic1/class_instantiation3/";
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of(f));

		assertThat(c.errorCount()) //
				// .isEqualTo(2);
				.isEqualTo(1);

	}
}

//
//
//
