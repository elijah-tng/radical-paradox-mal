/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.comp.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.ci.LibraryStatementPart;
import tripleo.elijah.comp.*;
import tripleo.elijah.comp.functionality.f202.F202;
import tripleo.elijah.comp.i.CompilerController;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.comp.i.ICompilationAccess;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.lang.OS_Package;
import tripleo.elijah.lang.Qualident;
import tripleo.elijah.nextgen.outputtree.EOT_OutputTree;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.deduce.FunctionMapHook;
import tripleo.elijah.stages.deduce.fluffy.i.FluffyComp;
import tripleo.elijah.stages.deduce.fluffy.impl.FluffyCompImpl;
import tripleo.elijah.stages.logging.ElLog;
import tripleo.elijah.testing.comp.IFunctionMapHook;
import tripleo.elijah.util.Operation2;
import tripleo.elijah.world.i.LivingRepo;
import tripleo.elijah.world.impl.DefaultLivingRepo;
import tripleo.elijah_fluffy.comp.CM_Prelude;
import tripleo.elijah_fluffy.comp.CM_Preludes;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ExceptionDiagnostic;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.EventualExtract;
import tripleo.elijah_fluffy.util.NotImplementedException;
import tripleo.elijah_prolific.comp_signals.CSS2_Signal;
import tripleo.elijah_remnant.startup.ProlificStartup2;
import tripleo.elijah_remnant.value.ElValue;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EDR_Compilation implements Compilation {
	private final @NotNull FluffyCompImpl               _fluffyComp;
	private final          ProlificStartup2             _startup;
	private final @NotNull EOT_OutputTree               _output_tree;
	private final          List<ElLog>                  elLogs;
	private final          Eventual<File>               _m_comp_dir_promise;
	private final          CompilationConfig            cfg;
	private final          Finally                      _f;
	private final          DefaultLivingRepo            _repo;
	private final          EDR_CIS                      _cis;
	private final          EDR_MOD                      mod;
	private final          EDR_USE                      use;
	private final          CompilationBusElValue        __cb;
	private final          Pipeline                     pipelines;
	private final          int                          _compilationNumber;
	private final          ErrSink                      errSink;
	private final          IO                           io;
	private final List<ExecutorService__> es;
	private                PipelineLogic                pipelineLogic;
	private                CompilerInstructionsObserver _cio;
	private                EDR_CompilationRunner        __cr;
	private                CompilerInstructions         rootCI;

	public EDR_Compilation(final @NotNull ErrSink aErrSink, final IO aIO) {
		errSink            = aErrSink;
		io                 = aIO;
		_compilationNumber = new Random().nextInt(Integer.MAX_VALUE);
		es                 = new ArrayList<ExecutorService__>();
		elLogs              = new LinkedList<>();
		_m_comp_dir_promise = new Eventual<>();
		pipelines          = new Pipeline(aErrSink);
		__cb               = new CompilationBusElValue();
		cfg                = new CompilationConfig();
		_f                 = new Finally();
		_repo              = new DefaultLivingRepo();
		_cis               = new EDR_CIS();
		mod                = new EDR_MOD();
		use                = new EDR_USE(this);
		_fluffyComp        = new FluffyCompImpl(this);
		_startup           = new ProlificStartup2(this);
		_output_tree       = new EOT_OutputTree();
	}

	@Override
	public void testMapHooks(final List<IFunctionMapHook> aMapHooks) {
		throw new NotImplementedException();
	}

	@Override
	public ProlificStartup2 getStartup() {
		return _startup;
	}

	@Override
	public ElValue<EDR_CompilationBus> get_cb() {
		return __cb;
	}

	@Override
	public ICompilationAccess _access() {
		final Eventual<ICompilationAccess> cap = _startup.getCompilationAccess();
		return EventualExtract.of(cap);
	}

	@Override
	public void feedCmdLine(final @NotNull List<String> args) {
		try {
			feedCmdLine(args, new EDR_CompilerController());
		} finally {
			final int y = 2;
		}
	}

	@Override
	public void feedCmdLine(final @NotNull List<String> aStringList, final CompilerController ctl) {
		if (aStringList.isEmpty()) {
			ctl.printUsage();
		} else {
			__cb.set((EDR_CompilationBus) ctl.getCB());
			final var launcher = new ProlificCompilationLauncher(this, aStringList, ctl);
			launcher.launch0();
		}
	}

	@Override
	public String getProjectName() {
		return rootCI.getName();
	}

	@Override
	public CM_Module realParseElijjahFile(final String f, final @NotNull File file, final boolean do_out) {
		final CM_Module res = new CM_Module();
		res.advise(this, use);
		res.advise(f, file, do_out);
		res.action();
		final Operation<OS_Module> osModuleOperation = res.getOperation();
		return res;
	}

	@Override
	public void pushItem(final CompilerInstructions aci) {
		_cis.onNext(aci);
	}

	@Override
	public List<ClassStatement> findClass(final String string) {
		final List<ClassStatement> l = new ArrayList<>();
		for (final OS_Module module : mod.modules) {
			if (module.hasClass(string)) {
				l.add((ClassStatement) module.findClass(string));
			}
		}
		return l;
	}

	@Override
	public void use(final @NotNull CompilerInstructions compilerInstructions, final boolean do_out) throws Exception {
		use.use(compilerInstructions, do_out); // NOTE Rust
	}

	@Override
	public int errorCount() {
		return errSink.errorCount();
	}

	@Override
	public void writeLogs(final @NotNull List<ElLog> aLogs) {
		final Multimap<String, ElLog> logMap = ArrayListMultimap.create();
		for (final ElLog deduceLog : aLogs) {
			logMap.put(deduceLog.getFileName(), deduceLog);
		}
		for (final Map.Entry<String, Collection<ElLog>> stringCollectionEntry : logMap.asMap().entrySet()) {
			final F202 f202 = new F202(getErrSink(), this);
			f202.processLogs(stringCollectionEntry.getValue());
		}
	}

	@Override
	public ErrSink getErrSink() {
		return errSink;
	}

	@Override
	public IO getIO() {
		return io;
	}

	@Override
	public void addModule(final OS_Module module, final String fn) {
		mod.addModule(module, fn);
	}

	@Override
	public OS_Module fileNameToModule(final String fileName) {
		if (mod.containsKey(fileName)) {
			return mod.get(fileName);
		}
		return null;
	}

	@Override
	public boolean getSilence() {
		return cfg.silent;
	}

	@Override
	public Operation2<OS_Module> findPrelude(final String prelude_name) {
		return use.findPrelude(prelude_name);
	}

	@Override
	public void addFunctionMapHook(final FunctionMapHook aFunctionMapHook) {
		getDeducePhase().addFunctionMapHook(aFunctionMapHook);
	}

	@Override
	public @NotNull DeducePhase getDeducePhase() {
		// TODO subscribeDeducePhase??
		return pipelineLogic.getDp();
	}

	@Override
	public int nextClassCode() {
		return _repo.nextClassCode();
	}

	@Override
	public int nextFunctionCode() {
		return _repo.nextFunctionCode();
	}

	@Override
	public OS_Package getPackage(final @NotNull Qualident pkg_name) {
		return _repo.getPackage(pkg_name.toString());
	}

	@Override
	public OS_Package makePackage(final Qualident pkg_name) {
		return _repo.makePackage(pkg_name);
	}

	@Override
	public int compilationNumber() {
		return _compilationNumber;
	}

	@Override
	public String getCompilationNumberString() {
		return String.format("%08x", _compilationNumber);
	}

	@Deprecated
	@Override
	public int modules_size() {
		return mod.size();
	}

	@Override
	public @NotNull EOT_OutputTree getOutputTree() {
		return _output_tree;
	}

	@Override
	public @NotNull FluffyComp getFluffy() {
		return _fluffyComp;
	}

	@Override
	public boolean isPackage(final String aPackageName) {
		return _repo.isPackage(aPackageName);
	}

	@Override
	public Pipeline getPipelines() {
		return pipelines;
	}

	@Override
	public ModuleBuilder moduleBuilder() {
		return new ModuleBuilder(this);
	}

	@Override
	public Finally reports() {
		return _f;
	}

	@Override
	public void signal(@NotNull final CSS2_Signal signal, final Object payload) {
		signal.trigger(this, payload);
	}

	@Override
	public void register(final Object registerable) {
		if (registerable instanceof final EDR_CompilationRunner cr) {
			__cr = cr;
		}
	}

	@Override
	public LivingRepo world() {
		return _repo;
	}

	@Override
	public Operation<CM_Prelude> findPrelude2(final @NotNull CM_Preludes aPreludeTag) {
		final var _c = this;
		final CM_Prelude result = new CM_Prelude() {
			private Operation2<OS_Module> x;

			@Override
			public @NotNull OS_Module getModule() {
				obtain();
				assert x != null;
				switch (x.mode()) {
					case SUCCESS -> {
						return x.success();
					}
					case FAILURE, NOTHING -> throw new RuntimeException("");
				}

				// wtf??
				// throw new NeverReached();
				return null;
			}

			private void obtain() {
				if (x == null) {
					x = findPrelude(aPreludeTag.getName());
				}
			}

			@Override
			public @NotNull CM_Preludes getTag() {
				return aPreludeTag;
			}

			@Override
			public @NotNull LibraryStatementPart getLsp() {
				return null;
			}

			@Override
			public @NotNull Compilation getCompilation() {
				return _c;
			}
		};
		return Operation.success(result);
	}

	@Override
	public Eventual<File> comp_dir_promise() {
		return _m_comp_dir_promise;
	}

	@Override
	public ICompilationAccess _compilationAccess() {
		final Eventual<ICompilationAccess> e = getStartup().getCompilationAccess();
		return EventualExtract.of(e);
	}

	@Override
	public List<ElLog> getElLogs() {
		return elLogs;
	}

	@Override
	public CompilationConfig getCfg() {
		return cfg;
	}

	@Override
	public EDR_CIS get_cis() {
		return _cis;
	}

	@Override
	public DefaultLivingRepo get_repo() {
		return _repo;
	}

	@Override
	public EDR_MOD getMod() {
		return mod;
	}

	@Override
	public PipelineLogic getPipelineLogic() {
		return pipelineLogic;
	}

	@Override
	public void setPipelineLogic(final PipelineLogic aPipelineLogic) {
		pipelineLogic = aPipelineLogic;
	}

	@Override
	public CompilerInstructionsObserver get_cio() {
		return _cio;
	}

	@Override
	public void set_cio(final CompilerInstructionsObserver a_cio) {
		_cio = a_cio;
	}

	@Override
	public EDR_CompilationRunner get__cr() {
		return __cr;
	}

	@Override
	public void hasInstructions(final @NotNull List<CompilerInstructions> cis) {
		assert !cis.isEmpty();

		rootCI = cis.get(0);

		__cr.start(rootCI, cfg.do_out);
	}

	@Override
	public void pushItem(final @NotNull OptCompilerInstructions aOptCI) {
		if (aOptCI.ci() != null) {
			pushItem(aOptCI.ci());
		} else if (aOptCI.diag() != null) {
			// pushItem();
			final ElDiagnostic diag = aOptCI.diag();
			if (diag instanceof final ExceptionDiagnostic ed) {
				final Object o = ed.get();
				if (o instanceof final Throwable r) {
					getErrSink().exception(r);
				}
			}
		} else {
			assert false;
		}
	}

	@Override
	public ExecutorService_ newExecutor() {
		final var e = new ExecutorService__();
		es.add(e);
		return e;
	}

	public interface ExecutorService_ {
		void submit(Runnable aRunnable);
	}

	public class ExecutorService__ implements ExecutorService_ {
		private final ExecutorService es;

		{
			es = Executors.newCachedThreadPool();
		}

		@Override
		public void submit(final Runnable aRunnable) {
			es.submit(aRunnable);
		}
	}
}

//
//
//
