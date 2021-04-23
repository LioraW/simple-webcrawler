package edu.umsl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WebCrawler {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter a Wikipedia URL such as https://en.wikipedia.org/wiki/NASA:");
        String url = input.nextLine();

        while (!url.trim().startsWith("https://en.wikipedia.org")) {
            System.out.println("That is not a wikipedia link! Please enter a wikipedia link: ");
            url = input.nextLine();
        }

        crawler(url);
    }

    public static void crawler (String startingURL) {
        final int MAX_LINKS = 1000;

        ArrayList<String> listOfPendingURLs = new ArrayList<>();
        HashSet<String> listOfTraversedURLs = new HashSet<>();
        HashMap<String, Integer> wordCountMap = new HashMap<>();

        listOfPendingURLs.add(startingURL);

        //go through the list of pending urls
        while (!listOfPendingURLs.isEmpty() && listOfTraversedURLs.size() <= MAX_LINKS) {
            String urlString = listOfPendingURLs.remove(0);

            if (!listOfTraversedURLs.contains(urlString)) {
                try {
                    Thread.sleep(50); //To avoid DDOS on wikipedia. Throws InterruptException

                    Document doc = Jsoup.connect(urlString).get(); //throws IOException

                    System.out.println(doc.title() + "\n");

                    //merge current doc's word count onto wordCountMap
                    getWordCount(doc).forEach((key, value) -> wordCountMap.merge(key, value, Integer::sum));

                    listOfTraversedURLs.add(urlString);

                    //add the next urls to the list
                    for (String s: getSubURls(doc)) {
                      if (!listOfTraversedURLs.contains(s)) {
                          listOfPendingURLs.add(s);
                      }
                    }
                } catch (IllegalArgumentException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    return;
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
        //print out the sorted total of all words from every page traversed
        System.out.println("\nHere are the totals of each word in every page traversed:");
        System.out.println(new TreeMap<>(wordCountMap)); //sort the words alphabetically and then print them
    }

    /* Returns a map of the number of times each word comes up in the document */
    public static HashMap<String, Integer> getWordCount (Document doc) {
        HashMap<String, Integer>  wordCounts = new HashMap<>();

        for (String s : getWords(doc))
            wordCounts.merge(s, 1, Integer::sum); //if s is a key in wordCounts, value++; else add s with value 1.

        return wordCounts;
    }

    /* Returns a list of the words in the p tags of the document */
    public static ArrayList<String> getWords (Document document) {
        ArrayList<String> wordList = new ArrayList<>();
        Elements paragraphs = document.select("p"); //get all the p tag elements

        for (Element paragraph : paragraphs) {
            String words  = paragraph.ownText();
            wordList.addAll(Arrays.asList(words.split("[^a-zA-Z]+"))); //split on any non-alpha character
        }
        return wordList;
    }

    /* Returns a list of the links to other wikipedia articles in the wikipedia article */
    public static ArrayList<String> getSubURls (Document document) {
        ArrayList<String> list = new ArrayList<>();
        Elements links = document.select("a[href]");

        for (Element link : links) {
            if (link.attr("href").startsWith("/wiki/")) { //use only internal wikipedia links
                String fullLinkName = "https://en.wikipedia.org" + link.attr("href");
                list.add(fullLinkName);
            }
        }
        return list;
    }
}
