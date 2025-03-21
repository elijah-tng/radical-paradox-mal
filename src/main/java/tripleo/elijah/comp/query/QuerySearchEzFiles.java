package tripleo.elijah.comp.query;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.Finally;
import tripleo.elijah.comp.IO;
import tripleo.elijah.comp.Operation;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.comp.i.ICompilationRunner;
import tripleo.elijah.comp.internal.CiReasoning;
import tripleo.elijah.comp.specs.EzSpec;
import tripleo.elijah.util.Operation2;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QuerySearchEzFiles {
	final         FilenameFilter     ez_files_filter = new FilenameFilter() {
		@Override
		public boolean accept(final File file, final String s) {
			final boolean matches2 = Pattern.matches(".+\\.ez$", s);
			return matches2;
		}
	};
	private final Compilation        c;
	private final ErrSink            errSink;
	private final IO                 io;
	private final ICompilationRunner cr;

	public QuerySearchEzFiles(
			final Compilation aC, final ErrSink aErrSink, final IO aIo, final ICompilationRunner aCr) {
		c       = aC;
		errSink = aErrSink;
		io      = aIo;
		cr      = aCr;
	}

	// TODO list of operations, not operation of list
	public Operation2<List<CompilerInstructions>> process(final File directory) {
		final List<CompilerInstructions> R    = new ArrayList<>();
		final String[]                   list = directory.list(ez_files_filter);

		if (list != null) {
			for (final String file_name : list) {
				try {
					final File                            file   = new File(directory, file_name);
					final Operation<CompilerInstructions> oci    = parseEzFile(file, file.toString(), errSink, io, c);
					final CompilerInstructions            ezFile = oci.success();
					if (ezFile != null) {

						c.reports().addInput(() -> file_name, Finally.Out2.EZ);

						R.add(ezFile);
					} else {
						errSink.reportError("9995 ezFile is null " + file);
					}
				} catch (final Exception e) {
					errSink.exception(e);
				}
			}
		}
		return Operation2.success(R);
	}

	@NotNull
	Operation<CompilerInstructions> parseEzFile(
			final @NotNull File f,
			final String file_name,
			final ErrSink errSink,
			final IO io,
			final Compilation c) {
		System.out.printf("** [qr] %s%n", new File(file_name).getAbsolutePath());

		Operation<CompilerInstructions> result;

		final File   file = new File(file_name);
		final EzSpec spec;

		try (final InputStream s = io.readFile(file)) {
			spec   = new EzSpec(file_name, s, file, CiReasoning.SEARCH);
			result = cr.realParseEzFile(spec, cr.ezCache());
		} catch (final IOException aE) {
			result = Operation.failure(aE);
		}

		return result;
	}
}
