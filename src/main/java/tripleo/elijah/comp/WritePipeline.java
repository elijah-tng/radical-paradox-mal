/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.comp.functionality.f203.F203;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.comp.i.PipelineMember;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.nextgen.outputstatement.*;
import tripleo.elijah.nextgen.outputtree.EOT_OutputFile;
import tripleo.elijah.nextgen.outputtree.EOT_OutputTree;
import tripleo.elijah.stages.gen_generic.GenerateResult;
import tripleo.elijah.stages.gen_generic.GenerateResultItem;
import tripleo.elijah.stages.generate.ElSystem;
import tripleo.elijah.stages.generate.OutputStrategy;
import tripleo.elijah_fluffy.util.*;
import tripleo.elijah_prolific.comp_signals.CSS2_Signal;
import tripleo.elijah_prolific.v.V;
import tripleo.util.buffer.Buffer;
import tripleo.util.buffer.DefaultBuffer;
import tripleo.util.buffer.TextBuffer;
import tripleo.util.io.CharSink;
import tripleo.util.io.FileCharSink;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created 8/21/21 10:19 PM
 */
public class WritePipeline implements PipelineMember, AccessBus.AB_GenerateResultListener {
	private final OutputStrategy os;
	private final ElSystem       sys;
	private final Compilation    c;
	private       GenerateResult gr;

	public WritePipeline(@NotNull final AccessBus ab) {
		c = ab.getCompilation();

		os = new OutputStrategy();
		os.per(OutputStrategy.Per.PER_CLASS); // TODO this needs to be configured per lsp

		final Eventual<ElSystem> sysP = c.getStartup().getSystem();
		if (sysP.isPending()) {
			final ElSystem sys1 = new ElSystem();

			sys1.verbose = false; // TODO flag? ie CompilationOptions
			sys1.setCompilation(c);
			sys1.setOutputStrategy(os);

			sysP.resolve(sys1);
			sys = sys1;
		} else {
			sys = EventualExtract.of(sysP);
		}

		ab.subscribe_GenerateResult(this);
	}

	@Override
	public void run() throws Exception {
		if (gr == null) {
			// SimplePrintLoggerToRemoveSoon.ilf("9889-0060 - gr is null in WritePipeline");
			SimplePrintLoggerToRemoveSoon.println_err("9889-0060 - gr is null in WritePipeline");
			return;
		}
		sys.generateOutputs(gr);

		write_files();
		// TODO flag?
		write_buffers();
	}

	public void write_files() throws IOException {
		final Multimap<String, Buffer> mb = ArrayListMultimap.create();

		for (final GenerateResultItem ab : gr.results()) {
			mb.put(ab.output, ab.buffer);
		}

		final Map<String, OS_Module> modmap = new HashMap<>();
		for (final GenerateResultItem ab : gr.results()) {
			modmap.put(ab.output, ab.node.module());
		}

		final _WriterPayload writerPayload = new _WriterPayload(mb, null);
		c.getOutputTree().addAll(writerPayload.getList(modmap::get));
		c.signal(
				new CSS2_Signal() {
					@Override
					public void trigger(final Compilation compilation, final Object payload) {
						assert payload == writerPayload;
						writerPayload.getActivator().resolve(Ok.instance());
					}
				},
				writerPayload
		);
	}

	public void write_buffers() throws FileNotFoundException {
		final File        file      = new File(choose_dir_name1(), "buffers.txt");
		final PrintStream db_stream = new PrintStream(file);
		PipelineLogic.debug_buffers(gr, db_stream);
		V.gri(gr);
	}

	private @NotNull File choose_dir_name() {
		final File fn00 = choose_dir_name1();
		final File fn01 = new File(fn00, "code");
		return fn01;
	}

	private @NotNull File choose_dir_name1() {
		final File fn00 = new F203(c.getErrSink(), c).chooseDirectory();
		return fn00;
	}

