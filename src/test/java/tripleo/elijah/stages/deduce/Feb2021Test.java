/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.stages.deduce;

import org.junit.Assert;
import org.junit.Test;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.factory.comp.CompilationFactory;

import static tripleo.elijah_fluffy.util.Helpers.List_of;

/**
 * Created 9/9/21 4:16 AM
 */
public class Feb2021Test {

	@Test
	public void testProperty() {
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of("test/feb2021/property/"));

		Assert.assertEquals(-1, c.errorCount());
	}

	@Test
	public void testFunction() {
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of("test/feb2021/function/"));

		Assert.assertEquals(-1, c.errorCount());
	}

	@Test
	public void testHier() {
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of("test/feb2021/hier/"));

		Assert.assertEquals(-1, c.errorCount());
	}

}

//
//
//
