package Assignment3.Firstapp;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.MapView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Random;

import Assignment3.Firstapp.Stock.Stock;
import Assignment3.Firstapp.Weather.WeatherAPI;
import org.json.*;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.soundCloud);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.loadUrl("file:///android_asset/SoundCloudEmbed.html");

        //weather
        final WeatherAPI weatherAPI = new WeatherAPI(this);
        final TextView weatherHere = findViewById(R.id.weatherHere);
        weatherAPI.requestWeatherData("lowell", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                weatherHere.setText("Lowell - " + weatherAPI.parseResponse(response));
            }
        });

        final TextView weatherHome = findViewById(R.id.weatherHome);
        weatherAPI.requestWeatherData("nashua", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                weatherHome.setText("Nashua - " + weatherAPI.parseResponse(response));
            }
        });

        final TextView weatherHawaii = findViewById(R.id.weatherHawaii);
        weatherAPI.requestWeatherData("honolulu", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                weatherHawaii.setText("Honolulu - " + weatherAPI.parseResponse(response));
            }
        });


        //resume button
        File resumeFileCopy = createExternalAssetCopy("A_Meier_UML_2020-Oct.pdf");

        final Uri resumeURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", resumeFileCopy);

        final Button resumeButton = findViewById(R.id.openResume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent openPDF = new Intent(Intent.ACTION_VIEW);
                openPDF.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                openPDF.setDataAndType(resumeURI, "application/pdf");

                Intent OpenPDFChooserIntent = Intent.createChooser(openPDF, "Open File");
                startActivity(OpenPDFChooserIntent);
            }
        });


        /*
        Stock portfilio tabs
         */
        TabLayout myPortfolio= findViewById(R.id.Portfolio);
        final FrameLayout portfolioFrame = findViewById(R.id.PortfolioFrame);
        myPortfolio.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MyPortfolioFragment portfolioFragment = MyPortfolioFragment.newInstance();
                if(tab.getPosition() == 0){
                    portfolioFragment.addStock(new Stock("GOOGL", "$2000.00"));
                    portfolioFragment.addStock(new Stock("APPL", "$1546.00"));
                    portfolioFragment.addStock(new Stock("WFC", "$20.02"));
                } else if(tab.getPosition() == 1){
                    portfolioFragment.addStock(new Stock("NASDAQ", "$2000.00"));
                    portfolioFragment.addStock(new Stock("MCSFT", "1300.00"));
                    portfolioFragment.addStock(new Stock("WFC", "$20.02"));
                }

                getFragmentManager().beginTransaction().add(portfolioFrame.getId(), portfolioFragment, "Main").commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                MyPortfolioFragment portfolioFragment = MyPortfolioFragment.newInstance();
                if(tab.getPosition() == 0){
                    portfolioFragment.addStock(new Stock("GOOGL", "$2000.00"));
                    portfolioFragment.addStock(new Stock("APPL", "$1546.00"));
                    portfolioFragment.addStock(new Stock("WFC", "$20.02"));
                } else if(tab.getPosition() == 1){
                    portfolioFragment.addStock(new Stock("NASDAQ", "$2000.00"));
                    portfolioFragment.addStock(new Stock("MCSFT", "1300.00"));
                    portfolioFragment.addStock(new Stock("WFC", "$20.02"));

                }
                getFragmentManager().beginTransaction().add(portfolioFrame.getId(), portfolioFragment, "Main").commit();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Change attributes on touch
        Random rand = new Random();
        ConstraintLayout layout =  findViewById(R.id.main_layout);
        layout.setBackgroundColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        return super.onTouchEvent(event);
    }

    /*
    This is a little complicated.  In order to create an intent to read a pdf file I needed to first
    create a copy of the asset as an external file. to do so, I used the asset manager to first obtain
    the pdf asset, and then manually create a copy in the external files directory that I set up a file
    provider for.  this code is based on a stack overflow answer I found
    here https://stackoverflow.com/questions/17085574/read-a-pdf-file-from-assets-folder
     */
    private File createExternalAssetCopy(String assetName) {

        File assetCopy = new File(getExternalFilesDir("external_files"), assetName);
        try {
            //setup
            AssetManager assetManager = getAssets();
            InputStream in = assetManager.open(assetName);
            OutputStream out = new FileOutputStream(assetCopy);

            //make copy
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }

            //cleanup;
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("MAIN", e.toString());
        }

        return assetCopy;
    }
}