package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Project 2
 * Written by : Dennis Muljadi and Gilbert Febrianto
 * 
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
		if (this.size() == 0) { // special case if the list contains nothing
			this.front = new Node<T>(item); // front of the list is the same as the back
			this.back = this.front;
		} else {
			this.back.next = new Node<T>(this.back, item, null); // add a node after the end of the list
			this.back = back.next; // move the end to the newly added node
		}
        this.size++; // increase the size by one
    }

    @Override
    public T remove() {
    		T item = this.back.data; // initialize the data in the back node
    		if (this.size() == 0) { // special case if the list contains nothing
    			throw new EmptyContainerException(); // throw EmptyContainerException
    		} else if (this.size() == 1) { // special case if the list contains only one node
    			this.front = new Node<T>(null); // empty the list
    			this.back = this.front;
    		} else {
    			this.back = this.back.prev; // move the back node to the node before it
    			this.back.next = null; // assign null to this.back.next since it is the back node
    		}
    		this.size--; // decrease the size by one
    		return item; // return the previously initialized data from the previous back node
    }

    @Override
    public T get(int index) {
    		Node<T> temp = getNode(index, this.size()); // get the node corresponding to the index
    		return temp.data; // return the data of the found node
    }

    @Override
    public void set(int index, T item) {    	
    		Node<T> temp = getNode(index, this.size()); // get the node at the corresponding index
		Node<T> newNode = new Node<T>(item);	// initialize the newNode
		
		if (index == 0) { // special case if inserting the newNode to the front
			this.front = newNode;
			newNode.prev = null;
		} else {
			temp.prev.next = newNode;
			newNode.prev = temp.prev;
		}
		
		newNode.next = temp.next;
		
		if (index == this.size() - 1) { // special case if inserting the newNode to the back
			this.back = newNode;
		} else {
			temp.next.prev = newNode;
		}	
    }

    @Override
    public void insert(int index, T item) {
    		if (index == this.size()) {
    			add(item); 
    		}
    		else {	// find the nodes efficiently, depending if it is near the front or back
	    		Node<T> temp1;
	    		Node<T> temp2; 
			if (index <= this.size()/2) {
	    			temp2 = getNode(index, this.size()+1); 
	    			temp1 = temp2.prev;
			} else {
	    			// if index is not within the bounds, throw IndexOutOfBoundsException
	    			if (index < 0 || index >= this.size()+1) {
	    				throw new IndexOutOfBoundsException();
	    			}
				temp1 = this.back;
				temp2 = null;
				for (int i = this.size()-1; i >= index; i--) { 
					temp2 = temp1;
					temp1 = temp1.prev;
				}
			}
			
			Node<T> newNode = new Node<T>(item); // initialize the newNode
			
			if (index == 0) { // special case if inserting the newNode to the front
				this.front = newNode;
			} else {
				temp1.next = newNode;
			}
			
			newNode.next = temp2;
			newNode.prev = temp1;
			temp2.prev = newNode;
			
			this.size++; // increase the size by one
    		}
    }

    @Override
    public T delete(int index) {
    		if (index == this.size()-1) { // special case if want to delete the back node
    			return remove();
    		}    		

    		Node<T> temp = getNode(index, this.size()); // get the node at the corresponding index
		T item = temp.data; // save the data from the soon to be deleted node
		
		if (index == 0) { // special case if want to delete the front node
			this.front = this.front.next;
			this.front.prev = null;
		} else {
			temp.prev.next = temp.next;
			temp.next.prev = temp.prev;
		}
		this.size--; // decrease size by one
		return item; // return the data in the deleted node
    }

    @Override
    public int indexOf(T item) {
		Node<T> temp = this.front; 				// for loop to find the index
		for (int i = 0; i < this.size(); i++) { 	// that contains the item
    			if (temp.data == item || temp.data.equals(item)) { // compare the data in the node to item
    				return i; // return index
    			}
    			temp = temp.next;			
		}
		return -1; // if item not index, return -1
    }

    @Override
    public int size() {
        return this.size; // return the size of the list
    }

    @Override
    public boolean contains(T other) {
    	return indexOf(other) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
        		return current != null; 
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
        		if (!hasNext()) {				// if hasNext() returns false
            		throw new NoSuchElementException();	// throw NoSuchElementException (end of iteration)
            }
            T temp = current.data;
            current = current.next; // move the current node to the one next to it
            return temp; // return the data in the current node
        }
    }
    
    // helper method
    public Node<T> getNode(int index, int max) {
		if (index < 0 || index >= max) {
			throw new IndexOutOfBoundsException();    	
		}
		Node<T> temp = this.front;
		for (int i = 0; i < index; i++) {
			temp = temp.next;
		}
		return temp; // return the node corresponding to the index
    }
}
