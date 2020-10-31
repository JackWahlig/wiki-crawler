import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * NetworkInfluence class
 * 
 * Implements an analysis class that creates statistics based on a graph. Meant
 * to be used in conjunction with the WikiCrawler class to analyze different
 * wikipages and their links.
 * 
 * @author Jacob Feddersen, Luke Heilman, John Wahlig
 *
 */
public class NetworkInfluence {

	/**
	 * Map of indices to string names of vertices
	 */
	private HashMap<Integer, String> intToVertexName;

	/**
	 * Map of vertex names to integer representation
	 */
	private HashMap<String, Integer> vertexNameToInt;

	/**
	 * Counter for next unused vertex index
	 */
	private int vertices;

	/**
	 * Adjacency list used to store the graph edges
	 */
	private ArrayList<ArrayList<Integer>> adjList;

	/**
	 * Constructs a new NetworkInfluence object based on the graph stored at
	 * graphData.
	 * 
	 * Parses the input file and constructs the graph based on the contents of the
	 * file
	 * 
	 * @param graphData
	 *            The absolute file path of the file that stores the graph to be
	 *            analyzed.
	 * @throws FileNotFoundException
	 */
	public NetworkInfluence(String graphData) throws FileNotFoundException {
		// Open the input file for parsing
		File inputFile = new File(graphData);
		Scanner s = new Scanner(inputFile);

		// Get the number of vertices in the graph, for use in initialization
		int numVertices = Integer.parseInt(s.nextLine());

		// Initialize the hash maps to an initial size based on number of vertices
		// expected
		intToVertexName = new HashMap<Integer, String>(numVertices);
		vertexNameToInt = new HashMap<String, Integer>(numVertices);

		// Vertex counter starts at 0
		vertices = 0;

		// Initialize the adjacency list to an initial size based on number of vertices
		// expected
		adjList = new ArrayList<ArrayList<Integer>>(numVertices);

		// Process every line of the file
		while (s.hasNextLine()) {
			String line = s.nextLine();

			// Split the line into its two components
			String[] components = line.split("\\s+");
			if (components.length != 2) {
				// Every line should have a pair of vertex names; if not, something is wrong
				s.close();
				throw new IllegalArgumentException("Input file misformatted: " + line);
			}

			// Get the vertex indices for the two vertex names of the edge
			int u = checkVertex(components[0]);
			int v = checkVertex(components[1]);

			// Add v to the adjacency list of u, creating an edge from u to v
			adjList.get(u).add(v);
		}

		// Close the file
		s.close();

		// If we didn't see the number of vertices that we expected, the input file was
		// invalid
		if (vertices != numVertices) {
			throw new IllegalArgumentException("The wrong number of vertices were found in the file: " + vertices);
		}
	}
	
	/**
	 * Checks if a vertex currently exists, and if not creates an index entry for it
	 * 
	 * @param vertexName
	 *            the name of the vertex to check
	 * @return the number that the vertex is mapped to
	 */
	private int checkVertex(String vertexName) {
		// Try to get the index of the given vertex name
		Integer vertexIndex = vertexNameToInt.get(vertexName);

		// If it is null, we haven't seen this vertex yet
		// We need to add it to the hash tables
		if (vertexIndex == null) {
			// Get the next index to use for this vertex
			vertexIndex = vertices++;

			// Create a new list in the adjacency list for this vertex
			adjList.add(new ArrayList<Integer>());

			// Add this vertex name and index pair to the hash tables
			intToVertexName.put(vertexIndex, vertexName);
			vertexNameToInt.put(vertexName, vertexIndex);
		}

		// Return the index for this vertex
		return vertexIndex.intValue();
	}

	/**
	 * Returns the out-degree of the vertex v
	 * 
	 * @param v
	 *            - The vertex to get the out-degree of
	 * @return The out-degree of v
	 */
	public int outDegree(String v) {
		// Simply return the size of the adjacency list at v
		int vIndex = vertexNameToInt.get(v);
		return adjList.get(vIndex).size();
	}

