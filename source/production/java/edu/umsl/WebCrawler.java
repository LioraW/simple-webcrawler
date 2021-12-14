package edu.umsl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WebCrawler {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
//
//        System.out.println("Enter a Wikipedia URL such as https://en.wikipedia.org/wiki/NASA:");
//        String url = input.nextLine();
//
//        while (!url.trim().startsWith("https://en.wikipedia.org")) {
//            System.out.println("That is not a wikipedia link! Please enter a wikipedia link: ");
//            url = input.nextLine();
//        }

        String url = "https://m.fanfiction.net/s/10847788/1/Goldstein";
        crawler(url);
    }

    public static void crawler (String urlString) {

        int chapterNum = 1;

        final int LAST_CHAP = 2;

        //go through the list of pending urls
        while (chapterNum < LAST_CHAP) {
                try {
                    Thread.sleep(1000); //To avoid DDOS Throws InterruptException

                    Document doc = Jsoup.connect(urlString)
                            .referrer("https://m.fanfiction.net//eye/2/1/37370795/10847788/1/Goldstein")
                            .cookie("__gads=ID=5c6f4395223fb4b6-223d802dbec9003b:T=1627270808:RT=1627270808:S=ALNI_MZigAEC7YRsorVVQJWXB7S3nNRvrg; xcookie2=%7B%22gui_font%22%3A%22Verdana%22%2C%22read_font%22%3A%22Verdana%22%2C%22read_font_size%22%3A1%2C%22read_theme%22%3A%22light%22%2C%22read_line_height%22%3A1.25%2C%22read_width%22%3A100%2C%22read_light_texture%22%3A%22%22%2C%22read_dark_texture%22%3A%22%22%7D; __cf_bm=8068781f680e0a70fe54fdb4428f011a204031fb-1627426655-1800-AZxwjhfpFDD0RM5UfVSeYeqYe8B4PkyxxxogB8lqEMHTkWVZT65W9c9cs5e06xJqOiXVg5HPEpSCgeW1Nf+sSZ9TylOkLmE8whUV+e8Db6s3kN/52z9Xx6bca9wgYz0AMg==","")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                            .get(); //throws IOException

                    System.out.println(doc.title() + "\n");

                    System.out.println(getWords(doc).toString());

                    //get next url
                    chapterNum++;
                    urlString = "https://m.fanfiction.net/s/10847788/" + chapterNum + "/Goldstein";


                } catch (IllegalArgumentException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    return;
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }

    }

    /* Returns a list of the lines in the chapter */
    public static ArrayList<String> getWords (Document document) {
        ArrayList<String> lines = new ArrayList<>();

        Element storyText = document.getElementById("storytext");

        for (Element element : storyText.getAllElements()) {
            lines.add(element.ownText());
        }

        return lines;
    }

}
