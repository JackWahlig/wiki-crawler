import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class NetworkInfluenceTest {
	@Test
	public void BasicGraphTest() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file.txt");
		
		assertEquals(NI.outDegree("A"), 2);
		assertEquals(NI.outDegree("B"), 1);
		assertEquals(NI.outDegree("C"), 3);
		assertEquals(NI.outDegree("D"), 1);
		assertEquals(NI.outDegree("J"), 0);
	}
	
	@Test
	public void BasicBFSTest() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file.txt");
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("C");
		expected.add("A");
		expected.add("B");
		assertEquals(NI.shortestPath("C", "B"), expected);
		assertEquals(NI.distance("C", "B"), 2);
		
		expected.clear();
		expected.add("A");
		expected.add("D");
		assertEquals(NI.shortestPath("A", "D"), expected);
		assertEquals(NI.distance("A", "D"), 1);
		
		expected.clear();
		expected.add("A");
		assertEquals(NI.shortestPath("A", "A"), expected);
		assertEquals(NI.distance("A", "A"), 0);
		
		expected.clear();
		Collections.addAll(expected, "Q", "M", "K", "L", "I", "E", "J");
		assertEquals(NI.shortestPath("Q", "J"), expected);
		assertEquals(NI.distance("Q", "J"), 6);
		
		expected.clear();
		Collections.addAll(expected, "Q", "M", "K", "N", "P");
		assertEquals(NI.shortestPath("Q", "P"), expected);
		assertEquals(NI.distance("Q", "P"), 4);
		
		expected.clear();
		assertEquals(NI.shortestPath("A", "Q"), expected);
		assertEquals(NI.distance("A", "Q"), -1);
	}
	
	@Test
	public void BasicBFSFromSetTest() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file.txt");
		
		ArrayList<String> startVertices = new ArrayList<String>();
		startVertices.add("A");
		startVertices.add("D");
		assertEquals(NI.distance(startVertices, "C"), 1);
		assertEquals(NI.distance(startVertices, "B"), 1);
		assertEquals(NI.distance(startVertices, "A"), 0);
		
		startVertices.clear();
		startVertices.add("D");
		startVertices.add("C");
		assertEquals(NI.distance(startVertices, "A"), 1);
		assertEquals(NI.distance(startVertices, "B"), 2);
		
		startVertices.clear();
		Collections.addAll(startVertices, "A", "H");
		assertEquals(NI.distance(startVertices, "J"), 3);
		assertEquals(NI.distance(startVertices, "G"), 3);
		assertEquals(NI.distance(startVertices, "D"), 1);
		assertEquals(NI.distance(startVertices, "Q"), -1);
	}
	
	@Test
	public void IndividualInfluence() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file.txt");
		
		assertEquals(NI.influence("A"), 2.828125, 0.000001);
	}
	
	@Test
	public void mostInfluentialDegreeTest() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file.txt");
		
		ArrayList<String> result = NI.mostInfluentialDegree(4);
		//System.out.println(result.toString());
		
		assertTrue(result.contains("C"));
		assertTrue(result.contains("I"));
		assertTrue(result.contains("F"));
		assertTrue(result.contains("N"));
		assertEquals(result.size(), 4);
	}
	
	@Test
	public void mostInfluentialModularTest() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file.txt");
		
		ArrayList<String> result = NI.mostInfluentialModular(7);
		//System.out.println(result.toString());
		
		assertTrue(result.contains("C"));
		assertTrue(result.contains("I"));
		assertTrue(result.contains("F"));
		assertTrue(result.contains("H"));
		assertTrue(result.contains("K"));
		assertTrue(result.contains("L"));
		assertTrue(result.contains("N"));
		assertEquals(result.size(), 7);
	}
	
	@Test
	public void mostInfluentialSubModularTest() throws FileNotFoundException {
		NetworkInfluence NI = new NetworkInfluence("test_graph_file2.txt");
		
		ArrayList<String> result = NI.mostInfluentialSubModular(3);
		System.out.println(result.toString());
		
		assertTrue(result.contains("C"));
		assertTrue(result.contains("D"));
		assertTrue(result.contains("A"));
		assertEquals(result.size(), 3);
	}
}
