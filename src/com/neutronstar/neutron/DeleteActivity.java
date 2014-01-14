package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DeleteActivity extends Activity {
	private NeutronDbHelper ndb;
	private int position;
	private int id;
	private String name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete);
		ndb = NeutronDbHelper.GetInstance(this);
		Intent intent=this.getIntent();
		Bundle bl = intent.getExtras();
		position = bl.getInt("position");
		id = bl.getInt("id");
		name = bl.getString("name");
	}
	
	public void cancelButton(View view)
	{
		DeleteActivity.this.finish();
	}

	public void deleteButton(View view)
	{
		Bundle bl= new Bundle();
		bl.putInt("position", position);
		Intent intent = this.getIntent();
		intent.putExtras(bl);
		DeleteActivity.this.setResult(RESULT_OK, intent);
		DeleteActivity.this.finish();
	}
}
