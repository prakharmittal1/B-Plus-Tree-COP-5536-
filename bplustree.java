import java.io.BufferedWriter;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Queue;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.io.File;


// class bplustree
 class bplustree {

	public static void main(String args[])throws IOException {

		BPTree t;

		// read input file from the command prompt
		String fileName = args[0]+".txt";
		File inputFile = new File(fileName);
		
			Scanner scan;
			scan = new Scanner(inputFile);
			// creating a corresponding output file
			BufferedWriter buffer;
			buffer = newFile();

			
			t = new BPTree();
			String m;
			m=scan.nextLine().split("\\(|,|\\)")[1];
			t.initialize(Integer.parseInt(m));

			while (scan.hasNextLine()) 
			{
				String newLine;
				newLine = scan.nextLine();
				String[] input;
				input = newLine.split("\\(|,|\\)");
				switch (input[0]) {
				// Insert element into B+ Tree
				case "Insert": {
					t.node_insert(Integer.parseInt(input[1]), Double.parseDouble(input[2]));
					break;
				}
				case "Search": {
					if (input.length == 2) {
						List<Double> request = t.node_search(Integer.parseInt(input[1]));
						searchKey(request, buffer);
					} 
					// Find all key values
					else {
						List<Bp_key> request = t.node_search(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
						key_search(request, buffer);
					}
					break;
				}
			}
		}
			scan.close();
			buffer.close();		
	}

	// determines when an IO expection has occured 
	private static BufferedWriter newFile() throws IOException  
	{
		File file ;
		file= new File("output_file" + ".txt");
		BufferedWriter buffer;
		buffer = new BufferedWriter(new FileWriter(file));
		return buffer;
	}

	// for putting the node_search result (Value) into the output file 
	private static void searchKey(List<Double> request, BufferedWriter buffer) throws IOException {
		
		String newLine;
		newLine = "";

		if (null == request) 
		{
			buffer.write("Null");
		} else {
			Iterator<Double> Iterate_value;
			Iterate_value = request.iterator();
			while (Iterate_value.hasNext()) 
			{
				newLine = newLine + Iterate_value.next();
			}
			buffer.write(newLine.substring(0, newLine.length())); // write to the file when new value is found
		}
		buffer.newLine();
	}

	// Put the node_search key result (corresponding key) into the output file 
	private static void key_search(List<Bp_key> request, BufferedWriter buffer) throws IOException {
		String newLine = "";
		if (request.isEmpty()) {
			buffer.write("Null"); // no values found
		} else {
			Iterator<Bp_key> Iterate_key;
			Iterate_key = request.iterator(); // when the value is found - write to the file
			Iterator<Double> Iterate_value;
			Bp_key key;
			while (Iterate_key.hasNext()) {
				key = Iterate_key.next();
				Iterate_value = key.get_Values().iterator();
				while (Iterate_value.hasNext()) {
					newLine = newLine + Iterate_value.next() + ",";
				}
			}
			buffer.write(newLine.substring(0, newLine.length()-1));
		}
		buffer.newLine();
	}
}


// class key

 class Bp_key {

 	List<Double> node_val;
	int key;
	

	// start a new key and value
	public Bp_key(int key, double value) {
		this.key = key;
		if (null == this.node_val) 
		{
			node_val = new ArrayList<>();
		}
		this.node_val.add(value);
	}
	
	public Bp_key(int key) {
		this.key = key;
		this.node_val = new ArrayList<>();
	}

	public List<Double> get_Values() {
		return node_val;
	}

	public void bp_setKey(int key) {
		this.key = key;
	}
	
	public void bp_Value(List<Double> node_val) {
		this.node_val = node_val;
	}

	public int bp_getKey() {
		return key;
	}

}
  
 // Class Node 
 class Node {
	
	private Node next;
	private Node prev;

	private List<Node> internal_child;  //children- for internal nodes
	private List<Bp_key> keys;
	private Node node_parent;
	

	// Initiates a new node
	public Node() {
		this.prev = null;
		this.next = null;

		this.keys = new ArrayList<>();
		this.internal_child = new ArrayList<>();
		
	}

	public List<Bp_key> getKeys() {
		return keys;
	}

	public Node getPrev() {
		return prev;
	}
	public void setBpChild(List<Node> internal_child) 
	{
		this.internal_child = internal_child;
	}

	public void setKeys(List<Bp_key> keys) {
		Iterator<Bp_key> bp_iterate;
		bp_iterate = keys.iterator();

		while (bp_iterate.hasNext()) {
			this.keys.add(bp_iterate.next());
		}
	}

	public List<Node> get_Child() {
		return internal_child;
	}

	public Node get_BPlusParent() {
		return node_parent;
	}

	public void set_Prev(Node prev) {
		this.prev = prev;
	}
	public void set_Next(Node next) {
		this.next = next;
	}

	public Node get_Next() {
		return next;
	}

	public void set_Parent(Node node_parent) {
		this.node_parent = node_parent;
	}
	
}


// Class BPTree
 class BPTree 
 {
	private int m;		
	public BPTree(){}							//Degree of B+ tree
	private Node root;								
								

	//  Initialising the B + tree with a degree 'm'
	public void initialize(int order) 
	{		
		this.m = order;								// order as m
		this.root = null;							// root set = null 
	}

	//INSERTING KEY AS INT AND VALUE AS DOUBLE INTO THE B+ TREE
	public void node_insert(int key, double value) {

		if (null == this.root) 						
			{			
			Node newNode;
			newNode = new Node();							// As B+ tree is empty new root and key is created 
			newNode.getKeys().add(new Bp_key(key, value));
			this.root = newNode;
			this.root.set_Parent(null);
			}

		else if ((this.m - 1) > this.root.getKeys().size() && this.root.get_Child().isEmpty())    //a specific node is not full
		{

			insert_ExternalNode(this.root,  value, key );
		}

		else {																//the standard insert procedure at last leaf
			Node node_current;
			node_current = this.root;
			
			while (!node_current.get_Child().isEmpty()) 
			{
				node_current = node_current.get_Child().get(search_NodeInternal(key, node_current.getKeys()));
			}
			insert_ExternalNode(node_current, value, key );
			if (node_current.getKeys().size() == this.m) 
			{
				ext_nodeSplit(this.m, node_current);						// Splitting the leaf when it has no space
			}
		}

	}

		// Insering the key and the value in the external node
	private void insert_ExternalNode( Node node, double value, int key) {
		
		int indexOfKey = search_NodeInternal(key, node.getKeys());	  // Searching where the value is to be inserted
		if (indexOfKey != 0 && node.getKeys().get(indexOfKey - 1).bp_getKey() == key) 
			{
			node.getKeys().get(indexOfKey - 1).get_Values().add(value);
			} 

			else 
			{
			Bp_key newKey;
			newKey = new Bp_key(key, value);
			node.getKeys().add(indexOfKey, newKey);
			}
	}

	// To Split the external Node via finding the middle index
	private void ext_nodeSplit(int m, Node node_current ) {

		int NodeIndex_mid;
		NodeIndex_mid = m / 2;
		Node middle;
		middle = new Node();
		Node rightPart;
		rightPart = new Node();

		rightPart.setKeys(node_current.getKeys().subList(NodeIndex_mid, node_current.getKeys().size()));
		rightPart.set_Parent(middle);
		
		middle.getKeys().add(new Bp_key(node_current.getKeys().get(NodeIndex_mid).bp_getKey()));
		middle.get_Child().add(rightPart);
		
		node_current.getKeys().subList(NodeIndex_mid, node_current.getKeys().size()).clear();
		// Split and put the middle key up a level and merge with the parent node
		boolean firstSplit;
		firstSplit = true;					
		internal_NodeSplit( node_current, m, middle, firstSplit, node_current.get_BPlusParent());

	}

	// Splitting the Internal Node 
	private void internal_NodeSplit( Node prev, int m, Node node_Insert, boolean firstSplit, Node node_current) {
		if (null == node_current) 
			{
			this.root = node_Insert;				// new node created 
			// Doing the similar node_search as done while finding the external node
			int indexForPrev;
			indexForPrev = search_NodeInternal(prev.getKeys().get(0).bp_getKey(), node_Insert.getKeys());
			prev.set_Parent(node_Insert);
			node_Insert.get_Child().add(indexForPrev, prev);
			if (firstSplit) 
			{
				if (indexForPrev == 0) {
					node_Insert.get_Child().get(0).set_Next(node_Insert.get_Child().get(1));
					node_Insert.get_Child().get(1).set_Prev(node_Insert.get_Child().get(0));
				} else {
					node_Insert.get_Child().get(indexForPrev + 1).set_Prev(node_Insert.get_Child().get(indexForPrev));
					node_Insert.get_Child().get(indexForPrev - 1).set_Next(node_Insert.get_Child().get(indexForPrev));
				}
			}
		} else {
			
			internal_NodeMerge(node_Insert, node_current);
			if (node_current.getKeys().size() == m) {
				
				int NodeIndex_mid;
				NodeIndex_mid = (int) Math.ceil(m / 2.0) - 1;
				Node rightPart;
				rightPart = new Node();
				Node middle;
				middle = new Node();
				

				rightPart.setKeys(node_current.getKeys().subList(NodeIndex_mid + 1, node_current.getKeys().size()));
				rightPart.set_Parent(middle);    			// the mid part will be now the parent of the RST

				middle.getKeys().add(node_current.getKeys().get(NodeIndex_mid));
				middle.get_Child().add(rightPart);

				List<Node> childrenOfCurr;
				childrenOfCurr = node_current.get_Child();
				List<Node> childrenOfRight;
				childrenOfRight = new ArrayList<>();

				int lastChildOfLeft ;
				lastChildOfLeft= childrenOfCurr.size() - 1;

				for (int i = childrenOfCurr.size() - 1; i >= 0; i--) {
					List<Bp_key> currKeysList = childrenOfCurr.get(i).getKeys();
					if (middle.getKeys().get(0).bp_getKey() <= currKeysList.get(0).bp_getKey()) {
						childrenOfCurr.get(i).set_Parent(rightPart);
						childrenOfRight.add(0, childrenOfCurr.get(i));
						lastChildOfLeft--;
					} else {
						break;
					}
				}

				rightPart.setBpChild(childrenOfRight);

				node_current.get_Child().subList(lastChildOfLeft + 1, childrenOfCurr.size()).clear();
				node_current.getKeys().subList(NodeIndex_mid, node_current.getKeys().size()).clear();
				
				internal_NodeSplit( node_current, m, middle, false, node_current.get_BPlusParent());  //The upper level node are traversed up
			}
		}
	}

	// Merging the Internal Nodes of the B+ tree
	private void internal_NodeMerge(Node interal_NodeMerge, Node interal_NodeMergeResult) {
		
		Bp_key insert_Key;
		insert_Key = interal_NodeMerge.getKeys().get(0);
		Node childToBeInserted;
		childToBeInserted = interal_NodeMerge.get_Child().get(0);

		int pos_Insert;
		pos_Insert = search_NodeInternal(insert_Key.bp_getKey(),   //node_search to find where the key has to be inserted
			interal_NodeMergeResult.getKeys());
		int child_Insert = pos_Insert;

		// the child inserted to the next location
		if (insert_Key.bp_getKey() <= childToBeInserted.getKeys().get(0).bp_getKey()) {
			child_Insert = pos_Insert + 1;
		}
		childToBeInserted.set_Parent(interal_NodeMergeResult);
		interal_NodeMergeResult.get_Child().add(child_Insert, childToBeInserted);
		interal_NodeMergeResult.getKeys().add(pos_Insert, insert_Key);

		if (!interal_NodeMergeResult.get_Child().isEmpty() && interal_NodeMergeResult.get_Child().get(0).get_Child().isEmpty()) {
			// if the last element requires merge then location of the last element and previous last elemement is updated
			if (0 != child_Insert && interal_NodeMergeResult.get_Child().get(child_Insert - 1).get_Next() == null) {
				interal_NodeMergeResult.get_Child().get(child_Insert).set_Prev(interal_NodeMergeResult.get_Child().get(child_Insert - 1));
				interal_NodeMergeResult.get_Child().get(child_Insert - 1).set_Next(interal_NodeMergeResult.get_Child().get(child_Insert));
			}
			// if the last element requires merge then location of the last element and previous last elemement is updated
			else if(interal_NodeMergeResult.get_Child().size() - 1 != child_Insert
					&& interal_NodeMergeResult.get_Child().get(child_Insert + 1).getPrev() == null){
				interal_NodeMergeResult.get_Child().get(child_Insert + 1).set_Prev(interal_NodeMergeResult.get_Child().get(child_Insert));
				interal_NodeMergeResult.get_Child().get(child_Insert).set_Next(interal_NodeMergeResult.get_Child().get(child_Insert + 1));
			}
			
			// merge happening in between, next and the previouly last element location is updated.
			else {
				interal_NodeMergeResult.get_Child().get(child_Insert)
						.set_Next(interal_NodeMergeResult.get_Child().get(child_Insert - 1).get_Next());
				interal_NodeMergeResult.get_Child().get(child_Insert).get_Next()
						.set_Prev(interal_NodeMergeResult.get_Child().get(child_Insert));
				interal_NodeMergeResult.get_Child().get(child_Insert - 1).set_Next(interal_NodeMergeResult.get_Child().get(child_Insert));
				interal_NodeMergeResult.get_Child().get(child_Insert).set_Prev(interal_NodeMergeResult.get_Child().get(child_Insert - 1));
			}
		}
	}
	// Searching the 1st position where the key > input key
	public int search_NodeInternal(int key, List<Bp_key> keyList) {
		
		int initial = 0;
		int search_mid;
		int index = -1;

		int end = keyList.size() - 1;

		if (key < keyList.get(initial).bp_getKey()) 		//key < the elemnet at the position index 1
		{
			return 0;
		}

		if (key >= keyList.get(end).bp_getKey()) {
			return keyList.size();
		}
		while (initial <= end) {
			search_mid = (initial + end) / 2;
			
			if (key < keyList.get(search_mid).bp_getKey() && key >= keyList.get(search_mid - 1).bp_getKey())   //determining location where key is to be inserted
			{
				index = search_mid;
				break;
			}
			else if (key >= keyList.get(search_mid).bp_getKey()) {
				initial = search_mid + 1;
			} else {
				end = search_mid - 1;
			}
		}
		return index;
	}

	

	// finding all the keys and the values between key1 and key2
	public List<Bp_key> node_search(int key1, int key2) {
		
		List<Bp_key> key_search = new ArrayList<>();
		Node currNode = this.root;
		
		while (currNode.get_Child().size() != 0) //traverse until we get key1
		{
			currNode = currNode.get_Child().get(search_NodeInternal(key1, currNode.getKeys()));
		}
		boolean endSearch = false;
		while (null != currNode && !endSearch) {
			for (int i = 0; i < currNode.getKeys().size(); i++) {
				if (currNode.getKeys().get(i).bp_getKey() >= key1 && currNode.getKeys().get(i).bp_getKey() <= key2)
					key_search.add(currNode.getKeys().get(i));
				if (currNode.getKeys().get(i).bp_getKey() > key2) {
					endSearch = true;
				}
			}
			currNode = currNode.get_Next();
		}

		return key_search;
	}

	// searching the key's values
	public List<Double> node_search(int key) {
		List<Double> searchValues=new ArrayList();

		Node node_current = this.root;
	
		while (node_current.get_Child().size() != 0) {
			node_current = node_current.get_Child().get(search_NodeInternal(key, node_current.getKeys()));
		}
		List<Bp_key> keyList = node_current.getKeys();

		for (int i = 0; i < keyList.size(); i++) {
			if (key == keyList.get(i).bp_getKey()) {
				searchValues = keyList.get(i).get_Values();  // searching the values that meet the parameters
			}
			if (key < keyList.get(i).bp_getKey()) {
				break;
			}
		}
		return searchValues;
	}

}

