package queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CircularFifoQueue is a first-in first-out queue with a fixed size that
 * replaces its oldest element if full.
 * <p>
 * The removal order of a {@link CircularFifoQueue} is based on the insertion
 * order; elements are removed in the same order in which they were added.
 * <p>
 * The {@link #put(Object)} operations all perform in constant time and always
 * success because the queue is circular.
 * <p>
 * <p>
 * The {@link #take()} operations will block if the queue is empty
 * <p>
 *
 */
/**
 * <pre>
 * 唉 写的不对 ArrayBlockingQueue都只能用一个大lock 我想实现remove
 * oldest，比ArrayBlockingQueue复杂，更加要用一个大lock 想用2个lock 会遇到种种问题 Goug Lee真牛
 * </pre>
 */
public class CircularFifoQueue<E> {

	private E[] elements;

	private AtomicInteger putIndex = new AtomicInteger(0);

	private AtomicInteger takeIndex = new AtomicInteger(0);

	private final ReentrantLock takeLock = new ReentrantLock();

	private final Condition notEmpty = takeLock.newCondition();

	private final ReentrantLock putLock = new ReentrantLock();

	public CircularFifoQueue() {
		this(32);
	}

	@SuppressWarnings("unchecked")
	public CircularFifoQueue(final int size) {
		if (size <= 0) {
			throw new IllegalArgumentException(
					"The size must be greater than 0");
		}
		elements = (E[]) new Object[size + 1];// 比实际容量多1。putIndex在taskIndex前面一个时，就算full。
	}

	public void put(E e) throws InterruptedException {
		final ReentrantLock putLock = this.putLock;
		putLock.lockInterruptibly();
		boolean isEmpty = false;
		try {
			int tIndex = takeIndex.get();
			if (tIndex == index(putIndex.get() + 1)) {// if full，takeIndex++
				takeIndex.compareAndSet(tIndex, index(tIndex + 1));
			}
			// not full, put element
			elements[index(putIndex.get())] = e;

			if (isEmpty()) {// empty
				isEmpty = true;
			}
			putIndex.incrementAndGet();
		} finally {
			putLock.unlock();
		}
		if (isEmpty) {
			signalNotEmpty();
		}
	}

	public boolean isEmpty() {
		return takeIndex.get() == putIndex.get();
	}

	public E take() throws InterruptedException {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lockInterruptibly();
		try {
			while (isEmpty()) {// empty
				notEmpty.await();
			}
			E ele = elements[index(takeIndex.getAndIncrement())];

			if (!isEmpty()) {// not empty
				signalNotEmpty();
			}

			return ele;
		} finally {
			takeLock.unlock();
		}
	}

	private int index(int idx) {
		return idx % elements.length;
	}

	private void signalNotEmpty() {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
	}

	public int size() {
		return (putIndex.get() - takeIndex.get() + elements.length)
				% elements.length;
	}

}