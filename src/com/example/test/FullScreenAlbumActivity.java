package com.example.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class FullScreenAlbumActivity extends Activity {

	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_album);
		
		mViewPager = (ViewPager) findViewById(R.id.fullScreenAlbumPager);
		FullScreenAlbumAdapter adapter = new FullScreenAlbumAdapter();
		mViewPager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_screen_album, menu);
		return true;
	}
	
	
	
	private class FullScreenAlbumAdapter extends PagerAdapter {
		
		ArrayList<String> mImageUrls;
		
		public FullScreenAlbumAdapter() {
	        mImageUrls = new ArrayList<String>();
	        mImageUrls.add("http://www.foodnut.com/i/Yung-Kee-Restaurant-Hong-Kong/Yung-Kee-Restaurant-Hong-Kong-1.jpg");
	        mImageUrls.add("http://www.12hk.com/area/Admiralty/LippoCtr_PHOT0582.jpg");
	        mImageUrls.add("http://www.eclectic-cool.com/wp-content/uploads/2011/04/hong-kong-street-signs.jpg");
	        mImageUrls.add("http://www.discoverhongkong.com/common/images/hotel/1315_image_COMP00050993_photo_1.jpg");
	        mImageUrls.add("http://annatam.com/wp-content/uploads/2013/12/cf7434fef207fe283c95be624f5db1b5.jpg");
	        mImageUrls.add("http://therakeonline.com/wp-content/uploads/2012/09/Where-The-Rake-Stays-Upper-House-Hong-Kong-Semi-private-booth.jpg");
	        mImageUrls.add("http://farm4.staticflickr.com/3255/2460544629_e3fa24bb40_o.jpg");
	        mImageUrls.add("http://www.yuantravel.com/wp-content/gallery/general-about-hong-kong/hongkong_2141.jpg");
	        mImageUrls.add("https://pbs.twimg.com/media/BbZpkd6CEAAB2GL.jpg");
	        mImageUrls.add("https://pbs.twimg.com/media/BbhSydpCAAAU-J5.jpg");
	        mImageUrls.add("http://fc04.deviantart.net/fs51/i/2009/307/a/3/Hong_Kong__Tallest_Building_by_thehardheadedsavior.jpg");
	    }

		@Override
		public int getCount() {
			return mImageUrls.size();
		}
 
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (RelativeLayout) object;
		}
		
		@Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        Button btnClose;
	        PhotoView photoView;
	  
	        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View viewLayout = inflater.inflate(R.layout.view_fullscreen_image, container, false);
	        
//	        btnClose = (Button) viewLayout.findViewById(R.id.fullScreenImageCloseButton);
	        photoView = (PhotoView) viewLayout.findViewById(R.id.fullScreenImagePhotoView);
	        
			try {
				URL imageUrl = new URL(mImageUrls.get(position));
				photoView.setImageURL(imageUrl, true, null);
			}
			catch(MalformedURLException mfe){}
		    catch(IOException ioe){}
	         
	        // close button click event
//	        btnClose.setOnClickListener(new View.OnClickListener() {
//	            @Override
//	            public void onClick(View v) {
//	                finish();
//	            }
//	        });
	  
	        ((ViewPager) container).addView(viewLayout);
	  
	        return viewLayout;
		}
		
		@Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);
		}
		
	}

}
