package com.example.catalog;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RSSItems {

    static SimpleDateFormat FORMATTER =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    private String title;
    private URL link;
    private String description;
    private Date date;

    // getter and setter for Title
    public void setTitle(String title) {
        this.title = title.trim();
    }
    public String getTitle(){return title;}

    // getter and setter for Link
    public void setLink(String link) {
        try{
            this.link = new URL(link);
        }catch (MalformedURLException e){
            throw new RuntimeException();
        }
    }
    public URL getLink(){return link;}

    public void setDescription(String description) {
        this.description = description.trim();
    }
    public String getDescription(){return description;}

    public void setDate(String date) {
        while (!date.endsWith("00")) date += "0";
        try{
            this.date = FORMATTER.parse(date.trim());
        }catch (ParseException e){
            throw new RuntimeException();
        }
    }
    public String getDate(){return FORMATTER.format(this.date);}

    public RSSItems copy() {
        RSSItems copy = new RSSItems();
        copy.title = title;
        copy.link = link;
        copy.description = description;
        copy.date = date;
        return copy;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + title + "\n");
        sb.append("Date: " + this.getDate() + "\n");
        sb.append("Link: " + link + "\n");
        sb.append("Description: " + description + "\n");
        return sb.toString();
    }
}
