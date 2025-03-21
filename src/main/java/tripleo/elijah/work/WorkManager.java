package tripleo.elijah.work;

public interface WorkManager {
	void addJobs(WorkList aList);

	void drain();
}
