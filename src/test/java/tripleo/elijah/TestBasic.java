/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.activej.test.rules.EventloopRule;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.IO;
import tripleo.elijah.comp.StdErrSink;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.factory.comp.CompilationFactory;
import tripleo.elijah.nextgen.outputstatement.EG_SequenceStatement;
import tripleo.elijah.nextgen.outputstatement.EG_Statement;
import tripleo.elijah.nextgen.outputtree.EOT_OutputFile;
import tripleo.elijah.nextgen.outputtree.EOT_OutputTree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static tripleo.elijah_fluffy.util.Helpers.List_of;

/**
 * @author Tripleo(envy)
 */
public class TestBasic {
	@ClassRule
	public static final EventloopRule eventloopRule = new EventloopRule();

	static <T> @NotNull Promise<T, Void, Void> select(@NotNull final List<T> list, final Predicate<T> p) {
		final DeferredObject<T, Void, Void> d = new DeferredObject<>();
		for (final T t : list) {
			if (p.test(t)) {
				d.resolve(t);
				return d;
			}
		}
		d.reject(null);
		return d;
	}

	private static @NotNull List<String> _mapGetTextToSequence(final EG_SequenceStatement statementSequence) {
		return statementSequence._list().stream().map(EG_Statement::getText).collect(Collectors.toList());
	}

	//<editor-fold desc="testBasic">
	@Ignore
	@Test
	public final void testBasicParse() throws Exception {
		final List<String> ez_files = Files.readLines(new File("test/basic/ez_files.txt"), Charsets.UTF_8);
		final List<String> args = new ArrayList<>();
		args.add("-sE");
		args.addAll(ez_files);
		final Compilation c = CompilationFactory.mkCompilation(new StdErrSink(), new IO());

		c.feedCmdLine(args);

		Assert.assertEquals(0, c.errorCount());
	}

	@Ignore
	@Test
	public final void testBasic() throws Exception {
		final List<String>          ez_files   = Files.readLines(new File("test/basic/ez_files.txt"), Charsets.UTF_8);
		final Map<Integer, Integer> errorCount = new HashMap<Integer, Integer>();
		int                         index      = 0;

		for (final String s : ez_files) {
//			List<String> args = List_of("test/basic", "-sO"/*, "-out"*/);
			final ErrSink     eee = new StdErrSink();
			final Compilation c = CompilationFactory.mkCompilation(eee, new IO());

			c.feedCmdLine(List_of(s, "-sO"));

			if (c.errorCount() != 0) {
				System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
			}
			errorCount.put(index, c.errorCount());
			index++;
		}

		// README this needs changing when running make
		Assert.assertEquals(7, (int) errorCount.get(0)); // TODO Error count obviously should be 0
		Assert.assertEquals(20, (int) errorCount.get(1)); // TODO Error count obviously should be 0
		Assert.assertEquals(9, (int) errorCount.get(2)); // TODO Error count obviously should be 0
	}
	//</editor-fold>

	@Test
	public final void testBasic_listfolders3() throws Exception {
		final String s = "test/basic/listfolders3/listfolders3.ez";
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of(s, "-sO"));

		if (c.errorCount() != 0) {
			System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
		}

		Assert.assertEquals(13, c.getOutputTree().list().size());
		Assert.assertEquals(24, c.errorCount()); // TODO Error count obviously should be 0
	}

	@Test
	public final void testBasic_listfolders4() {
		final String      s = "test/basic/listfolders4/listfolders4.ez";
		final Compilation c = CompilationFactory.mkCompilation(new StdErrSink(), new IO());

		c.feedCmdLine(List_of(s, "-sO"));

		if (c.errorCount() != 0) {
			System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
		}

		Assert.assertEquals(22, c.errorCount()); // TODO Error count obviously should be 0
	}

	@Test
	public final void testBasic_fact1() throws Exception {
		final String s = "test/basic/fact1/main2";

		final ErrSink     eee = new StdErrSink();
		final Compilation c = CompilationFactory.mkCompilation(eee, new IO());

		c.feedCmdLine(List_of(s, "-sO"));

		if (c.errorCount() != 0) {
			System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
		}

		final @NotNull EOT_OutputTree cot = c.getOutputTree();

		c.getErrSink().PrintErrors();

		for (final EOT_OutputFile off : cot.list()) {
			System.err.println("156 " + off.getFilename());
		}


		Assert.assertEquals(19, cot.size()); // TODO why not 6?

		select(cot.list(), f -> f.getFilename().equals("/main2/Main.h")).then(f -> {
			final EG_SequenceStatement statementSequence = (EG_SequenceStatement) f.getStatementSequence();
			System.out.println(_mapGetTextToSequence(statementSequence));
		  });
		select(cot.list(), f -> f.getFilename().equals("/main2/Main.c")).then(f -> {
			final EG_SequenceStatement statementSequence = (EG_SequenceStatement) f.getStatementSequence();
			System.out.println(_mapGetTextToSequence(statementSequence));
		  });

		// TODO Error count obviously should be 0
		Assert.assertEquals(124, c.errorCount()); // FIXME why 123?? 04/15
	}
}

//
//
//
