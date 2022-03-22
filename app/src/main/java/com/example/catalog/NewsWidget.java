package com.example.catalog;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;


public class NewsWidget extends AppWidgetProvider {
    String searchTerms = "";
    WidgetUpdateInfo widgetUpdateInfo;

    private class WidgetUpdateInfo {
        Context context;
        final AppWidgetManager appWidgetManager;
        int[] appWidgetIds;


        private WidgetUpdateInfo(Context context,
                                 final AppWidgetManager appWidgetManager,
                                 int[] appWidgetIds) {
            this.appWidgetManager = appWidgetManager;
            this.context = context;
            this.appWidgetIds = appWidgetIds;
        }
    }

    public void onUpdate(Context context,
                         final AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        // Assemble news feed from URL search terms
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        searchTerms = SP.getString("search_terms", "");

        try{
            String rssSourceUrl =
                    "https://news.google.com/news/feeds?q="+
                            URLEncoder.encode(searchTerms, "UTF-8");

            // Use an AsyncTask to load the news list off of the main UI thread
            widgetUpdateInfo = new WidgetUpdateInfo(context, appWidgetManager, appWidgetIds);
            new asyncNewsLoad().execute(rssSourceUrl);
        }catch (UnsupportedEncodingException e){

        }
    }

   class asyncNewsLoad extends AsyncTask<String, Void, List<RSSItems>> {
        @Override
        protected List<RSSItems> doInBackground(String... strings){
            // Load the list with the news feed
            Log.i("Learning", "Background Task started to download newsfeed");
            RSSParser parser = null;

            try{
                String newsURL = strings[0];
                parser = new RSSParser(newsURL);

            }catch (Throwable t){
            }
            return parser.parse();
        }

        protected void onPostExecute(List<RSSItems> result){
            String newsHeadLinesText = "";

            // This method runs on the UI thread; we can update the ListView here
            int newsItemsCount = 0;
            try{
                newsItemsCount = result.size();
                if(newsItemsCount > 0){
                    Log.i("Learning", "Parsing"+  newsItemsCount + " news items");

                    // Populate the list of news items
                    for (int n = 0; n < newsItemsCount; n++){
                        final RSSItems newsItem = result.get(n);
                        newsHeadLinesText += "\n" + " ◻ "  + newsItem.getTitle();
                    }
                }
            }catch (Throwable t){
                // Log.e("News Feed", t.getMessage(),t);

            }

            // Update each widget instance with the headlines
            for(int appWidgetID : widgetUpdateInfo.appWidgetIds){
                String widgetText = "(Last Updated: " + getTimeStamp() + ") ";
                widgetText += newsHeadLinesText;

                //Construct the RemoteViews object
                RemoteViews views = new RemoteViews(widgetUpdateInfo.context.getPackageName(),
                        R.layout.widget_news);
                views.setTextViewText(R.id.appwidget_text, widgetText);

                // Instruct the widget manager to update the widget
                widgetUpdateInfo.appWidgetManager.updateAppWidget(appWidgetID, views);
            }
        }
    }

    private String getTimeStamp() {
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        return timeFormat.format(new Date());
    }
}