package alee.com.album_500px.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = "https://api.500px.com/v1/photos?consumer_key=fAeci4B1pHssX8LnMK788zTmYsIGD8m41Sn5lcRo&image_size=5,4,3,2&feature=fresh_today&page=1";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

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
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        pDialog.hide();

                        try {
                            if(response.getJSONArray("photos")!=null){
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
        JSONArray photoArray = response.getJSONArray("photos");

        for (int i = 0; i < photoArray.length(); i++) {

            JSONObject object = photoArray.getJSONObject(i);
            Image image = new Image();
            image.setName(object.getString("name"));
            image.setDescription(object.getString("description"));
            JSONArray image_url = object.getJSONArray("images");
            image.setSmall(image_url.getJSONObject(1).getString("url"));
            image.setMedium(image_url.getJSONObject(1).getString("url"));
            image.setLarge(image_url.getJSONObject(2).getString("url"));
            image.setTimestamp(object.getString("created_at"));
            images.add(image);

        }
        mAdapter.notifyDataSetChanged();

    }
}
