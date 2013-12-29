package com.neutronstar.neutron;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class Avatar extends Activity{
	
	private ImageView ivAvatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avatar);
		ivAvatar = (ImageView)findViewById(R.id.user_avatar_id);
		
		Bundle bl = this.getIntent().getExtras();
		Bitmap avatar = bl.getParcelable("avatar");
		Matrix matrix = new Matrix();
		matrix.postScale(2.0f, 2.0f);
		Bitmap bit = Bitmap.createBitmap(avatar, 0, 0, avatar.getWidth(), avatar.getHeight(), matrix, true);
		ivAvatar.setImageBitmap(avatar);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

}
