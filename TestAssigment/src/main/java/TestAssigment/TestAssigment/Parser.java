package TestAssigment.TestAssigment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Unfortunately, due to stated time limit I have not managed my program to work with dynamic web pages. So, in this particular case it works 
 * with 16 elements loaded instantly after entering the main website page only. However, this functionality can be implemented in different ways
 * such as via selenium java framefork as an example.
 */


public class Parser {
    
	/*
	 * Function getPage() takes url as a parameter, parse provided website and returns it's html code
	 */
	private static Document getPage(String url) throws MalformedURLException, IOException {
		Document page = Jsoup.parse((new URL(url)), 3000 * 30);
		return page;
	}
	
	public static void main(String[] args) throws MalformedURLException, IOException, NoSuchMethodException, FileNotFoundException {
		int counter = 0;
    	int httpCounter = 1;
		
    	/*
    	 * written code below is responsible for obtaining containers with information on the main page of the website
    	 */
    	Document page = getPage("https://www.aboutyou.de/c/maenner/bekleidung-20290");
    	Element container = page.select("div[class=ReactVirtualized__Grid__innerScrollContainer]").first();
    	Elements blocks = container.select("a[data-test-id=ProductTile]");
    	
    	/*
    	 * here is a printwriter to fill in a csv file with all necessary information regarding company's projects
    	 */
    	try	(PrintWriter csvWriter = new PrintWriter(new File("new.csv"))) {
    	
    	/*
    	 * this for each loop goes over all elements provided directly from the website
    	 */
    	for(Element x : blocks) {	
    		StringBuilder sb1 = new StringBuilder();
    		
    		/*
    		 * here program obtains link of a specific product and follows the link to separately gain all required information about goods such as: Product name, brand, price, etc....
    		 */
    		String strURL = x.attr("href").toString();
    		Document pg1 = getPage("https://www.aboutyou.de" + strURL);
    		Element blockContainer = pg1.select("div[data-test-id=BuyBox]").first();
    		
    		Element productNameAndBrandContainer = blockContainer.select("div").get(2);
    		String productName = productNameAndBrandContainer.select("div").get(2).html();
    		sb1.append(productName);
    		sb1.append(';');
    		
    		String brand = productNameAndBrandContainer.select("div").get(1).select("img").attr("alt");
    		sb1.append(brand);
    		sb1.append(';');
    		
    		Element colorTMP = blockContainer.select("div[data-test-id=ColorVariantHeadline]").next().first();
    		String colors = colorTMP.select("span[data-test-id=ColorVariantColorInfo]").html();
    		colors = colors.replaceAll("[\r\n]+", " ");
    		sb1.append(colors);
    		sb1.append(';');
    		
    		String priceTMP = blockContainer.select("span[data-test-id=ProductPriceCampaignWithoutSale], span[data-test-id=ProductPriceCampaignWithSale],"
    				+ "span[data-test-id=ProductPriceFormattedBasePrice], span[data-test-id=FormattedSalePrice]").first().html();
    		sb1.append(priceTMP);
    		sb1.append(';');
    		
    		String[] bits = strURL.split("-");
    		String articleID = bits[bits.length-1];
    		sb1.append(articleID);
    		sb1.append(';');
    		sb1.append('\n');
    		
    		csvWriter.write(sb1.toString());
    		counter++;
    		httpCounter++;
    		}
    	}
    	System.out.println("Amount of triggered HTTP requests: " + counter);
		System.out.println("Amount of extracted products: " + httpCounter);
    }
}
