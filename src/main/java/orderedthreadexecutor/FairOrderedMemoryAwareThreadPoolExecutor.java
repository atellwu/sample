/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package orderedthreadexecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.execution.ChannelEventRunnable;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.util.ObjectSizeEstimator;

public class FairOrderedMemoryAwareThreadPoolExecutor extends
		MemoryAwareThreadPoolExecutor {

	private final EventTask END = new EventTask(null);// next断开时赋值END

	private final AtomicReferenceFieldUpdater<EventTask, EventTask> fieldUpdater = AtomicReferenceFieldUpdater
			.newUpdater(EventTask.class, EventTask.class, "next");

	// 和OrderedMemoryAwareThreadPoolExecutor一样，需要按key，链式存储任务
	ConcurrentHashMap<Object, EventTask> map = new ConcurrentHashMap<Object, EventTask>();

	/**
	 * Creates a new instance.
	 *
	 * @param corePoolSize
	 *            the maximum number of active threads
	 * @param maxChannelMemorySize
	 *            the maximum total size of the queued events per channel.
	 *            Specify {@code 0} to disable.
	 * @param maxTotalMemorySize
	 *            the maximum total size of the queued events for this pool
	 *            Specify {@code 0} to disable.
	 */
	public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize,
			long maxChannelMemorySize, long maxTotalMemorySize) {
		super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param corePoolSize
	 *            the maximum number of active threads
	 * @param maxChannelMemorySize
	 *            the maximum total size of the queued events per channel.
	 *            Specify {@code 0} to disable.
	 * @param maxTotalMemorySize
	 *            the maximum total size of the queued events for this pool
	 *            Specify {@code 0} to disable.
	 * @param keepAliveTime
	 *            the amount of time for an inactive thread to shut itself down
	 * @param unit
	 *            the {@link TimeUnit} of {@code keepAliveTime}
	 */
	public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize,
			long maxChannelMemorySize, long maxTotalMemorySize,
			long keepAliveTime, TimeUnit unit) {
		super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize,
				keepAliveTime, unit);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param corePoolSize
	 *            the maximum number of active threads
	 * @param maxChannelMemorySize
	 *            the maximum total size of the queued events per channel.
	 *            Specify {@code 0} to disable.
	 * @param maxTotalMemorySize
	 *            the maximum total size of the queued events for this pool
	 *            Specify {@code 0} to disable.
	 * @param keepAliveTime
	 *            the amount of time for an inactive thread to shut itself down
	 * @param unit
	 *            the {@link TimeUnit} of {@code keepAliveTime}
	 * @param threadFactory
	 *            the {@link ThreadFactory} of this pool
	 */
	public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize,
			long maxChannelMemorySize, long maxTotalMemorySize,
			long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
		super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize,
				keepAliveTime, unit, threadFactory);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param corePoolSize
	 *            the maximum number of active threads
	 * @param maxChannelMemorySize
	 *            the maximum total size of the queued events per channel.
	 *            Specify {@code 0} to disable.
	 * @param maxTotalMemorySize
	 *            the maximum total size of the queued events for this pool
	 *            Specify {@code 0} to disable.
	 * @param keepAliveTime
	 *            the amount of time for an inactive thread to shut itself down
	 * @param unit
	 *            the {@link TimeUnit} of {@code keepAliveTime}
	 * @param threadFactory
	 *            the {@link ThreadFactory} of this pool
	 * @param objectSizeEstimator
	 *            the {@link ObjectSizeEstimator} of this pool
	 */
	public FairOrderedMemoryAwareThreadPoolExecutor(int corePoolSize,
			long maxChannelMemorySize, long maxTotalMemorySize,
			long keepAliveTime, TimeUnit unit,
			ObjectSizeEstimator objectSizeEstimator, ThreadFactory threadFactory) {
		super(corePoolSize, maxChannelMemorySize, maxTotalMemorySize,
				keepAliveTime, unit, objectSizeEstimator, threadFactory);
	}

	/**
	 * Executes the specified task concurrently while maintaining the event
	 * order.
	 */
	@Override
	protected void doExecute(Runnable task) {
		if (task instanceof ChannelEventRunnable) {
			ChannelEventRunnable eventRunnable = (ChannelEventRunnable) task;
			EventTask newEventTask = new EventTask(eventRunnable);

			// 直接放到map里(后入的放在key上， 如果插入顺序是 A,B,C ，那么map里该key的情况是 key--->
			// C->B->A)
			// 把队尾放在map里，每次建立next的链接，不需要遍历这条链， 拿key上的runnable.next指向即可

			// “链表”这块实现的对比：
			// 1.
			// netty的OrderedMemoryAwareThreadPoolExecutor使用putIfAbsent因为它一个key只需要一个EventTask，EventTask里才是一个LinkList
			// execute和run都会访问这个“链表”，netty在对LinkList，需要使用syncrnized
			// 2.
			// 而我的实现，在execute修改“链表”和run里面访问“链表”时，都不会出现问题，多亏map.put支持并发(原子)并且能返回旧值，另外对next的修改使用cas。

			// 该操作返回的是旧值，能保证，即使并发访问execute，也不会并发出现同一个absentEventTask对象，
			// 所以下方对它的next修改，只需考虑run里面可能有其他线程正在访问它的next，借助END对象和cas，即可处理

			/*
			 * e.g. Three event "A1","A2","A3" with the same key(keyA), are submitted in sequence, then map
			 * keyA's value is "A3",and "A3 -> A2 -> A1"
			 * Every channel(as a key) has its "EventTask's LinkedList"
			 */

			Object key = getKey(eventRunnable.getEvent());
			EventTask previousEventTask = map.put(key, newEventTask);

			if (previousEventTask != null) {
				// try to setup "previousEventTask -> newEventTask"
				// if success, then "newEventTask" will be executed after
				// "previousEventTask"
				// if failed (because the "previousEventTask.next" is already
				// END), then just trigger "newEventTask" to be executed
				if (!compareAndSetNext(previousEventTask, null, newEventTask)) {
					super.execute(newEventTask);
				}
			} else {
				// "newEventTask" is the header of "EventTask's LinkedList"
				// so just execute it
				super.execute(newEventTask);
			}

			// Remove the entry when the channel closes.
			ChannelEvent event = eventRunnable.getEvent();
			if (event instanceof ChannelStateEvent) {
				Channel channel = event.getChannel();
				ChannelStateEvent se = (ChannelStateEvent) event;
				if (se.getState() == ChannelState.OPEN && !channel.isOpen()) {
					removeKey(key);
				}
			}
		} else {
			super.execute(task);
		}
	}

	/**
	 * call removeKey(Object key) when the life cycle of the key ends, such as
	 * when the channel is closed
	 */
	protected boolean removeKey(Object key) {
		return map.remove(key) != null;
	}

	protected Object getKey(ChannelEvent e) {
		return e.getChannel();
	}

	@Override
	protected boolean shouldCount(Runnable task) {
		if (task instanceof EventTask) {
			return false;
		}

		return super.shouldCount(task);
	}

	protected final class EventTask implements Runnable {

		private ChannelEventRunnable runnable;
		private volatile EventTask next;

		EventTask(ChannelEventRunnable runnable) {
			this.runnable = runnable;
		}

		public void run() {
			try {
				this.runnable.run();
			} finally {
				// if "next" is not null, then trigger "next" to execute;
				// else if "next" is null, set "next" to END, means end this
				// "EventTask's LinkedList"
				if (!compareAndSetNext(this, null, END)) {
					execute(next);
				}
			}
		}
	}

	private boolean compareAndSetNext(EventTask eventTask, EventTask expect,
			EventTask update) {
		// because the "next" field is modified by method "doExecute()" and
		// method "EventTask.run()", so use CAS for thread-safe
		return fieldUpdater.compareAndSet(eventTask, expect, update);
	}
}
