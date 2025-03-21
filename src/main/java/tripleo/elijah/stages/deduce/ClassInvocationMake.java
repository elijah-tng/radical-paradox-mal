package tripleo.elijah.stages.deduce;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.comp.i.ErrSink;
import tripleo.elijah.lang.ClassStatement;
import tripleo.elijah.lang.NormalTypeName;
import tripleo.elijah.lang.TypeName;
import tripleo.elijah.lang.TypeNameList;
import tripleo.elijah.lang.types.OS_UserType;
import tripleo.elijah.stages.gen_fn.GenType;
import tripleo.elijah_durable_prolific.deduce.DCC;

import java.util.List;

public class ClassInvocationMake {
	public static @NotNull ClassInvocation withGenericPart(final ClassStatement best,
	                                                       final String constructorName,
	                                                       final NormalTypeName aTyn1,
	                                                       final @NotNull DCC dcc) {
		final DeduceTypes2                      dt2         = dcc.deduceTypes2();
		final ErrSink                           aErrSink    = dcc.deduceTypes2()._errSink();
		final @NotNull DeduceTypes2.GenericPart genericPart = new DeduceTypes2.GenericPart(best, aTyn1);
		final @Nullable ClassInvocation         clsinv      = new ClassInvocation(best, constructorName);

		if (genericPart.hasGenericPart()) {
			final @NotNull List<TypeName> gp  = best.getGenericPart();
			final @NotNull TypeNameList   gp2 = genericPart.getGenericPartFromTypeName();

			for (int i = 0; i < gp.size(); i++) {
				final TypeName         typeName = gp2.get(i);
				@NotNull final GenType typeName2;
				try {
					typeName2 = dt2.resolve_type(new OS_UserType(typeName), typeName.getContext());
					// TODO transition to GenType
					clsinv.set(i, gp.get(i), typeName2.getResolved());
				} catch (final ResolveError aResolveError) {
					//						aResolveError.printStackTrace();
					aErrSink.reportDiagnostic(aResolveError);
				}
			}
		}
		return clsinv;
	}
}
