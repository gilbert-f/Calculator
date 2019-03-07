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
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // extra fields
    protected static final int INITIAL_CAPACITY = 7;
    private static final int[] PRIMES = {
    		7, 13, 31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381,
    	    32749, 65521, 131071, 262139, 524287, 1048573, 2097143, 4194301,
    	    8388593, 16777213, 33554393, 67108859, 134217689, 268435399,
    	    536870909, 1073741789, 2147483647};
    private static final float LOAD_FACTOR = 0.75f; // load factor
    private int tableSize;
    private int pairsSize;
    private int resizeCounter;
    private int maxPairsSize;

    
    public ChainedHashDictionary() {
    		this.chains = makeArrayOfChains(INITIAL_CAPACITY);
    		this.tableSize = INITIAL_CAPACITY;
    		this.pairsSize = 0;
    		this.resizeCounter = 0;
    		this.maxPairsSize = 5;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int hash = hash(key); // calls the hash function
        if (chains[hash] != null) {
        		return chains[hash].get(key); // return the value in the dictionary
        }
		throw new NoSuchKeyException(); // if key not found, throw NoSuchKeyException
    }

    @Override
    public void put(K key, V value) {
    		checkResize(); // resize if needed
    		int hash = hash(key);
    		if (chains[hash] == null) { // if the entry in the array is empty
    			chains[hash] = new ArrayDictionary<>(); // create a dictionary 
    		}
    		if (!chains[hash].containsKey(key)) {
    			pairsSize++; // increment the size if a new key is put
    		}
        chains[hash].put(key, value);
    }

    @Override
    public V remove(K key) {
    		int hash = hash(key);
        if (chains[hash] != null) {
        		if (chains[hash].containsKey(key)) {
        			pairsSize--; // decrement the size if a key is remove
        		}
        		return chains[hash].remove(key);
        } else {
        		throw new NoSuchKeyException(); // if key not found, throw NoSuchKeyException
        }
    }

    @Override
    public boolean containsKey(K key) {
    		int hash = hash(key);
    		if (chains[hash] != null) {
    			return chains[hash].containsKey(key); // return true if the dict contains the key
    		} else {
    			return false;
    		}
    }

    @Override
    public int size() {
    		return pairsSize;
    }
    
    /**
     * hash function
     */
    public int hash(K key) {
    		int hash;
    		if (key == null) { 
    			hash = 0; // set hash = 0 if given key is null
    		} else {
        		hash = (key.hashCode() & 0x7fffffff) % tableSize; // hash to positive int only
        }
        return hash;
    }
    
    /**
     * resize the array roughly twice the original size when necessary
     */
    public void checkResize() {
    		if (pairsSize >= maxPairsSize) {
    			int oldTableSize = tableSize;
    			resizeCounter += 1; // increment the resize counter
    			tableSize = PRIMES[resizeCounter]; // pull out a new prime number from PRIMES array
    			maxPairsSize = (int) (tableSize * LOAD_FACTOR); // update the max pairs number
    			IDictionary<K, V>[] newChains = makeArrayOfChains(tableSize); // newChains
    			pairsSize = 0;
    			for (int i = 0; i < oldTableSize; i++) { // copy the dictionaries from old chains to new chains
    				if (chains[i] != null) {
	    				for (KVPair<K, V> pair : chains[i]) {
		    		    		int hash = hash(pair.getKey());
		    		    		if (newChains[hash] == null) {
		    		    			newChains[hash] = new ArrayDictionary<>();
		    		    		}
		    		    		if (!newChains[hash].containsKey(pair.getKey())) {
		    		    			pairsSize++;
		    		    		}
		    		    		newChains[hash].put(pair.getKey(), pair.getValue());
	    				}
    				}
    			}
    			chains = newChains;
    		}
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(chains);
    }
   
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private Iterator<KVPair<K, V>> iter;
		private int index;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            findNonNullIndex(); // find the first non-null index
            if (hasNext()) {
            		this.iter = this.chains[index].iterator(); // inner dictionary iterator
            }
        }

        @Override
        public boolean hasNext() {
        		return (index != -1);
        }

        @Override
        public KVPair<K, V> next() {
            if (!hasNext() || !iter.hasNext()) { // if end of iteration
                throw new NoSuchElementException();
            }
            KVPair<K, V> temp = iter.next(); // the next element in the inner dictionary iterator
            if (!iter.hasNext()) { // if reaches the end of inner dictionary iterator
                index++;
                findNonNullIndex(); // find the next non-null index in the chains array
                if (hasNext()) {
                    iter = chains[index].iterator();
                }
            }
            return temp;
        }
        
        public void findNonNullIndex() {
        		// find the first non-null entry in chains
        		while (index < chains.length && chains[index] == null) { 
        			index++;
            }
        		if (index >= chains.length) { // if the index is still within the chains' length
        			index = -1; // otherwise, return -1, array is empty
        		}
        }
    }
}
