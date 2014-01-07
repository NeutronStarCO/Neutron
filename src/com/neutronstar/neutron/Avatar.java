package com.neutronstar.neutron;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class Avatar extends Activity{
	
	private ImageView ivAvatar;
	private Matrix matrix = new Matrix();
	private static int mScreenWidth;
	private static int mScreenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_avatar);		
		ivAvatar = (ImageView)findViewById(R.id.user_avatar_id);
		
		Bundle bl = this.getIntent().getExtras();
		Bitmap avatar = bl.getParcelable("avatar");
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		
		int width = avatar.getWidth();
		int height = avatar.getHeight();
		
		float scaleWidth = ((float)mScreenWidth) / width;
		float scaleHeight = ((float)mScreenHeight) / height;
		
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bit = Bitmap.createBitmap(avatar, 0, 0, width, height, matrix, true);
		BitmapDrawable bd = new BitmapDrawable(bit);
		bd.setDither(true);
		ivAvatar.setScaleType(ScaleType.FIT_CENTER);
		ivAvatar.setImageDrawable(bd);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

}