	private void __rest(
			final @NotNull Multimap<String, Buffer> mb,
			final @NotNull File aFile_prefix,
			final List<EOT_OutputFile> leof)
			throws IOException {
		aFile_prefix.mkdirs();
		final String prefix = aFile_prefix.toString();

		// TODO flag?
		write_inputs(aFile_prefix);

		for (final Map.Entry<String, Collection<Buffer>> entry : mb.asMap().entrySet()) {
			final String key  = entry.getKey();
			final Path   path = FileSystems.getDefault().getPath(prefix, key);
			//			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

			path.getParent().toFile().mkdirs();

			// TODO functionality
			if (true) {
				System.out.println("201 Writing path: " + path);
			}
			c.reports().add401b(new Finally._401bSpec() {
			});
			final CharSink x = c.getIO().openWrite(path);

			final EG_SingleStatement beginning =
					new EG_SingleStatement("", EX_Explanation.withMessage("WritePipeline" + ".beginning"));
			final EG_Statement middle = new GE_BuffersStatement(entry);
			final EG_SingleStatement ending =
					new EG_SingleStatement("", EX_Explanation.withMessage("WritePipeline.ending"));
			final EX_Explanation explanation = EX_Explanation.withMessage("write output file");

			final EG_CompoundStatement seq = new EG_CompoundStatement(beginning, ending, middle, false, explanation);

			//			for (final @NotNull Buffer buffer : entry.getValue()) {
			//				x.accept(buffer.getText());
			//			}
			x.accept(seq.getText());
			((FileCharSink) x).close();

			final @NotNull EOT_OutputTree cot = c.getOutputTree();
			cot._putSeq(key, path, seq);
		}
	}

	private void write_inputs(final File file_prefix) throws IOException {
		final DefaultBuffer buf = new DefaultBuffer("");
		//			FileBackedBuffer buf = new FileBackedBuffer(fn1);
		//			for (OS_Module module : modules) {
		//				final String fn = module.getFileName();
		//
		//				append_hash(buf, fn);
		//			}
		//
		//			for (CompilerInstructions ci : cis) {
		//				final String fn = ci.getFilename();
		//
		//				append_hash(buf, fn);
		//			}

		final List<File> recordedreads = c.getIO().recordedreads;
		final List<String> recordedread_filenames =
				recordedreads.stream().map(file -> file.toString()).collect(Collectors.toList());

		for (final @NotNull File file : recordedreads) {
			final String fn = file.toString();

			append_hash(buf, fn, c.getErrSink());
		}

		final File   fn1 = new File(file_prefix, "inputs.txt");
		final String s   = buf.getText();
		try (final Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn1, true)))) {
			w.write(s);
		}
	}

	private void append_hash(final TextBuffer aBuf, final String aFilename, final ErrSink errSink) throws IOException {
		@Nullable final String hh = Helpers.getHashForFilename(aFilename, errSink);
		if (hh != null) {
			aBuf.append(hh);
			aBuf.append(" ");
			aBuf.append_ln(aFilename);
		}
	}

	@Override
	public void gr_slot(final GenerateResult gr) {
		this.gr = gr;
	}

	public Consumer<Supplier<GenerateResult>> consumer() {
		return new Consumer<Supplier<GenerateResult>>() {
			@Override
			public void accept(final Supplier<GenerateResult> aGenerateResultSupplier) {
				//				if (grs != null) {
				//					tripleo.elijah.util.Stupidity.println_err2("234 grs not null "+grs.getClass().getName());
				//					return;
				//				}
				//
				//				assert false;
				//				grs = aGenerateResultSupplier;
				//				//final GenerateResult gr = aGenerateResultSupplier.get();
				final int y = 2;
			}
		};
	}

	public class _WriterPayload {
		private final Multimap<String, Buffer> _g_mb;
		private final List<EOT_OutputFile>     _g_leof;
		private final Eventual<Ok>             activatorPromise = new Eventual<>();
		private final Eventual<Ok>             resultPromise    = new Eventual<>();

		public _WriterPayload(final Multimap<String, Buffer> aMb, final List<EOT_OutputFile> aLeof) {
			_g_mb   = aMb;
			_g_leof = aLeof;

			activatorPromise.then(Sok -> action());
			activatorPromise.onFail(Sfail -> {
				// FIXME Don't even know if this is possible
				c.getErrSink().reportError("Activation of WritePipeline ignored.");
			});
		}

		public Eventual<Ok> getActivator() {
			return activatorPromise; // technically latch, also resettable?? (see activej...)
		}

		public Eventual<Ok> getResult() {
			return resultPromise;
		}

		private void action() {
			final File fn1 = choose_dir_name();
			try {
				__rest(_g_mb, fn1, _g_leof);
			} catch (final IOException aE) {
				throw new RuntimeException(aE);
			}
		}

		public List<EOT_OutputFile> getList(final Function<String, OS_Module> q) {
			final List<EOT_OutputFile> leof = new ArrayList<>();

			for (final String s : _g_mb.keySet()) {
				final Collection<Buffer> vs = _g_mb.get(s);

				final EOT_OutputFile eof = EOT_OutputFile.bufferSetToOutputFile(s, vs, c, q.apply(s));
				leof.add(eof);
			}

			return leof;
		}
	}
}

//
//
//
