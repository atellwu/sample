/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package queue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CircularFifoQueue is a first-in first-out queue with a fixed size that
 * replaces its oldest element if full.
 * <p>
 * The removal order of a {@link CircularFifoQueue} is based on the insertion
 * order; elements are removed in the same order in which they were added. The
 * iteration order is the same as the removal order.
 * <p>
 * The {@link #add(Object)}, {@link #remove()}, {@link #peek()}, {@link #poll},
 * {@link #offer(Object)} operations all perform in constant time. All other
 * operations perform in linear time or worse.
 * <p>
 * This queue prevents null objects from being added.
 *
 * @since 4.0
 * @version $Id: CircularFifoQueue.java 1543246 2013-11-19 00:36:29Z ggregory $
 */
public class CircularFifoQueue<E> {

	private E[] elements;

	private AtomicInteger putIndex = new AtomicInteger(-1);

	private AtomicInteger pollIndex = new AtomicInteger(0);

	/** Lock held by take, poll, etc */
	private final ReentrantLock takeLock = new ReentrantLock();

	/** Wait queue for waiting takes */
	private final Condition notEmpty = takeLock.newCondition();

	private void signalNotEmpty() {
		final ReentrantLock takeLock = this.takeLock;
		takeLock.lock();
		try {
			notEmpty.signal();
		} finally {
			takeLock.unlock();
		}
	}

	public CircularFifoQueue() {
		this(32);
	}

	@SuppressWarnings("unchecked")
	public CircularFifoQueue(final int size) {
		if (size <= 0) {
			throw new IllegalArgumentException(
					"The size must be greater than 0");
		}
		elements = (E[]) new Object[size];
	}

	public void put(E e) {
		int index = putIndex.incrementAndGet() % elements.length;
		elements[index] = e;
		if(index == ){
			signalNotEmpty();
		}
	}

	public E poll() throws InterruptedException {
		this.takeLock.lockInterruptibly();
		try {
			while(pollIndex == putIndex){//empty
				notEmpty.wait();
			}
			return elements[pollIndex.getAndIncrement() % elements.length];
		} finally {
			this.takeLock.unlock();
		}
	}

}