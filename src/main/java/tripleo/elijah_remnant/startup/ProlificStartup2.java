package tripleo.elijah_remnant.startup;

import org.jdeferred2.DoneCallback;
import tripleo.elijah.comp.AccessBus;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.comp.PipelineLogic;
import tripleo.elijah.comp.i.CR_State;
import tripleo.elijah.comp.i.ICompilationAccess;
import tripleo.elijah.comp.i.ICompilationBus;
import tripleo.elijah.comp.i.ICompilationRunner;
import tripleo.elijah.comp.internal.EDR_CompilationAccess;
import tripleo.elijah.comp.internal.EDR_CompilationRunner;
import tripleo.elijah.comp.internal.EDR_ProcessRecord;
import tripleo.elijah.stages.generate.ElSystem;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.elijah_fluffy.util.EventualExtract;

public class ProlificStartup2 {
	private final Compilation                  _compilation;
	private final Eventual<EDR_ProcessRecord>  _p_ProcessRecord     = new Eventual<>();
	private final Eventual<ICompilationAccess> _p_CompilationAccess = new Eventual<>();
	private final Eventual<ICompilationRunner> _p_CompilationRunner = new Eventual<>();
	private final Eventual<ICompilationBus>    _p_CompilationBus    = new Eventual<>();
	private final Eventual<PipelineLogic>      _p_PipelineLogic     = new Eventual<>();
	private final Eventual<ElSystem>           _p_ElSystem          = new Eventual<>();
	private final Eventual<AccessBus>          _p_AccessBus         = new Eventual<>();
	private final Eventual<CR_State> _p_CR_State = new Eventual<>();
	@SuppressWarnings("FieldCanBeLocal")
	private       EDR_ProcessRecord            __processRecord;
	private       boolean                      _f_processRecord;
	@SuppressWarnings("FieldCanBeLocal")
	private       EDR_CompilationAccess        __compilationAccess;
	private       boolean                      _f_CompilationAccess;
	@SuppressWarnings("FieldCanBeLocal")
	private       ICompilationRunner           __CompilationRunner;
	private       boolean                      _f_CompilationRunner;
	@SuppressWarnings("FieldCanBeLocal")
	private       ICompilationBus              __CompilationBus;
	private       boolean                      _f_CompilationBus;
	@SuppressWarnings("FieldCanBeLocal")
	private       PipelineLogic                __PipelineLogic;
	private       boolean            _f_PipelineLogic;

	public ProlificStartup2(final Compilation aCompilation) {
		_compilation = aCompilation;
	}

	public Eventual<ICompilationAccess> getCompilationAccess() {
		if (!_f_CompilationAccess) {
			_f_CompilationAccess = true;
			__compilationAccess  = new EDR_CompilationAccess(getCompilation(), this);
			_p_CompilationAccess.resolve(__compilationAccess);
		}
		return _p_CompilationAccess;
	}

	private Compilation getCompilation() {
		return _compilation;
	}

	public Eventual<EDR_ProcessRecord> getProcessRecord() {
		if (!_f_processRecord) {
			_f_processRecord = true;
			__processRecord  = new EDR_ProcessRecord(__compilationAccess);
			_p_ProcessRecord.resolve(__processRecord);
		}
		return _p_ProcessRecord;
	}

	public Eventual<ICompilationRunner> getCompilationRunner() {
		if (!_f_CompilationRunner) {
			_f_CompilationRunner = true;
			__CompilationRunner  = new EDR_CompilationRunner(_compilation, EventualExtract.of(getCompilationBus()));
			_p_CompilationRunner.resolve(__CompilationRunner);
		}
		return _p_CompilationRunner;
	}

	public Eventual<ICompilationBus> getCompilationBus() {
		// Let ControllerBase resolve this
		/*
		 * if (!_f_CompilationBus) { _f_CompilationBus = true; __CompilationBus = new
		 * CompilationBus(_compilation); _p_CompilationBus.resolve(__CompilationBus); }
		 */
		_p_CompilationBus.then(new DoneCallback<ICompilationBus>() {
			@Override
			public void onDone(final ICompilationBus result) {
				_f_CompilationBus = true;
				__CompilationBus  = result;
			}
		});
		return _p_CompilationBus;
	}

	public Eventual<PipelineLogic> getPipelineLogic() {
		/*
		 * if (!_f_PipelineLogic) { _f_PipelineLogic = true; __PipelineLogic = null;
		 * _p_PipelineLogic.resolve(__PipelineLogic); }
		 */
		return _p_PipelineLogic;
	}

	public void resolvePipelineLogic(final PipelineLogic aX) {
		__PipelineLogic = aX;
		_p_PipelineLogic.resolve(__PipelineLogic);
	}

	public Eventual<ElSystem> getSystem() {
		return _p_ElSystem;
	}

	public Eventual<AccessBus> getAccessBus() {
		return _p_AccessBus;
	}

	public Eventual<CR_State> getCrState() {
		return _p_CR_State;
	}

	public CR_State getCrStateEx() {
		final CR_State           st1;
		final Eventual<CR_State> crP = getCrState();
		if (crP.isPending()) {
			st1 = new EDR_CompilationRunner.EDR_CR_State(this);
			crP.resolve(st1);
		} else {
			st1 = EventualExtract.of(crP);
		}
		return st1;
	}
}
