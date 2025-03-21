package tripleo.elijah_fluffy.comp;

import org.jetbrains.annotations.NotNull;
import tripleo.elijah.ci.LibraryStatementPart;
import tripleo.elijah.comp.Compilation;
import tripleo.elijah.lang.OS_Module;

public interface CM_Prelude {
    @NotNull OS_Module getModule();

    @NotNull CM_Preludes getTag();

    @NotNull LibraryStatementPart getLsp();

    @NotNull Compilation getCompilation();
}
