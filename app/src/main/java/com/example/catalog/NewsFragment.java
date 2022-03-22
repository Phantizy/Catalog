package com.example.catalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment {

    // Newsfeed properties
    ArrayList<String>newsList = new ArrayList<String>();
    ArrayList<String>newsDescriptions = new ArrayList<String>();
    ArrayList<String>newsUrls = new ArrayList<String>();

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Assemble news feed URL from search terms *saves info*
        SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        String searchTerms = SP.getString("search_terms", "");

        try{
            String rssSourceUrl =
                    "https://news.google.com/news/feeds?q="+
                            URLEncoder.encode(searchTerms, "UTF-8");
            // Use an AsyncTask to load the news list off of the main UI thread
            Log.i("Learning", "News URL is " + rssSourceUrl);
            new asyncNewsLoad().execute(rssSourceUrl);
        }catch(UnsupportedEncodingException e){
            // Encoding Error
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    class asyncNewsLoad extends AsyncTask<String, Void, List<RSSItems>> {
        @Override
        protected List<RSSItems> doInBackground(String... strings) {
            // Load the list with the news feed
            Log.i("Learning", "Background task started to download newsfeed");
            RSSParser parser = null;

            try {
                String newsURL = strings[0];
                parser = new RSSParser(newsURL);
            }catch (Throwable t){
                Log.i("Learning", t.getMessage());
            }

            // Parse the newsfeed
            List<RSSItems> result = parser.parse();
            return result;
        }

        @Override
        protected void onPostExecute(List<RSSItems> rssItems) {
            // this method runs on the UI thread; we can update the ListView here
            int newsItemsCount = 0;
            try{
                newsItemsCount = rssItems.size();
                if (newsItemsCount >0){
                    Log.i("Learning", "Parsing " + newsItemsCount + " news items");

                    // Populate the list of news items
                    for (int n = 0; n < newsItemsCount; n++){
                        final RSSItems newsItems = rssItems.get(n);
                        newsList.add(newsItems.getTitle());
                        newsDescriptions.add(newsItems.getDescription());
                        newsUrls.add(newsItems.getLink().toString());
                        Log.i("Learning", "NEWS ITEM " + n + ") " +
                                newsItems.getTitle());
                    }

                    // newsList contains the list of news headlines
                    // newsDescription contains article description
                    // newsURLs contains the web addresses of the articles

                    ArrayAdapter<String> listAdapter =
                            new ArrayAdapter<String>(
                                    getActivity(),
                                    R.layout.simple_row,
                                    newsList
                            );

                    ListView newsListView = getActivity().findViewById(R.id.newsList);

                    // set click handler
                    newsListView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                                    showNewsItem(i); // i gets the index number of the news items
                                }
                            }
                    );

                    newsListView.setAdapter(listAdapter);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "News Feed Not Found", Toast.LENGTH_SHORT).show();
                }
            }catch (Throwable t){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Unable to load news",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // New news page selected for preview by user
    private void showNewsItem(int i) {
        // user clicked to show news items
        String thisUrl = newsUrls.get(i);

        // Read preferences to determine if it wants to use an external browser
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useBrowser = SP.getBoolean("use_browser", false); // comes from settings

        // pass url out to external browser app on the device
        if(useBrowser){
            Toast.makeText(getContext(),
                    "Loading news in external browser",
                    Toast.LENGTH_SHORT).show();
            String newsItemUrl = thisUrl;
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(newsItemUrl)
            );
            startActivity(intent);
        }else{
            // Load the internal news reader WebView with the selected URL
            Toast.makeText(getContext(), "Loading news......", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("com.example.NewsReader");
            intent.putExtra("newsItemUrl", thisUrl);
            startActivity(intent);
        }
    }


}