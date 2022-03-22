package com.example.catalog;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class CatalogFragment extends Fragment {

    // Parallel arrays to hold product data for quick lookup
    CharSequence[] productName;
    CharSequence[] productDescriptions;
    CharSequence[] productCategories;
    CharSequence[] productPrices;
    CharSequence[] productImages;

    public CatalogFragment() {
        // Required empty public constructor
    }


    public static CatalogFragment newInstance() {
        CatalogFragment fragment = new CatalogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        // Get product data from our resources
        Resources res = getResources();
        // remember arrays are placed in the strings.xml file
        productName = res.getTextArray(R.array.product_names);
        productDescriptions = res.getTextArray(R.array.product_descriptions);
        productCategories = res.getTextArray(R.array.product_categories);
        productPrices = res.getTextArray(R.array.product_prices);
        productImages = res.getTextArray(R.array.product_images);

        // ListView for productList in catalog Fragment
        ListView productListView = view.findViewById(R.id.productList);
        ArrayAdapter<CharSequence> productListAdapter =
                ArrayAdapter.createFromResource(
                        view.getContext(),
                        R.array.product_names,
                        android.R.layout.simple_list_item_1
                );
        productListView.setAdapter(productListAdapter);

        // set the click handler
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showProduct(position); // the array are looking for numbers
            }
        });

        return view;
    }

    private void showProduct(int position) {

        // 1 multi-dimensional array
        // array = {}

        // String[][][][] info = {
        // {} = productNames
        // {} = productImages
        // {} = productDescription
        // {} = productPrices
        // };

        // Pull together data we'll pass off to the product detail view
        String selectedProduct = (String) productName[position];
        String selectedImage = (String) productImages[position];
        String selectedDescription = (String) productDescriptions[position];
        String selectedPrice = (String) productPrices[position];

        // Launch separate activity to display details, passing info through an extra
        Intent intent = new Intent("com.example.CatalogDetailActivity");
        intent.putExtra("name", selectedProduct);
        intent.putExtra("description", selectedDescription);
        intent.putExtra("price", selectedPrice);
        intent.putExtra("image", selectedImage);
        startActivity(intent);
    }

}