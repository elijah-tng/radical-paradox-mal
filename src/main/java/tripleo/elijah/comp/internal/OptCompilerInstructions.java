package tripleo.elijah.comp.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tripleo.elijah.ci.CompilerInstructions;
import tripleo.elijah.comp.Operation;
import tripleo.elijah_fluffy.diagnostic.ElDiagnostic;
import tripleo.elijah_fluffy.diagnostic.ExceptionDiagnostic;

import java.util.HashMap;
import java.util.Map;

public record OptCompilerInstructions(
		@Nullable CompilerInstructions ci,
		@Nullable ElDiagnostic diag,
		@Nullable Map<String, Object> rider,
		@Nullable Object up
) {
	public static OptCompilerInstructions of(final @NotNull Operation<CompilerInstructions> oci, final CiReasoning aReasoning) {
		final OptCompilerInstructions res;
		final Map<String, Object>     rider = new HashMap<>();
		if (aReasoning != null) {
			rider.put("reasoning", aReasoning);
		}
		switch (oci.mode()) {
			case SUCCESS -> {
				res = new OptCompilerInstructions(oci.success(), null, rider, null);
			}
			case FAILURE -> {
				res = new OptCompilerInstructions(null, new ExceptionDiagnostic(oci.failure()), rider, null);
			}
			default -> throw new IllegalStateException("Unexpected value: " + oci.mode());
		}
		assert res != null;
		return res;
	}

	public Operation<CompilerInstructions> asOperation() {
		if (ci != null) {
			return Operation.success(ci);
		}
		assert diag != null;
		if (diag != null) {
			return Operation.failure((Exception) diag.get());
		}
		throw new IllegalStateException();
	}
}
