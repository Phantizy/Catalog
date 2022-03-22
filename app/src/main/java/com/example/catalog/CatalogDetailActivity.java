package com.example.catalog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class CatalogDetailActivity extends AppCompatActivity {

    String theName;
    String theImage;
    String theDescription;
    String thePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_detail);

        Intent intent = getIntent();

        // Get values passed in with the intent
        theName = intent.getExtras().getString("name");
        theImage = intent.getExtras().getString("image");
        theDescription = intent.getExtras().getString("description");
        thePrice = intent.getExtras().getString("price");

        // Set title * design *
        TextView productTitleView = findViewById(R.id.productTitle);
        productTitleView.setText(theName);

        // Set Price
        TextView productPriceView = findViewById(R.id.productPrice);
        productPriceView.setText(thePrice);

        // Set Description
        TextView productDescriptionView = findViewById(R.id.productText);
        productDescriptionView.setText(theDescription);

        // Set Image
        ImageView productImageView = findViewById(R.id.productImage);
        int redID = getResources().getIdentifier(theImage, "drawable", getPackageName());
        productImageView.setImageResource(redID);


        // start image animation
        Animation imageAnimation = AnimationUtils.loadAnimation(this.getBaseContext(), R.anim.picture_anim);

        imageAnimation.setStartOffset(500);
        productImageView.startAnimation(imageAnimation);


    }
}