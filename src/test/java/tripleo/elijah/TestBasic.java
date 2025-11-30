/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah;

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
import tripleo.elijah.nextgen.outputtree.EOT_OutputType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
		final List<String> ez_files = Files.readLines(new File("test/basic/ez_files.txt"), StandardCharsets.UTF_8);
		final List<String> args     = new ArrayList<>();
		args.add("-sE");
		args.addAll(ez_files);
		final Compilation c = CompilationFactory.mkCompilation(new StdErrSink(), new IO());

		c.feedCmdLine(args);

		Assert.assertEquals(0, c.errorCount());
	}

	@Ignore
	@Test
	public final void testBasic() throws Exception {
		final List<String>          ez_files   = Files.readLines(new File("test/basic/ez_files.txt"), StandardCharsets.UTF_8);
		final Map<Integer, Integer> errorCount = new HashMap<>();
		int                         index      = 0;

		for (final String s : ez_files) {
//			List<String> args = List_of("test/basic", "-sO"/*, "-out"*/);
			final ErrSink     eee = new StdErrSink();
			final Compilation c   = CompilationFactory.mkCompilation(eee, new IO());

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
	public final void testBasic_listFolders3() {
		final String      s = "test/basic/listfolders3/listfolders3.ez";
		final Compilation c = CompilationFactory.mkCompilation();

		c.feedCmdLine(List_of(s, "-sO"));

		if (c.errorCount() != 0) {
			System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
		}

		final List<@NotNull EOT_OutputFile> outputFileList = c.getOutputTree().list();
		assertThat(outputFileList.size()) //
				// .isEqualTo(13); //
				.isEqualTo(3);

		assertThat(p(outputFileList).stream().map(Objects::toString)) //
				.containsExactlyInAnyOrderElementsOf(List_of( //
						"pEOT_OutputFile[aType=SOURCES, aFilename=/listfolders3/Main.c]", //
						"pEOT_OutputFile[aType=SOURCES, aFilename=/listfolders3/Main.h]", //
						"pEOT_OutputFile[aType=ERROR_REPORT, aFilename=error-report.json]" //
				));

		assertThat(c.errorCount()) //
				// .isEqualTo(24); //
				.withFailMessage("Error count obviously should be 0")
				// .isEqualTo(3);
				.isEqualTo(2);

	}

	private List<pEOT_OutputFile> p(final List<EOT_OutputFile> aOutputFileList) {
		final List<pEOT_OutputFile> x = new ArrayList<>();
		for (EOT_OutputFile eotOutputFile : aOutputFileList) {
			final EOT_OutputType type     = eotOutputFile.getType();
			final String         filename = eotOutputFile.getFilename();
			x.add(new pEOT_OutputFile(type, filename));
		}
		return x;
	}

	public record pEOT_OutputFile(EOT_OutputType aType, String aFilename) {
	}

	@Test
	public final void testBasic_listFolders4() {
		final String      s = "test/basic/listfolders4/listfolders4.ez";
		final Compilation c = CompilationFactory.mkCompilation(new StdErrSink(), new IO());

		c.feedCmdLine(List_of(s, "-sO"));

		if (c.errorCount() != 0) {
			System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
		}

		final int was22 = 2;
		Assert.assertEquals(was22, c.errorCount()); // TODO Error count obviously should be 0
	}

	@SuppressWarnings("CommentedOutCode")
	@Test
	public final void testBasic_fact1() {
		final String s = "test/basic/fact1/main2";

		final ErrSink     eee = new StdErrSink();
		final Compilation c   = CompilationFactory.mkCompilation(eee, new IO());

		c.feedCmdLine(List_of(s, "-sO"));

		if (c.errorCount() != 0) {
			System.err.printf("Error count should be 0 but is %d for %s%n", c.errorCount(), s);
		}

		final @NotNull EOT_OutputTree cot = c.getOutputTree();

		c.getErrSink().PrintErrors();

		for (final EOT_OutputFile off : cot.list()) {
			System.err.println("156 " + off.getFilename());
		}

		assertThat(cot.size()) //
				// .isEqualTo(6);  // TODO why not 6?
				// .isEqualTo(19); //
				.isEqualTo(3);

		select(cot.list(), f -> f.getFilename().equals("/main2/Main.h")).then(f -> {
			final EG_SequenceStatement statementSequence = (EG_SequenceStatement) f.getStatementSequence();
			System.out.println(_mapGetTextToSequence(statementSequence));
		});
		select(cot.list(), f -> f.getFilename().equals("/main2/Main.c")).then(f -> {
			final EG_SequenceStatement statementSequence = (EG_SequenceStatement) f.getStatementSequence();
			System.out.println(_mapGetTextToSequence(statementSequence));
		});

		// TODO Error count obviously should be 0
		// assertThat(c.getOutputTree().list().size()) //
		// 		// .isEqualTo(13); //
		// 		.isEqualTo(3);
		//
		// assertThat(c.getOutputTree().list()) //
		// 		.containsExactlyElementsOf(List_of( //
		// 		                                    null
		// 		));

		assertThat(c.errorCount()) //
				// .isEqualTo(123);  // FIXME why 123?? 04/15
				// .isEqualTo(124); //
				.withFailMessage("Error count obviously should be 0")
				.isEqualTo(3);


	}
}

//
//
//
