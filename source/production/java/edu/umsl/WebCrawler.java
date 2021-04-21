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
        System.out.print("Enter a URL:");
        String url = input.nextLine();
        crawler(url);
    }

    public static void crawler (String startingURL) {
        final int MAX = 100;

        ArrayList<String> listOfPendingURLs = new ArrayList<>();
        HashSet<String> listOfTraversedURLs = new HashSet<>();
        HashSet<String> titles = new HashSet<>();
        HashMap<String, Integer>  wordCounts = new HashMap<>();


        listOfPendingURLs.add(startingURL);

        //go through the list of pending urls
        while (!listOfPendingURLs.isEmpty() && listOfTraversedURLs.size() <= MAX) {
            //sleep a bit
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex){
                //do nothing
            }

            String urlString = listOfPendingURLs.remove(0);

            if (!listOfTraversedURLs.contains(urlString)) {
                listOfTraversedURLs.add(urlString);

                try {
                    Document doc = Jsoup.connect(urlString).get(); //try to connect

                    String title = doc.title();
                    System.out.println(title);

                    System.out.println("\t" + urlString);
                    System.out.println("\t" + getWordCount(doc));

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

    public static ArrayList<String> getWords (Document document) {
        ArrayList<String> wordList = new ArrayList<>();

        Elements paragraphs = document.select("p");

        for (Element paragraph : paragraphs) {
            String words  = paragraph.ownText();
            //System.out.println(words);
            wordList.addAll(Arrays.asList(words.split(" ")));
        }
        return wordList;
    }
    public static HashMap<String, Integer> getWordCount (Document doc) {
        ArrayList<String> wordList = getWords(doc);
        HashMap<String, Integer>  wordCounts = new HashMap<>();

        //Add the array of words to the map
        for (String s : wordList) {
            if (wordCounts.containsKey(s)) {
                int newValue = Integer.parseInt(String.valueOf(wordCounts.get(s)));
                newValue++;
                wordCounts.put(s, newValue);
            } else {
                wordCounts.put(s, 1);
            }
        }
        return wordCounts;
    }

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
