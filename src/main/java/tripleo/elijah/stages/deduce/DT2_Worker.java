package tripleo.elijah.stages.deduce;

import tripleo.elijah_fluffy.util.Eventual;

public class DT2_Worker {
    private final Eventual<DeduceTypes2> trigger = new Eventual<>("DT2_Worker::trigger");

    public void addWork(final DT2_Work job) {
        trigger.then(SdeduceTypes2 -> job.run(SdeduceTypes2, DT2_Worker.this));
    }

    public void asvErr(final int code, final boolean aB) {
        trigger.then(SdeduceTypes2 -> SdeduceTypes2.asvErr(code, aB));
    }

    public interface DT2_Work {
        void run(DeduceTypes2 dt2, DT2_Worker worker);
    }
}
