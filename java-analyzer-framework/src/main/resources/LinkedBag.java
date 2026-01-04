/* @formatter:off
 *
 * © David M Rosenberg
 *
 * Topic: Bags
 *
 * Usage restrictions:
 *
 * You may use this code for exploration, experimentation, and furthering your
 * learning for this course. You may not use this code for any other
 * assignments, in my course or elsewhere, without explicit permission, in
 * advance, from myself (and the instructor of any other course).
 *
 * Further, you may not post (including in a public repository such as on github)
 * nor otherwise share this code with anyone other than current students in my
 * sections of this course.
 *
 * Violation of these usage restrictions will be considered a violation of
 * Wentworth Institute of Technology's Academic Honesty Policy.  Unauthorized posting
 * or use of this code may also be considered copyright infringement and may subject
 * the poster and/or the owners/operators of said websites to legal and/or financial
 * penalties.  Students are permitted to store this code in a private repository
 * or other private cloud-based storage.
 *
 * Do not modify or remove this notice.
 *
 * @formatter:on
 */

package edu.wit.scds.ds.bags;

import education.the_software_toolsmith.testing.framework.StubMethodException;

import edu.wit.scds.ds.bags.tests.ArrayBag;
import edu.wit.scds.ds.common.chains.enhanced.Node;

import java.util.Arrays;

/**
 * A class of bags whose entries are stored in a chain of linked nodes. The bag
 * is never full.
 *
 * Note: This implementation does not permit {@code null} entries.
 *
 * @author Johanna Hoang
 *
 * @version 6.2 2025-10-01 implementation per assignment
 *
 * @param <T> The class of entry the {@code LinkedBag} will hold.
 */
public final class LinkedBag<T> implements BagInterface<T> {

	private Node<T> firstNode;
	private int numberOfEntries;

	public LinkedBag() {
		initializeState();
	}

	public LinkedBag(final BagInterface<T> sourceBag) {
		this();
		if (sourceBag != null) {
			T[] items = sourceBag.toArray();
			add(items);
		}
	}

	public LinkedBag(final T[] initialContents) {
		this();
		if (initialContents != null) {
			add(initialContents);
		}
	}

	@Override
	public boolean add(final T newEntry) {
		if (newEntry == null) {
			throw new IllegalArgumentException("entry cannot be null");
		}
		final Node<T> newNode = new Node<>(newEntry, this.firstNode);
		this.firstNode = newNode;
		this.numberOfEntries++;
		return true;
	}

	@Override
	public void clear() {
		initializeState();
	}

	@Override
	public boolean contains(final T anEntry) {
		return getReferenceTo(anEntry) != null;
	}

	@Override
	public BagInterface<T> difference(final BagInterface<T> anotherBag) {
		if (anotherBag == null) {
			return new LinkedBag<>();
		}

		final LinkedBag<T> resultBag = new LinkedBag<>(this);
		for (T item : anotherBag.toArray()) {
			resultBag.remove(item);
		}
		return resultBag;
	}

	@Override
	public int getCurrentSize() {
		return this.numberOfEntries;
	}

	@Override
	public int getFrequencyOf(final T anEntry) {
		if (anEntry == null) {
			return 0;
		}

		int foundCount = 0;
		Node<T> currentNode = this.firstNode;
		while (currentNode != null) {
			if (currentNode.getData().equals(anEntry)) {
				foundCount++;
			}
			currentNode = currentNode.getNext();
		}
		return foundCount;
	}

	@Override
	public BagInterface<T> intersection(final BagInterface<T> anotherBag) {
		if (anotherBag == null) {
			return new LinkedBag<>();
		}

		final LinkedBag<T> resultBag = new LinkedBag<>();
		T[] thisItems = this.toArray();
		LinkedBag<T> tempBag = new LinkedBag<>(anotherBag);

		for (T item : thisItems) {
			if (tempBag.remove(item)) {
				resultBag.add(item);
			}
		}
		return resultBag;
	}

	@Override
	public boolean isEmpty() {
		return this.numberOfEntries == 0;
	}

	@Override
	public T remove() {
		if (isEmpty()) {
			return null;
		}
		final T anEntry = this.firstNode.getData();
		this.firstNode = this.firstNode.getNext();
		this.numberOfEntries--;
		return anEntry;
	}

	@Override
	public boolean remove(final T anEntry) {
		final Node<T> foundEntry = this.getReferenceTo(anEntry);
		if (foundEntry == null) {
			return false;
		}
		foundEntry.setData(this.firstNode.getData());
		remove();
		return true;
	}

	@Override
	public T[] toArray() {
		@SuppressWarnings("unchecked")
		final T[] result = (T[]) new Object[this.numberOfEntries];
		int index = 0;
		Node<T> currentNode = this.firstNode;
		while (currentNode != null) {
			result[index] = currentNode.getData();
			currentNode = currentNode.getNext();
			index++;
		}
		return result;
	}

	@Override
	public String toString() {
		return String.format("nOE: %,d; data: %s", this.numberOfEntries, Arrays.toString(toArray()));
	}

	@Override
	public BagInterface<T> union(final BagInterface<T> anotherBag) {
		if (anotherBag == null) {
			return new LinkedBag<>(this);
		}
		final LinkedBag<T> resultBag = new LinkedBag<>(this);
		resultBag.add(anotherBag.toArray());
		return resultBag;
	}

	private void add(final T[] newEntries) {
		for (T item : newEntries) {
			add(item);
		}
	}

	private Node<T> getReferenceTo(final T anEntry) {
		if (anEntry == null) {
			return null;
		}
		Node<T> currentNode = this.firstNode;
		while (currentNode != null) {
			if (currentNode.getData().equals(anEntry)) {
				return currentNode;
			}
			currentNode = currentNode.getNext();
		}
		return null;
	}

	private void initializeState() {
		this.firstNode = null;
		this.numberOfEntries = 0;
	}

	public static void main(final String[] args) {
		final BagInterface<String> bag1 = new LinkedBag<>();
		printStuff(bag1, null);

		final BagInterface<String> bag2 = new ArrayBag<>();
		printStuff(bag1, bag2);

		bag1.add("a");
		printStuff(bag1, bag2);

		bag2.add("a");
		printStuff(bag1, bag2);

		bag2.add("b");
		printStuff(bag1, bag2);

		bag1.add("a");
		bag2.add("b");
		printStuff(bag1, bag2);

		bag2.add("a");
		printStuff(bag1, bag2);

		bag1.add("a");
		printStuff(bag1, bag2);

		bag2.clear();
		bag2.add("z");
		bag2.add("y");
		bag2.add("x");
		bag2.add("w");
		printStuff(bag1, bag2);
	}

	private static void printStuff(final BagInterface<String> bag1, final BagInterface<String> bag2) {
		System.out.printf("linked and array-backed bags:%nLB: %s%nAB: %s%n%n", bag1, bag2);
	}

} // end class LinkedBag
