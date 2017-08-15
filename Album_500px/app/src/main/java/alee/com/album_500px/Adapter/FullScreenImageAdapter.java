package alee.com.album_500px.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import alee.com.album_500px.Model.Image;
import alee.com.album_500px.R;


public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<Image> _imagePaths;
	private LayoutInflater inflater;

	// constructor
	public FullScreenImageAdapter(Activity activity,
								  ArrayList<Image> imagePaths) {
		this._activity = activity;
		this._imagePaths = imagePaths;
	}


	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
	
	@Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_fullscreen_preview, container, false);


		ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.image_preview);
		TextView des = (TextView) viewLayout.findViewById(R.id.des);
		TextView date_created = (TextView) viewLayout.findViewById(R.id.created_at);

		String name = _imagePaths.get(position).getName();
		Image image = _imagePaths.get(position);
		des.setText(image.getDescription());
		date_created.setText(image.getTimestamp());

		// Check for untitled/null/empty values in name
		if(!name.equalsIgnoreCase("untitled")&&!name.equalsIgnoreCase("null")&&!name.equalsIgnoreCase("")&&name!=null){
		_activity.setTitle(image.getName());
		}
		else{
			_activity.setTitle("N/A");
		}


		//Loading image with Glide
		Glide.with(_activity).load(image.getLarge())
				.crossFade()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(imgDisplay);


        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);
 
    }

}