	/**
	 * Returns a BFS path from u to v. This method returns an array list of strings
	 * that represents a shortest path from u to v. Note that this method must
	 * return an array list of Strings. First vertex in the path must be u and the
	 * last vertex must be v. If there is no path from u to v, then this method
	 * returns an empty list.
	 * 
	 * @param u
	 *            The first vertex of the path
	 * @param v
	 *            The last vertex of the path
	 * @return An ArrayList of Strings that represents the shortest path from u to
	 *         v. This list is empty if there is no path between u and v.
	 */
	public ArrayList<String> shortestPath(String u, String v) {
		// Shortcut; if the end is same as the start, return a path of just that node
		if (u.equals(v)) {
			ArrayList<String> path = new ArrayList<String>();
			path.add(u);
			return path;
		}

		// Initialize arrays
		boolean[] S = new boolean[vertices];
		int[] parent = new int[vertices];
		int[] dist = new int[vertices];

		// Set initial values
		for (int i = 0; i < vertices; i++) {
			S[i] = false;
			parent[i] = -1;
			dist[i] = -1;
		}

		// Get the start and end indices
		int start = vertexNameToInt.get(u);
		int end = vertexNameToInt.get(v);
		LinkedList<Integer> Q = new LinkedList<Integer>();

		// Add the start vertex to the queue, with distance 0
		Q.add(start);
		S[start] = true;
		dist[start] = 0;

		boolean foundEnd = false;

		// Perform BFS until we either found the end or have no nodes left to search
		while (!foundEnd && !Q.isEmpty()) {
			int x = Q.removeFirst();
			for (int y : adjList.get(x)) {
				if (!S[y]) {
					S[y] = true;
					Q.addLast(y);
					parent[y] = x;
					dist[y] = dist[x] + 1;

					if (y == end) {
						foundEnd = true;
					}
				}
			}
		}

		// If we never reached the end, there is no path from u to v
		if (parent[end] == -1) {
			return new ArrayList<String>();
		}

		// Otherwise make the path
		String[] path = new String[dist[end] + 1];
		int curr = end;
		for (int i = dist[end]; i >= 0; i--) {
			path[i] = intToVertexName.get(curr);
			curr = parent[curr];
		}

		return new ArrayList<String>(Arrays.asList(path));
	}

	/**
	 * Returns the distance from vertex u to vertex v.
	 * 
	 * @param u
	 *            The start vertex
	 * @param v
	 *            The end vertex
	 * @return The distance from u to v
	 */
	public int distance(String u, String v) {
		// This is almost identical to shortestPath, but we chose to duplicate the code
		// because the string operations and parent are not needed for distance

		// If the nodes are equal, distance is 0
		if (u.equals(v)) {
			return 0;
		}

		// Initialize arrays
		boolean[] S = new boolean[vertices];
		int[] dist = new int[vertices];

		// Set initial values
		for (int i = 0; i < vertices; i++) {
			S[i] = false;
			dist[i] = -1;
		}

		// Get the start and end indices
		int start = vertexNameToInt.get(u);
		int end = vertexNameToInt.get(v);
		LinkedList<Integer> Q = new LinkedList<Integer>();

		// Add start to the list
		S[start] = true;
		Q.addLast(start);
		dist[start] = 0;

		// Perform BFS
		while (!Q.isEmpty()) {
			int x = Q.removeFirst();
			for (int y : adjList.get(x)) {
				if (!S[y]) {
					S[y] = true;
					Q.addLast(y);
					dist[y] = dist[x] + 1;

					// If we have found the end, we can just return its distance
					if (y == end) {
						return dist[end];
					}
				}
			}
		}

		// We never reach the end, return -1
		return -1;
	}

	/**
	 * Returns the distance from the subset of vertices s and the vertex v.
	 * 
	 * @param s
	 *            The starting subset of vertices
	 * @param v
	 *            The end vertex
	 * @return The distance from s to v
	 */
	public int distance(ArrayList<String> s, String v) {
		// Initialize arrays
		boolean[] S = new boolean[vertices];
		int[] dist = new int[vertices];

		// Set initial values
		for (int i = 0; i < vertices; i++) {
			S[i] = false;
			dist[i] = -1;
		}

		// Find the index of the end vertex
		int end = vertexNameToInt.get(v);
		LinkedList<Integer> Q = new LinkedList<Integer>();

		// For each of the start vertices
		for (String u : s) {
			// Get the index
			int uIndex = vertexNameToInt.get(u);

			// If this happens to be the end vertex, then s contains v and we return 0
			if (uIndex == end)
				return 0;

			// Add u to the queue and mark it as visited with distance 0
			S[uIndex] = true;
			Q.addLast(uIndex);
			dist[uIndex] = 0;
		}

		// Perform BFS
		while (!Q.isEmpty()) {
			int x = Q.removeFirst();
			for (int y : adjList.get(x)) {
				if (!S[y]) {
					S[y] = true;
					Q.addLast(y);
					dist[y] = dist[x] + 1;

					// If we find the end vertex, return distance
					if (y == end) {
						return dist[y];
					}
				}
			}
		}

		// Otherwise we didn't find the end vertex, return -1
		return -1;
	}

