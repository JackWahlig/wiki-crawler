import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * PageScanner class
 * 
 * Implements a scanner class that reads and provides useful information on the
 * given web page. This includes returning the list of links on the web page,
 * and whether the web page contains given keywords.
 * 
 * @author Jacob Feddersen, Luke Heilman, John Wahlig
 *
 */
public class PageScanner {

	// the string of the URL this PageScanner is for
	private String Url;

	/**
	 * Constructs a new PageScanner that can read the given web page.
	 * 
	 * @param Url
	 *            The URL of the web page this PageScanner will read - expects the
	 *            full URL
	 */
	public PageScanner(String Url) {
		this.Url = Url;
	}

	/**
	 * Scans the web page of this PageScanner and finds all relevant links contained
	 * in the page. It ignores all links found before a "&ltp&gt" tag and all links
	 * that contain "#" or ":". It also ignores any links that are not in the form
	 * "/wiki/xxx". If the scanned page does not contain all of the elements in
	 * topics, then it returns null instead.
	 * 
	 * @param topics
	 * @return An ArrayList of strings, where each string is the URL of a link,
	 *         minus the base URL
	 */
	public ArrayList<String> getLinks(ArrayList<String> topics) throws IOException {

		int topicsSize = 0;
		if (topics != null) {
			topicsSize = topics.size();
		}
		boolean[] isPresent = new boolean[topicsSize];

		// open the web page and read the content with a BufferedReader
		URL url = new URL(Url);
		InputStream inStream = url.openStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
		// string buffer to hold each line scanned in the page
		String line;
		// boolean to mark if the <p> has been found yet
		boolean start = false;
		// list to hold each valid link that is found
		ArrayList<String> links = new ArrayList<String>();

		// loop through each line in the page
		while ((line = bReader.readLine()) != null) {

			Scanner sc = new Scanner(line);
			// the end of the link URL in the <a></a> ends with a "
			sc.useDelimiter("\"");

			// the <p> tag has not been found
			if (!start) {
				// check if <p> tag is in the line
				if (sc.findInLine("<p>") != null) {
					Scanner tempScanner = new Scanner(line);
					tempScanner.findInLine("<p>");
					String topicsToken = tempScanner.nextLine();
					for (int i = 0; i < topics.size(); i++) {
						if (!isPresent[i] && topicsToken.contains(topics.get(i))) {
							isPresent[i] = true;
						}
					}
					tempScanner.close();
					// if found, set the boolean
					start = true;
					// for each each link found
					while (sc.findInLine("<a href=\"/wiki/") != null) {
						if (sc.hasNext()) {
							// get the link URL
							String linksToken = sc.next();
							// check if it is valid
							if (!(linksToken.contains("#") || linksToken.contains(":"))) {
								links.add(linksToken);
							}
						}
					}
				}
			}
			// the <p> tag has already been found
			else {
				for (int i = 0; i < topics.size(); i++) {
					if (!isPresent[i] && line.contains(topics.get(i))) {
						isPresent[i] = true;
					}
				}
				// for each each link found
				while (sc.findInLine("<a href=\"/wiki/") != null) {
					if (sc.hasNext()) {
						// get the link URL
						String token = sc.next();
						// check if it is valid
						if (!(token.contains("#") || token.contains(":"))) {
							links.add(token);
						}
					}
				}
			}
			sc.close();
		}
		bReader.close();

		// check if every topic is found
		for (int i = 0; i < isPresent.length; i++) {
			if (!isPresent[i]) {
				return null;
			}
		}

		return links;
	}
}