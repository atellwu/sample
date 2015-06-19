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

/**
 * <pre>
 * This is a <b>fair</b> alternative of OrderedMemoryAwareThreadPoolExecutor.
 * 
 * <h3>Unfair of OrderedMemoryAwareThreadPoolExecutor</h3>
 * The task executed in OrderedMemoryAwareThreadPoolExecutor is unfair in some situations.
 * For example, let's say there is only one executor thread that handle the events from the two channels, and events are submitted in sequence:
 * 
 *           Channel A (Event A1) , Channel B (Event B), Channel A (Event A2) , ... , Channel A (Event An)
 * 
 * Then the events maybe executed in this unfair order:
 * 
 *          ----------------------------------------------------&gt; Timeline ------------------------------------------------------&gt;
 *           Channel A (Event A1) , Channel A (Event A2) , ... , Channel A (Event An), Channel B (Event B)
 * 
 *  As we see above, Channel B (Event B) maybe executed unfairly late.
 *  Even more, if there are too much events come in Channel A, and one-by-one closely, then Channel B (Event B) would be waiting for a long while and become "hungry".
 * 
 * <h3>Fair of FairOrderedMemoryAwareThreadPoolExecutor</h3>
 * In the same case above ( one executor thread and two channels ) , this implement will guarantee execution order as:
 * 
 *          ----------------------------------------------------&gt; Timeline ------------------------------------------------------&gt;
 *           Channel A (Event A1) , Channel B (Event B), Channel A (Event A2) , ... , Channel A (Event An),
 * 
 * </pre>
 *  <b>NOTE</b>:  For convenience the case above use <b>one single executor thread</b>, but the fair mechanism is suitable for <b>multiple executor threads</b> situations.
 * 
 * @author atell.wu
 *
 */
public class FairOrderedMemoryAwareThreadPoolExecutor extends
		MemoryAwareThreadPoolExecutor {

	//end sign
	private final EventTask END = new EventTask(null);

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

			/*
			 * e.g. Three event "Channel A (Event A1)","Channel A (Event A2)","Channel A (Event A3)" are submitted in sequence, then
			 * key "Channel A" is refer to the  value of "Event A3", and "Event A3" -> "Event A2" -> "Event A1" ( linked by the field "next" in EventTask )
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
