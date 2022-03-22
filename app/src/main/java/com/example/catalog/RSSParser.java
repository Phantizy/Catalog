package com.example.catalog;

import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import org.xml.sax.Attributes;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class RSSParser {

    // names of the Xml tags
    static final String RSS = "rss";
    static final String CHANNEL = "channel";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String TITLE = "title";

    private final URL feedURL;

    public RSSParser(String newsURL) {
        try{
            this.feedURL = new URL(newsURL);
            Log.i("Learning", "Constructing RSSParser for URL " + this.feedURL);
        }catch (MalformedURLException e){
            throw new RuntimeException();
        }
    }

    public List<RSSItems> parse() {
        Log.i("Learning", "Parsing");

        final RSSItems currentMessage = new RSSItems();
        RootElement root = new RootElement(RSS);
        final List<RSSItems> newsItems = new ArrayList<RSSItems>();
        Element itemList = root.getChild(CHANNEL); // getting items from CHANNEL
        Element item = itemList.getChild(ITEM);

        item.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                newsItems.add(currentMessage.copy()); // check RSSItems.java file
            }
        });

        item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Log.i("Learning", "Parsing article " + body);
                currentMessage.setTitle(body);
            }
        });

        item.getChild(LINK).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                currentMessage.setLink(body);
            }
        });

        item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                currentMessage.setDescription(body);
            }
        });

        item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                currentMessage.setDate(body);
            }
        });

        try {
            URLConnection urlConnection = feedURL.openConnection();
            InputStream stream = urlConnection.getInputStream();
            Xml.parse(stream, Xml.Encoding.UTF_8, root.getContentHandler());

        }catch (Exception e){
            // throw new RuntimeException
            Log.i("Learning", "Runtime error" + e.getLocalizedMessage());
        }


        return newsItems;
    }
}
