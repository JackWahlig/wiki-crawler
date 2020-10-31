import java.util.ArrayList;

public class TestWikiCrawler {
	
	public static void main(String[] args) {
		
		ArrayList<String> topics = new ArrayList<String>();
		//topics.add("Iowa State");
		//topics.add("Cyclones");
		//topics.add("harry");
		//topics.add("potter");
		//topics.add("stuff");
		//topics.add("1966");
		//topics.add("Wayne");
		//topics.add("blalalala");
		
		
		long crawlStart = System.nanoTime();
		WikiCrawler crawler = new WikiCrawler("/wiki/Complexity_theory", 100, topics, "wiki_crawler_test.txt");
		//WikiCrawler crawler = new WikiCrawler("/wiki/Iowa_State_University", 100, topics, "testeee.txt");
		//WikiCrawler crawler = new WikiCrawler("/wiki/A311.html", 100, topics, "css311_test.txt");
		try {
			crawler.crawl();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		long crawlEnd = System.nanoTime();
		
		double crawlElapsed = (crawlEnd - crawlStart) / 1000000000.0;
		System.out.printf("Total time: %f seconds%n", crawlElapsed);
	}

}
