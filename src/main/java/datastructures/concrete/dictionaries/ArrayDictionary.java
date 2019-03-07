package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Project 2
 * Written by : Dennis Muljadi and Gilbert Febrianto
 * 
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;

    // extra fields
    protected static final int INITIAL_CAPACITY = 10;
    private int size;

    public ArrayDictionary() {
    		this.pairs = makeArrayOfPairs(INITIAL_CAPACITY);   
        this.size = 0; // set the actual size to 0 not the .length
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        for (int i = 0; i < size(); i++) { // for loop to find the key in the array
        		if (key == null) {
        			if (pairs[i].key == key || pairs[i].key.equals(key)) {
        				return pairs[i].value; // return the value corresponding to the key
        			}
        		} else {
	        		if (pairs[i].key == key || key.equals(pairs[i].key)) {
	        			return pairs[i].value; // return the value corresponding to the key
	        		}
        		}
        }
        throw new NoSuchKeyException(); // throw NoSuchKeyException if key not found
    }

    @Override
    public void put(K key, V value) {
    		int indexOf = indexOf(key); 
        if (indexOf == -1) { // if not found
        		checkResize();	// check if the array needs a resize
        		pairs[size()] = new Pair<K, V>(key, value); // add the pair to the end of the actual array
        		size++; // increase size by one
        } else { // if key found
        		pairs[indexOf].value = value; // replace the old value with new value
        }
    }

    @Override
    public V remove(K key) {
    		int indexOf = indexOf(key);
        if (indexOf == -1) { // if not found
        		throw new NoSuchKeyException(); // throw NoSuchKeyException
        } else { // if found update the array
        		V temp = pairs[indexOf].value;
            for (int index = indexOf; index <= size() - 1; index++) {
                pairs[index] = pairs[index + 1];
            }        	
        		size--;
        		return temp; // return the old value
        }
    }

    @Override
    public boolean containsKey(K key) {
    		return (indexOf(key) != -1);
    }

    @Override
    public int size() {
        return size; // return the actual size of the array
    }  
    
    /**
     * helper method to resize the array double the original size
     */
    private void checkResize() {
        if (size() >= pairs.length) {
        		Pair<K, V>[] newPairs = makeArrayOfPairs(pairs.length*2);
        		for (int i = 0; i < size(); i++)  {
        			newPairs[i] = pairs[i];
        		}
        		pairs = newPairs;
        }
    }
    
    /**
     * helper method to return the index of the corresponding key
     */
    public int indexOf(K key) {
        for (int i = 0; i < size(); i++) { // for loop to find the key in the array
        		if (key == null) {
        			if (pairs[i].key == key || pairs[i].key.equals(key)) {
		    			return i; // return the value corresponding to the key
        			}
        		} else {
	        		if (pairs[i].key == key || key.equals(pairs[i].key)) {
		    			return i; // return the value corresponding to the key
	        		}
        		}
        }
        return -1; // if not found return -1
    }    

    private static class Pair<K, V> {
        public K key;
        public V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
    
    @Override
    public Iterator<KVPair<K, V>> iterator() {
    		return new ArrayDictionaryIterator<>(pairs, size());
    }
    
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
    		private Pair<K, V>[] pairs;
    		private int index;
    		private int size;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int size) {
        		this.pairs = pairs;
        		this.index = 0;
        		this.size = size;
        }
        
        @Override
        public boolean hasNext() {
        		return index < size; // return true if index is still within the size
        }

        @Override
        public KVPair<K, V> next() {	
        		if (!hasNext()) {			
        			throw new NoSuchElementException();
        		}
        		KVPair<K, V> temp = new KVPair<K, V>(pairs[index].key, pairs[index].value);
        		index++;
        		return temp;
        }
    }
}
