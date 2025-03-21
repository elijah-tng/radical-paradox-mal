package tripleo.elijah.stages.deduce.fluffy.i;

import tripleo.elijah.nextgen.composable.IComposable;
import tripleo.elijah_fluffy.diagnostic.ElLocatable;

public interface FluffyVar {
    String name();

    ElLocatable nameLocatable();

    IComposable nameComposable();

    FluffyVarTarget target();
}
