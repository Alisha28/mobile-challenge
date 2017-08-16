package alee.com.album_500px.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import alee.com.album_500px.Adapter.GalleryAdapter;
import alee.com.album_500px.App.AppController;
import alee.com.album_500px.Model.Image;
import alee.com.album_500px.R;

import static alee.com.album_500px.Extras.APILinkManager.ADDRESS;
import static alee.com.album_500px.Extras.APILinkManager.CONSUMER_KEY;
import static alee.com.album_500px.Extras.APILinkManager.FEATURE;
import static alee.com.album_500px.Extras.APILinkManager.PAGE;
import static alee.com.album_500px.Extras.APILinkManager.PHOTOS;
import static alee.com.album_500px.Extras.APILinkManager.SIZE;
import static alee.com.album_500px.Extras.Keys.CATAGORY;
import static alee.com.album_500px.Extras.Keys.CREATED_AT;
import static alee.com.album_500px.Extras.Keys.DESCRIPTION;
import static alee.com.album_500px.Extras.Keys.IMAGES;
import static alee.com.album_500px.Extras.Keys.NAME;
import static alee.com.album_500px.Extras.Keys.PHOTO;
import static alee.com.album_500px.Extras.Keys.URL;

public class MainActivity extends Activity {
    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = ADDRESS+PHOTOS+FEATURE+CONSUMER_KEY+SIZE+PAGE;
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    public static int pageCounter = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        //Setting RecyclerView attributes
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // Adding ScrollListener to RecyclerView for loading more items on end of scroll
        recyclerView.addOnScrollListener(new CustomScrollListener());

        //Handling Touch event on RecyclerView for launching full screen activity
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(MainActivity.this, FullScreenImage.class);

                //Passing List of Image Object  and position of current item selected to full screen activity
                intent.putExtra("images", images);
                intent.putExtra("position", position);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        fetchImages();

    }
    /**
     * This method displays a progress dialog prior to making a call to API.
     */
    private void fetchImages() {
        pDialog.setMessage("Downloading Images...");
        pDialog.show();
        callImagesAPI();
    }

    /**
     * Firing Volley Get request for /photos endpoint
     */
    private void callImagesAPI(){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint+pageCounter, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        pDialog.hide();

                        try {
                            if(response.getJSONArray(PHOTO)!=null){
                                parseJSON(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pDialog.hide();

                    }
                });

        // Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

    /**
     * parseJSON method used for parsing the /photos API response.
     * @param response
     * @throws JSONException
     */
    public void parseJSON(JSONObject response) throws JSONException {
        JSONArray photoArray = response.getJSONArray(PHOTO);

        for (int i = 0; i < photoArray.length(); i++) {

            JSONObject object = photoArray.getJSONObject(i);
            Image image = new Image();
            image.setName(object.getString(NAME));
            image.setDescription(object.getString(DESCRIPTION));
            JSONArray image_url = object.getJSONArray(IMAGES);
            image.setSmall(image_url.getJSONObject(1).getString(URL));
            image.setMedium(image_url.getJSONObject(1).getString(URL));
            image.setLarge(image_url.getJSONObject(2).getString(URL));
            image.setTimestamp(object.getString(CREATED_AT));
            //Display photos excluding nude pictures
            if(!((object.getString(CATAGORY)).equalsIgnoreCase("4"))){
                images.add(image);}

        }
        mAdapter.notifyDataSetChanged();

    }

    class CustomScrollListener extends RecyclerView.OnScrollListener {
        CustomScrollListener(){
        }
        public void onScrollStateChanged(RecyclerView recyclerView, int newState){
            switch (newState) {
                //no scrolling in RecyclerView
                case RecyclerView.SCROLL_STATE_IDLE:
                    pageCounter++;
                    callImagesAPI();
                    break;
            }

        }

    }

    // Fetching the position of current item displayed on ViewPager in Full Screen Activity.
// So that Gallery view is auto-scrolled to display that item first.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                final int pointer = data.getIntExtra("pointer",0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(pointer+1);
                    }
                }, 200);
            }
        }
    }
}
