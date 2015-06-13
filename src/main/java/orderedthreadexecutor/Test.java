package orderedthreadexecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Test {

	public static void main(String[] args) {
		OrderedByKeyThreadExecutor executor = new OrderedByKeyThreadExecutor(5,
				10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		Runnable taskA = new Task("A","key",3);
		Runnable taskB = new Task("B","key",2);
		Runnable taskC = new Task("C","key",4);
		Runnable taskD = new Task("D","key2",2);
		Runnable taskE = new Task("E","key2",1);
		executor.execute(taskA);
		executor.execute(taskB);
		executor.execute(taskC);
		executor.execute(taskD);
		executor.execute(taskE);

	}

	static class Task implements RunnableWithKey {
		private String name;
		private String key;
		private long timeCost;


		public Task(String name, String key, long timeCost) {
			super();
			this.name = name;
			this.key = key;
			this.timeCost = timeCost;
		}

		@Override
		public void run() {
			System.out.println(String.format("this is task[%s] start", name));
			
			try {
				Thread.sleep(timeCost*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println(String.format("this is task[%s] end", name));
		}

		@Override
		public Object getKey() {
			return key;
		}

	}

}
