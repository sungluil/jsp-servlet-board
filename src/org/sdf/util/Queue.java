package org.sdf.util;

/*
 * @(#)Queue.java   1.1 2001/04/18
 *
 * (c) Copyright JavaService.Net, 2001. All rights reserved.
 *
 * NOTICE !      You can copy or redistribute this code freely, 
 * but you should not remove the information about the copyright notice 
 * and the author.
 * 
 * @author  Lee WonYoung  javaservice@hanmail.net, lwy@kr.ibm.com
 * 
 */

import java.util.Vector;

/**
 * A simple FIFO queue class which causes the calling thread to wait if the
 * queue is empty and notifies threads that are waiting when it is not empty.
 * 
 * @author Anil V (akv@eng.sun.com)
 */
public class Queue {
	private Vector vector = new Vector();

	/**
	 * Put the object into the queue.
	 * 
	 * @param object
	 *            the object to be appended to the queue.
	 */
	public synchronized void put(Object object) {
		vector.addElement(object);
		notify();
	}

	/**
	 * Pull the first object out of the queue. Wait if the queue is empty.
	 */
	public synchronized Object pull() {
		while (isEmpty())
			try {
				wait();
			} catch (InterruptedException ex) {
			}
		return get();
	}

	/**
	 * Get the first object out of the queue. Return null if the queue is empty.
	 */
	public synchronized Object get() {
		Object object = peek();
		if (object != null)
			vector.removeElementAt(0);
		return object;
	}

	/**
	 * Peek to see if something is available.
	 */
	public Object peek() {
		if (isEmpty())
			return null;
		return vector.elementAt(0);
	}

	/**
	 * Is the queue empty?
	 */
	public boolean isEmpty() {
		return vector.isEmpty();
	}

	/**
	 * How many elements are there in this queue?
	 */
	public int size() {
		return vector.size();
	}
}
