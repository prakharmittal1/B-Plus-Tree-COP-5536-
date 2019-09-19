Problem description

The primary value of a B+ tree is in storing data for efficient retrieval in a block-oriented storage context — in particular,file systems.
In this project, I have developed and tested a small degree B+ tree used for internal-memory dictionaries (i.e. the entire tree resides
in main memory). The data is given in the form (key, value) with no duplicates,it was required to implement an m-way B+ tree to store 
the data pairs. Note that in a B+ tree only leaf nodes contain the actual values, and the leaves should be linked into a doubly linked list.

This implementation supports the following operations:
1. Initialize (m): create a new m-way B+ tree
2. Insert (key, value)
3. Search (key): returns the value associated with the key
4. Search (key1, key2): returns values such that in the range key1 <= key <= key2
