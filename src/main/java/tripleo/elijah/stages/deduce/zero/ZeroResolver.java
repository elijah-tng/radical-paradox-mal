package tripleo.elijah.stages.deduce.zero;

import tripleo.elijah.lang.OS_Type;
import tripleo.elijah.stages.deduce.DeduceTypes2;
import tripleo.elijah.stages.deduce.ResolveError;
import tripleo.elijah.stages.gen_fn.GenType;
import tripleo.elijah_fluffy.util.NotImplementedException;

class ZeroResolver {

    private DeduceTypes2 deduceTypes2;

    public Zero_Type resolve_type(final OS_Type ty) {
        try {
            final GenType gt = deduceTypes2.resolve_type(ty, ty.getTypeName().getContext());
            return new Zero_Type(gt);
        } catch (final ResolveError aE) {
            NotImplementedException.raise();
            return null;
        }
    }
}