	/**
	 * Returns Inf(u) - the influence of the given vertex.
	 * 
	 * @param u
	 *            The vertex to get the influence of
	 * @return The influence of u as determined by the Inf() function
	 */
	public float influence(String u) {
		// This is almost identical to shortestPath, but we chose to duplicate the code
		// because the string operations and parent are not needed for distance

		// Initialize arrays
		boolean[] S = new boolean[vertices];
		int[] dist = new int[vertices];
		float influence = (float)1.0;

		// Set initial values
		for (int i = 0; i < vertices; i++) {
			S[i] = false;
			dist[i] = -1;
		}

		// Get the start index
		int start = vertexNameToInt.get(u);
		LinkedList<Integer> Q = new LinkedList<Integer>();

		// Add start to the list
		S[start] = true;
		Q.addLast(start);
		dist[start] = 0;
		
		float a = (float)0.5;
		int currentDistance = 1;

		// Perform BFS
		while (!Q.isEmpty()) {
			int x = Q.removeFirst();
			for (int y : adjList.get(x)) {
				if (!S[y]) {
					S[y] = true;
					Q.addLast(y);
					dist[y] = dist[x] + 1;
					if (dist[y] > currentDistance) {
						currentDistance = dist[y];
						a *= 0.5;
					}
					influence += a;
				}
			}
		}
		
		return influence;
	}

	/**
	 * Returns Inf(s) the influence of the given set of vertices.
	 * 
	 * @param s
	 *            The set of vertices to get the influence of
	 * @return The influence of the set of vertices as determined by the Inf()
	 *         function
	 */
	public float influence(ArrayList<String> s) {
		// Initialize arrays
		boolean[] S = new boolean[vertices];
		int[] dist = new int[vertices];
		float influence = 0;

		// Set initial values
		for (int i = 0; i < vertices; i++) {
			S[i] = false;
			dist[i] = -1;
		}

		// queue to be used in the BFS
		LinkedList<Integer> Q = new LinkedList<Integer>();

		// For each of the start vertices
		for (String u : s) {
			// Get the index
			int uIndex = vertexNameToInt.get(u);

			// Add u to the queue and mark it as visited with distance 0
			S[uIndex] = true;
			Q.addLast(uIndex);
			dist[uIndex] = 0;
			
			influence++;
		}
		
		float a = (float)0.5;
		int currentDistance = 1;

		// Perform BFS
		while (!Q.isEmpty()) {
			int x = Q.removeFirst();
			for (int y : adjList.get(x)) {
				if (!S[y]) {
					S[y] = true;
					Q.addLast(y);
					dist[y] = dist[x] + 1;
					if (dist[y] > currentDistance) {
						currentDistance = dist[y];
						a *= 0.5;
					}
					influence += a;
				}
			}
		}
		
		return influence;
	}

	/**
	 * Internal node class for use in heaps
	 * @author John Wahlig
	 *
	 */
	public class Node {
		private String value;
		private float key;
		
		/**
		 * Node constructor
		 * @param value 
		 * @param key
		 */
		public Node(String value, float key) {
			this.value = value;
			this.key = key;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public void setKey(float key) {
			this.key = key;
		}
		
		public String getValue() {
			return value;
		}
		
		public float getKey() {
			return key;
		}
	}
	
	/**
	 * Heapifies the given ArrayList, based on the given target node
	 * 
	 * @param maxHeap List to be heapified
	 * @param size Size of the list
	 * @param i Target node to heapify
	 */
	private void heapify(ArrayList<Node> maxHeap, int size, int i) {
		//Target node to heapify
		int largest = i;
		//Left child of target in heap array representation
		int left = 2*i + 1;
		//Right child of target in heap array representation
		int right = 2*i + 2;
		
		//Find which of the three nodes above has the largest key
		if(left < size && maxHeap.get(left).getKey() > maxHeap.get(largest).getKey()) {
			largest = left;
		}
		
		if(right < size && maxHeap.get(right).getKey() > maxHeap.get(largest).getKey()) {
			largest = right;
		}
		
		//If i is not larger than its children, it is swapped with the largest and heapified again
		if(largest != i) {
			Node temp = new Node(maxHeap.get(i).getValue(), maxHeap.get(i).getKey());
			maxHeap.set(i, maxHeap.get(largest));
			maxHeap.set(largest, temp);
			
			heapify(maxHeap, size, largest);
		}
		
	}
	
