package com.neutronstar.neutron.model;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neutronstar.neutron.MainNeutron1;
import com.neutronstar.neutron.R;

public class FamilyMemberEntityAdapter extends BaseAdapter {
	
	private static final String TAG = FamilyMemberEntityAdapter.class.getSimpleName();
	private List<FamilyMemberEntity> coll;
	private Context context;
	private LayoutInflater mInflater;

	public FamilyMemberEntityAdapter(Context context, List<FamilyMemberEntity> coll) {
		context = context;
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		return coll.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FamilyMemberEntity entity = coll.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_family_member, null);
			viewHolder = new ViewHolder();
			viewHolder.ivHead = (ImageView)convertView.findViewById(R.id.family_member_head); 
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.family_member_name);
			viewHolder.tvBirthday = (TextView) convertView.findViewById(R.id.family_member_birthday);
			viewHolder.tvRelation = (TextView) convertView.findViewById(R.id.family_member_relation);
			viewHolder.tvRelationTag = (TextView) convertView.findViewById(R.id.family_member_relation_tag);
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder) convertView.getTag();
		
		viewHolder.ivHead.setImageBitmap(entity.getAvatar());
		viewHolder.tvName.setText(entity.getName());
		viewHolder.tvBirthday.setText(new SimpleDateFormat(MainNeutron1.instance.getResources().getString(R.string.dateformat_birthday)).format(entity.getBirthday()));
		viewHolder.tvRelation.setText(MainNeutron1.instance.getResources().getStringArray(R.array.relations)[entity.getRelation()]);
		viewHolder.tvRelationTag.setText(entity.getRelationTag() == com.neutronstar.neutron.NeutronContract.TAG.offered ? MainNeutron1.instance.getResources().getString(R.string.waiting_for_confirm):"");
		return convertView;
	}

	static class ViewHolder {
		public ImageView ivHead;
		public TextView tvName;
		public TextView tvBirthday;
		public TextView tvRelation;
		public TextView tvRelationTag;
	}
}
