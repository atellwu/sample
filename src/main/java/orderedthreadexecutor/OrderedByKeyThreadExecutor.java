package orderedthreadexecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * <pre>
 * netty有个OrderedMemoryAwareThreadPoolExecutor，在ExecutionHandler中可以使用，但它是不公平的。
 * 为了说明不公平，假设executor只用一个线程：
 * 比如事件发生的顺序是：     channel-1，channel-2，channel-1，channel-1
 * 最终执行的顺序很可能是： channel-1，channel-1，channel-1，channel-2
 * 不公平的缺点：channel-1的事件来的频繁的话 可能导致channel-2迟迟得不到执行
 * 因为它的实现里，相同channel追加时，可能会延长该channel的run的时间，和lock的不公平有类似。
 * 
 * 这里，实现同样的功能：相同key，串行执行。特点是：无锁化(除了map)，考虑了公平性（关键点在于，追加某个key的任务时，不会导致任务的run变长，
 * 而是在run之后间任务再次交给executor）。
 * 
 * Please be very careful of memory leak, You must call removeKey(Object key)
 * when the life cycle of the key ends
 * 
 * </pre>
 * 
 * @author atell
 *
 */
public class OrderedByKeyThreadExecutor extends ThreadPoolExecutor {

	final WrapRunnable END = new WrapRunnable(null);// next断开时赋值END
	private final AtomicReferenceFieldUpdater<WrapRunnable, WrapRunnable> nextUpdater = AtomicReferenceFieldUpdater
			.newUpdater(WrapRunnable.class, WrapRunnable.class, "next");

	// 和OrderedMemoryAwareThreadPoolExecutor一样，需要按key，链式存储任务
	ConcurrentHashMap<Object, WrapRunnable> map = new ConcurrentHashMap<Object, WrapRunnable>();

	public OrderedByKeyThreadExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public OrderedByKeyThreadExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				handler);
	}

	public OrderedByKeyThreadExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}

	public OrderedByKeyThreadExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
	}

	@Override
	public void execute(Runnable command) {
		if (command instanceof RunnableWithKey) {
			RunnableWithKey runnableWithKey = (RunnableWithKey) command;
			WrapRunnable wrapRunnable = new WrapRunnable(runnableWithKey);

			// 直接放到map里(后入的放在key上， 如果插入顺序是 A,B,C ，那么map里该key的情况是 key--->
			// C->B->A)
			//把队尾放在map里，每次建立next的链接，不需要遍历这条链， 拿key上的runnable.next指向即可
			
			//“链表”这块实现的对比：
			//1. netty的OrderedMemoryAwareThreadPoolExecutor使用putIfAbsent因为它一个key只需要一个ChildExecutor，ChildExecutor里才是一个LinkList
			//execute和run都会访问这个“链表”，netty在对LinkList，需要使用syncrnized
			//2. 而我的实现，在execute修改“链表”和run里面访问“链表”时，都不会出现问题，多亏map.put支持并发(原子)并且能返回旧值，另外对next的修改使用cas。

			WrapRunnable absentWrapRunnable = map.put(runnableWithKey.getKey(),
					wrapRunnable);

			// 1. 如果map已经存在相同key的对象，则尝试链接上它的next
			if (absentWrapRunnable != null) {
				// 能链接上next，就不管了：能连上，该task的执行，就交给链表上游的task去触发执行
				//不能链接上，则说明next已经为END，即该链已断，不会再被调用，那么需要重新调用
				if (!compareAndSetNext(absentWrapRunnable, null, wrapRunnable)) {
					super.execute(wrapRunnable);
				}
			} else {
				// 2. map本身不存在相同key，作为第一个，那么就需要执行
				super.execute(wrapRunnable);
			}
		} else {
			super.execute(command);
		}
	}

	/**
	 * call removeKey(Object key) when the life cycle of the key ends
	 */
	public boolean removeKey(Object key) {
		return map.remove(key) != null;
	}

	class WrapRunnable implements Runnable {

		private RunnableWithKey runnable;
		volatile WrapRunnable next;

		WrapRunnable(RunnableWithKey runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				this.runnable.run();
			} finally {
				if (!compareAndSetNext(this, null, END)) {// 如果next为null，再断开链(设置END)；否则执行next
					OrderedByKeyThreadExecutor.this.execute(next);
				} else {
					// next已经指向END，从map里删除自己，但不能remove掉别人
					// 实现起来很难不使用锁，所以不左，而是对外提供removeKey方法，这点OrderedMemoryAwareThreadPoolExecutor也是一样的。
				}
			}
		}
	}

	// 对于next字段，有2方面的线程在访问（executor提交，run），所以对next的读写，需要使用cas
	private boolean compareAndSetNext(WrapRunnable wrapRunnable,
			WrapRunnable expect, WrapRunnable update) {
		return nextUpdater.compareAndSet(wrapRunnable, expect, update);
	}

}