	/**
	 * Returns a set of k vertices obtained by using the Degree Greedy algorithm.
	 * 
	 * @param k
	 *            The number of vertices to output
	 * @return The top k influential vertices as determined by the Degree Greedy
	 *         algorithm
	 */
	public ArrayList<String> mostInfluentialDegree(int k) {
		ArrayList<Node> maxHeap = new ArrayList<Node>();
		for(int i = 0; i < vertices; i++) {
			Node n = new Node(intToVertexName.get(i), outDegree(intToVertexName.get(i)));
			
			//Set new node at the end of the heap array
			maxHeap.add(n);
			
			//Percolate new node up to maintain max heap
			int j = i;
			while(j != 0 && maxHeap.get((j - 1)/2).getKey() < maxHeap.get(j).getKey()) {
				Node temp = new Node(maxHeap.get(j).getValue(), maxHeap.get(j).getKey());
				maxHeap.set(j, maxHeap.get((j - 1)/2));
				maxHeap.set((j - 1)/2, temp);
				
				j = (i - 1)/2;
			}
		}
		
		ArrayList<String> mostInfluential = new ArrayList<String>();
		int heapSize = vertices;
		
		//Extract maximum value from heapy and heapify
		for(int i = 0; i < k && i < vertices; i++) {
			mostInfluential.add(maxHeap.get(0).getValue());
			maxHeap.set(0, maxHeap.get(heapSize - 1));
			heapSize--;
			heapify(maxHeap, heapSize, 0);
		}
		
		return mostInfluential;
	}

	/**
	 * Returns a set of k vertices obtained by using the Modular Greedy algorithm.
	 * 
	 * @param k
	 *            The number of vertices to output
	 * @return The top k influential vertices as determined by the Modular Greedy
	 *         algorithm
	 */
	public ArrayList<String> mostInfluentialModular(int k) {
		ArrayList<Node> maxHeap = new ArrayList<Node>();
		for(int i = 0; i < vertices; i++) {
			Node n = new Node(intToVertexName.get(i), influence(intToVertexName.get(i)));
			
			//Set new node at the end of the heap array
			maxHeap.add(n);
			
			//Percolate new node up to maintain max heap
			int j = i;
			while(j != 0 && maxHeap.get((j - 1)/2).getKey() < maxHeap.get(j).getKey()) {
				Node temp = new Node(maxHeap.get(j).getValue(), maxHeap.get(j).getKey());
				maxHeap.set(j, maxHeap.get((j - 1)/2));
				maxHeap.set((j - 1)/2, temp);
				
				j = (i - 1)/2;
			}
		}
		
		ArrayList<String> mostInfluential = new ArrayList<String>();
		int heapSize = vertices;
		
		//Extract maximum value from heap and heapify
		for(int i = 0; i < k && i < vertices; i++) {
			mostInfluential.add(maxHeap.get(0).getValue());
			maxHeap.set(0, maxHeap.get(heapSize - 1));
			heapSize--;
			heapify(maxHeap, heapSize, 0);
		}
		
		return mostInfluential;
	}

	/**
	 * Returns a set of k vertices obtained by using the SubModular Greedy algorithm
	 * 
	 * @param k
	 *            The number of vertices to output
	 * @return The top k influential vertices as determined by the SubModular Greedy
	 *         algorithm
	 */
	public ArrayList<String> mostInfluentialSubModular(int k) {
		//Set S in algorithm
		ArrayList<String> S = new ArrayList<String>();
		
		//Keep adding vertices to S until it is of size k
		while((S.size() < k) && (S.size() < vertices)) {
		
			//New dummy node to hold value of next vertex to be added to S
			//key will be the influence that S has when this node is added to it
			Node nextMostInfluential = new Node("", 0);
			
			//Iterate through all vertices not in k to find which is best choice
			for(int i = 0; i < vertices; i++) {
				
				//We only care about vertices not in S
				if(!S.contains(intToVertexName.get(i))) {
					
					//Temporarily add a vertex to S to see how it improves the influence of S
					S.add(intToVertexName.get(i));
					if(influence(S) > nextMostInfluential.getKey()) {
						nextMostInfluential.setValue(intToVertexName.get(i));
						nextMostInfluential.setKey(influence(S));
					}
					
					//Now remove the vertex from S to allow other nodes to be tested
					S.remove(intToVertexName.get(i));
				}
			}
			
			//The vertex with the highest added influence to S is added to S
			S.add(nextMostInfluential.getValue());
		}
		
		return S;
	}

}
