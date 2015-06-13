package orderedthreadexecutor;

public interface RunnableWithKey extends Runnable{
	Object getKey();
}
