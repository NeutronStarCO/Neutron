package com.neutronstar.neutron.model;

import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neutronstar.neutron.R;

public class GroupTestingEntityAdapter extends BaseAdapter {
	
	private static final String TAG = GroupTestingEntityAdapter.class.getSimpleName();
	private List<GroupTestingEntity> coll;
	private Context context;
	private LayoutInflater mInflater;

	public GroupTestingEntityAdapter(Context context, List<GroupTestingEntity> coll) {
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupTestingEntity entity = coll.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_group_testing_item, null);
			viewHolder = new ViewHolder();
			viewHolder.layoutR = (RelativeLayout)convertView.findViewById(R.id.group_testing_item_layout); 
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.group_testing_item_name);
			viewHolder.tvValue = (TextView) convertView.findViewById(R.id.group_testing_item_value);
			viewHolder.tvDimension = (TextView) convertView.findViewById(R.id.group_testing_item_dimension);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvName.setText(entity.getName());
		if(entity.hasValue())
			viewHolder.tvValue.setText(new DecimalFormat("#.##").format(entity.getValue()));
		else
			viewHolder.tvValue.setText("");
		viewHolder.tvDimension.setText(entity.getDimension());
		if(1 == coll.size())
			viewHolder.layoutR.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.preference_single_item));
		else if(1<coll.size())
		{
			if(0 == position)
				viewHolder.layoutR.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.preference_first_item));
			if(coll.size()-1 == position)
				viewHolder.layoutR.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.preference_last_item));
		}
			
		return convertView;
	}
	
	static class ViewHolder {
		public RelativeLayout layoutR;
		public TextView tvName;
		public TextView tvValue;
		public TextView tvDimension;
	}

}
