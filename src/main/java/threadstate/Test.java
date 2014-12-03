package threadstate;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

	public static void main(String[] args) {
		/*
		 * 名称: Thread-0
状态: TIMED_WAITING
总阻止数: 0, 总等待数: 1

堆栈跟踪: 
java.lang.Thread.sleep(Native Method)
threadstate.T.run(Test.java:27)
		 */
		
		/*
		 * 名称: Thread-1
状态: java.util.concurrent.locks.ReentrantLock$NonfairSync@7c8b3bd8上的WAITING, 拥有者: Thread-0
总阻止数: 0, 总等待数: 1

堆栈跟踪: 
sun.misc.Unsafe.park(Native Method)
java.util.concurrent.locks.LockSupport.park(LockSupport.java:186)
java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(AbstractQueuedSynchronizer.java:834)
java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(AbstractQueuedSynchronizer.java:867)
java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1197)
java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(ReentrantLock.java:214)
java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:290)
threadstate.T.run(Test.java:24)

		 */
		T t1 = new T();
		t1.start();

		T t2 = new T();
		t2.start();
	}

}

class T extends Thread {

	static Lock lock = new ReentrantLock();

	@Override
	public void run() {
		lock.lock();
		System.out.println("Get the lock:" + this.getName());
		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
