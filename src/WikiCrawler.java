import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * WikiCrawler class
 * 
 * Implements a web crawler that analyzes wikipages and builds a graph based on
 * the links in them. Uses a combination of the base URL and a seed URL for the
 * starting page, and looks for keywords that are given in topics.
 * 
 * @author Jacob Feddersen, Luke Heilman, John Wahlig
 *
 */
public class WikiCrawler {

	// the base URL address that is prefixed to the given seed URL
	// static final String BASE_URL = "http://web.cs.iastate.edu/~pavan";
	static final String BASE_URL = "https://en.wikipedia.org";

	// holds the seed URL
	private String seedUrl;
	// holds the maximum number of pages to visit
	private int max;
	// list of the keywords to look for
	private ArrayList<String> topics;
	// file path and name of the output file
	private String fileName;
	// variable to hold number of requests made to pages
	private int requests;

	/**
	 * Constructs a new WikiCrawler with the given seedURL. It will look for
	 * keywords listed in topics and output the graph into the file given by
	 * fileName.
	 * 
	 * @param seedUrl
	 *            The URL to the wikipage that the crawler will start on
	 * @param max
	 *            The maximum number of sites the wikicrawler will visit
	 * @param topics
	 *            List of keywords the crawler will look for
	 * @param fileName
	 *            The file path and name of the txt file the crawler will output to
	 */
	public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName) {
		// remove "/wiki/" from each url during processing to speed it up
		Scanner sc = new Scanner(seedUrl);
		sc.findInLine("/wiki/");
		this.seedUrl = sc.nextLine();
		sc.close();
		this.max = max;
		this.topics = topics;
		this.fileName = fileName;
		// requests used to follow politeness policy
		requests = 0;
	}

	/**
	 * Constructs a directed graph of the wikipages the WikiCrawler is assigned to.
	 * The constructed graph only contains pages that contain all of the keywords
	 * stored in topics.
	 */
	public void crawl() throws IOException, InterruptedException {
		// the queue for the BFS search - will only store pages that meet the topics
		// requirements
		LinkedList<VertexWithLinks> queue = new LinkedList<VertexWithLinks>();
		// vertices used to construct the graph
		HashSet<VertexWithLinks> vertices = new HashSet<VertexWithLinks>();
		// visited set for the BFS search
		HashSet<String> visited = new HashSet<String>();

		// scan the seedUrl
		PageScanner firstScanner = new PageScanner(BASE_URL + "/wiki/" + seedUrl);
		ArrayList<String> seedLinks;

		// check requests number before reading from a page
		bufferRequests();
		// only add to the queue if topics requirement is met
		if ((seedLinks = firstScanner.getLinks(topics)) != null) {
			queue.add(new VertexWithLinks(seedUrl, seedLinks));
			vertices.add(new VertexWithLinks(seedUrl, seedLinks));
			visited.add(seedUrl);
		}

		// main loop of the BFS - this does run even after all max vertices are visited
		while ((!queue.isEmpty()) && vertices.size() < max) {
			// remove page from front of queue
			VertexWithLinks currentPage = queue.remove();

			// loop over each link in the currentPage - note this also checks if less than
			// max pages are in visited
			for (String link : currentPage.links) {
				if (vertices.size() == max) {
					break;
				}

				// if it hasn't been visited
				if (!visited.contains(link)) {

					// check if it contains all the topics
					PageScanner testScanner = new PageScanner(BASE_URL + "/wiki/" + link);
					ArrayList<String> temp;
					// check requests number before reading from a page
					bufferRequests();
					if ((temp = testScanner.getLinks(topics)) != null) {
						// if it does, add to the queue and visited
						queue.add(new VertexWithLinks(link, temp));
						vertices.add(new VertexWithLinks(link, temp));
						visited.add(link);
					}
				}
			}

		}
		// after the BFS, construct the graph with the found vertices and their edges
		constructGraph(vertices);
	}

	/**
	 * Checks how many page requests have been made. Sleeps before making a 26th
	 * request. Should be called before reading from a page.
	 */
	private void bufferRequests() throws InterruptedException {
		// sleep and reset requests if 25 have been made
		if (requests == 25) {
			requests = 0;
			System.out.println("Sleeping...");
			Thread.sleep(3000);
		}
		// increase the number of requests
		requests++;
	}

	/**
	 * This method actually prints the graph to the file, given the vertices and
	 * their edges.
	 * 
	 * @param vertices
	 *            The set of vertices in the graph
	 * @param edges
	 *            The set of edges in the graph
	 */
	private void constructGraph(HashSet<VertexWithLinks> pages) throws IOException {
		long startGraph = System.nanoTime();

		// writer used to print the graph
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");

		VertexWithLinks[] pagesArray = pages.toArray(new VertexWithLinks[pages.size()]);

		// clear the file
		writer.write(pagesArray.length + System.getProperty("line.separator"));

		// for each node
		for (int i = 0; i < pagesArray.length; i++) {
			// for each node
			for (int j = 0; j < pagesArray.length; j++) {
				if (i != j) {
					// if the nodes are different, and there is a link between them, add it to the
					// graph
					if (pagesArray[i].hashedLinks.contains(pagesArray[j].vertex)) {
						// have to add "/wiki/" back to links for the output
						writer.write("/wiki/" + pagesArray[i].vertex + " " + "/wiki/" + pagesArray[j].vertex
								+ System.getProperty("line.separator"));
					}
				}
			}
		}

		writer.close();
		long graphEnd = System.nanoTime();

		double crawlElapsed = (graphEnd - startGraph) / 1000000000.0;
		System.out.printf("Time to construct graph: %f seconds%n", crawlElapsed);

	}

	/**
	 * Private internal class to couple a page (stored as a string) with its links
	 * (stored as a ArrayList of strings). Mainly used by the queue in the BFS
	 * search in WikiCrawler.
	 *
	 */
	private class VertexWithLinks {

		// the page itself
		private String vertex;
		// the links in the page
		private ArrayList<String> links;
		// copy of the links ArrayList in a hash set to decrease time of using
		// contains()
		private HashSet<String> hashedLinks;

		/**
		 * Constructs a new VertexWithLinks object. Couples the vertex and links
		 * together.
		 * 
		 * @param vertex
		 *            The name of the page
		 * @param links
		 *            The list of links on the page
		 */
		private VertexWithLinks(String vertex, ArrayList<String> links) {
			this.vertex = vertex;
			this.links = links;
			hashedLinks = new HashSet<String>(links);
		}
	}

}
