package com.neutronstar.neutron;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Avatar extends Activity{
	
	private ImageView ivAvatar;
	private Matrix matrix = new Matrix();
	private static int mScreenWidth;
	private static int mScreenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_avatar);
		ivAvatar = (ImageView)findViewById(R.id.user_avatar_id);
		
//		ivAvatar.setImageDrawable(getResources().getDrawable(R.drawable.xiaohei_big));
		
		
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
		bd.setTileModeXY(TileMode.CLAMP, TileMode.CLAMP);
		bd.setDither(true);
		ivAvatar.setImageDrawable(bd);
//		ivAvatar.setImageBitmap(avatar);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

}
