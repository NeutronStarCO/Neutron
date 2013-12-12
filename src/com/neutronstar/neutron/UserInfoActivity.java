package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserInfoActivity extends Activity {
	private NeutronDbHelper ndb;
	private Intent intent;
	private Bundle bl;
	private int usage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		// ��ʼ�����ݿ⹤��
		ndb = NeutronDbHelper.GetInstance(this);
		// ��ô�������
		intent = this.getIntent();
		bl = intent.getExtras();
		usage = bl.getInt("usage");
		// ��ʼ���ý����Ϊ�½���һ���û�����ʽ
		switch(usage)
		{
		case MainTabFamily.TAG_ADD:
			((TextView)findViewById(R.id.user_info_gender_content)).setText("male");
		}
	}

}
