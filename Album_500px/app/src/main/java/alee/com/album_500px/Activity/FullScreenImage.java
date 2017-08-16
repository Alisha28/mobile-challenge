package alee.com.album_500px.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import alee.com.album_500px.Adapter.FullScreenImageAdapter;
import alee.com.album_500px.Model.Image;
import alee.com.album_500px.R;

public class FullScreenImage extends Activity {

    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        //launching activity with animation
        overridePendingTransition(R.anim.appearance_go_in, R.anim.appearance_go_out);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent intent = this.getIntent();
        int position = intent.getIntExtra("position", 0);
        ArrayList<Image> images = (ArrayList<Image>) getIntent().getSerializableExtra("images");

        adapter = new FullScreenImageAdapter(FullScreenImage.this, images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);         // displaying selected image first
    }

    //Sending current item position back to ActivityResult
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("pointer", viewPager.getCurrentItem());
        setResult(Activity.RESULT_OK, intent);
        finishMyActivity();
    }

    /**
     * Finishing activity
     */
    public void finishMyActivity() {
        finish();
        overridePendingTransition(R.anim.appearance_back_in, R.anim.appearance_back_out);
    }
}
