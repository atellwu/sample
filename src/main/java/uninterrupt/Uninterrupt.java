package uninterrupt;

import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.FutureCallback;

// awaitUninterruptibly是可以被kill <pid>以及 kill -9 <pid>的，这和操作系统进程真正的不可中断状态不一样。
// addShutdownHook是在kill <pid>触发的，kill -3只是触发打印线程和堆状态。
public class Uninterrupt {

	public static void main(String[] args) {
		ReentrantLock lock = new ReentrantLock();
		
		Condition condition = lock.newCondition();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public void run() {
				System.out.println("----------");
			}
			
		});
		
		lock.lock();
		condition.awaitUninterruptibly();
		
	}

}
