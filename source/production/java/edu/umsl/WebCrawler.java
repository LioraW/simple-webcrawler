package edu.umsl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.ArrayList;

public class WebCrawler {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a URL:");
        String url = input.nextLine();
        crawler(url);
    }

    public static void crawler (String startingURL) {
        final int MAX = 100;
        ArrayList<String> listOfPendingURLs = new ArrayList<>();
        HashSet<String> listOfTraversedURLs = new HashSet<>();
        HashSet<String> titles = new HashSet<>();

        listOfPendingURLs.add(startingURL);

        //go through the list of pending urls
        while (!listOfPendingURLs.isEmpty() && listOfTraversedURLs.size() <= MAX) {
            //sleep a bit
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex){
                //do nothing
            }

            String urlString = listOfPendingURLs.remove(0);

            if (!listOfTraversedURLs.contains(urlString)) {
                listOfTraversedURLs.add(urlString);

                System.out.println("Crawl " + urlString);
                try {
                    Document doc = Jsoup.connect(urlString).get(); //try to connect
                    String title = doc.title();
                    System.out.println(title);

                    //add the next urls to the list
                    for (String s: getSubURls(doc)) {
                      if (!listOfTraversedURLs.contains(s)){
                          listOfPendingURLs.add(s);
                      }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


            }
        }
    }

    public static ArrayList<String> getSubURls (Document document) {
        ArrayList<String> list = new ArrayList<>();

        Elements links = document.select("a[href]");

        for (Element link : links) {

            if (link.attr("href").startsWith("/wiki/")) { //use only internal wikipedia links
                String fullLinkName = "https://en.wikipedia.org" + link.attr("href");
                list.add(fullLinkName);
                //System.out.println("link : " + fullLinkName);
            }
        }
        return list;
    }

}
