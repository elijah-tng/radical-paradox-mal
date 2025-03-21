package tripleo.elijah_remnant.rosetta;

import org.jdeferred2.DoneCallback;
import org.jetbrains.annotations.NotNull;
import tripleo.elijah.lang.OS_Module;
import tripleo.elijah.stages.deduce.DeducePhase;
import tripleo.elijah.stages.deduce.DeduceTypes2;
import tripleo.elijah_fluffy.util.Eventual;
import tripleo.vendor.org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class FakeRosetta3 {
    @SuppressWarnings("UnusedReturnValue")
    public static @NotNull Eventual<DeduceTypes2> mkDeduceTypes2(
            final OS_Module mod, final DeducePhase phase, final DoneCallback<? super DeduceTypes2> cb) {
        FakeRosetta3$.called.add(Pair.of(cb, false));

        final Eventual<DeduceTypes2> e = _getDeduceTypes2Eventual(mod, phase);
        e.then(new DoneCallback<DeduceTypes2>() {
            @Override
            public void onDone(final DeduceTypes2 result) {
                boolean f = false;

                for (final Pair<DoneCallback<?>, Boolean> p : FakeRosetta3$.called) {
                    if (p.getLeft() == result) {
                        p.setValue(true);
                        f = true;
                    }
                }

                if (!f) {
                    throw new AssertionError("Internal Logic Error");
                }

                cb.onDone(result);
            }
        });
        return e;
    }

    private static @NotNull Eventual<DeduceTypes2> _getDeduceTypes2Eventual(
            final OS_Module mod, final DeducePhase phase) {
        final DeduceTypes2 d = new DeduceTypes2(mod, phase);
        final Eventual<DeduceTypes2> e = new Eventual<>();
        e.resolve(d);
        return e;
    }

    public static void ensure_all() {
        for (final var p : FakeRosetta3$.called) {
            if (!p.getRight()) {
                System.err.println("~~ Didn't call: " + p.getLeft());
            }
        }
    }

    private static class FakeRosetta3$ {
        // TODO prol doesnt need to be pcoll?
        private static final List<Pair<DoneCallback<?>, Boolean>> called = new ArrayList<>();

        private static final FakeRosetta3 INSTANCE = new FakeRosetta3();
    }
}
