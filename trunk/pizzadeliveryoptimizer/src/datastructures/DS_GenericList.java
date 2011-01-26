package datastructures;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;

public class DS_GenericList<K> implements Iterable<K>, Serializable
{

	// Serialisation ID required by the JGraphX package
	private static final long serialVersionUID = 1L;

	private class Item implements Serializable
	{
		// Serialisation ID required by the JGraphX package
		private static final long serialVersionUID = -2671831338657982908L;

		// Object that holds the data of the item
		private K object;

		// Next item in the list
		public Item next;

		/**
		 * Item constructor
		 * @param obj to associate this Item with
		 */
		public Item(K obj)
		{
			this.object = obj;
			this.next = null;
		}

		/**
		 * Create an empty item with a 'next' Item (used for the Iterator)
		 * @param next item in the list
		 */
		public Item(Item next)
		{
			this.object = null;
			this.next = next;
		}

		/**
		 * Accessing method for the data in the item
		 * @return the object associated with this item
		 */
		public K getData()
		{
			return this.object;
		}
	}

	private class ItemIterator implements Iterator<K>
	{
		// Object that holds the current item
		private Item current;

		// List object (used for removal)
		private DS_GenericList<K> list;

		/**
		 * ItemIterator constructor
		 * @param first item to start the iterator off with
		 * @param list that the iterator will use
		 */
		public ItemIterator(Item first, DS_GenericList<K> list)
		{
			this.current = first;
			this.list = list;
		}

		/**
		 * Determine if the iterator has another item in it
		 * @return whether the iterator has another item in it or not
		 */
		public boolean hasNext()
		{
			if(this.current == null || this.current.next == null)
				return false;
			else
				return true;
		}

		/**
		 * Get the data of the following item in the iteration
		 * @return the data of the next item
		 */
		public K next()
		{
			if(this.current == null) return null;
			this.current = this.current.next;
			if(this.current == null) return null;
			return this.current.getData();
		}

		/**
		 * Remove the current item from the iterated list
		 */
		public void remove()
		{
			Item temp = this.current.next;
			this.list.remove(this.current.getData());
			this.current = temp;
		}
	}

	// Integer variable to hold the number of items stored in the list
	private int numberOfItems;

	// Variable to hold the first item in the list
	private Item first;

	/**
	 * Basic constructor for the GenericList class
	 */
	public DS_GenericList()
	{
		this.numberOfItems = 0;
		this.first = null;
	}

	/**
	 * Determine if the list is empty
	 * @return if the list is empty of not
	 */
	public boolean isEmpty()
	{
		return this.first == null;
	}

	/**
	 * Get the size of the list (how many items are in it)
	 * @return integer size of the list
	 */
	public int size()
	{
		return this.numberOfItems;
	}

	/**
	 * Add an object to the list
	 * @param obj to add
	 * @return whether or not the object was added
	 */
	public boolean add(K obj)
	{
		if(obj == null) return false;

		Item i = new Item(obj);
		Item t = this.first;

		if(t == null) {
			this.first = i;
		} else {
			while(t.next != null)
				t = t.next;
			t.next = i;
		}

		this.numberOfItems++;

		return true;
	}

	/**
	 * Remove an object from the list
	 * @param obj to remove from the list
	 * @return whether or not it was removed
	 */
	public boolean remove(K obj)
	{
		if(this.first == null || obj == null) return false;

		Item t = this.first;

		if(t.getData() == obj) {
			this.first = t.next;
		} else {
			while(t.next != null) {
				if(t.next.getData() == obj) {
					t.next = t.next.next;
					break;
				} else {
					t = t.next;
				}
			}
		}

		this.numberOfItems--;

		return true;
	}

	/**
	 * Get an iterator for this list
	 * @return Iterator object initialised to this list
	 */
	public Iterator<K> iterator()
	{
		return new ItemIterator(new Item(this.first), this);
	}

	/**
	 * Create an array of objects based on this list
	 * @param c / class type to return
	 * @return array representation of this list
	 */
	public K[] toArray(Class<K> c)
	{
		@SuppressWarnings("unchecked")
		K[] result = (K[])Array.newInstance(c, this.size());
		int index  = 0;
		for(K k : this)
			result[index++] = k;
		return result;
	}

}
