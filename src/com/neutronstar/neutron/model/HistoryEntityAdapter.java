package com.neutronstar.neutron.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.neutronstar.neutron.R;

public class HistoryEntityAdapter extends BaseAdapter {
	
	private static final String TAG = HistoryEntityAdapter.class.getSimpleName();
	private List<HistoryEntity> coll;
	private Context context;
	private LayoutInflater mInflater;

	public HistoryEntityAdapter(Context context, List<HistoryEntity> coll) {
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
		HistoryEntity entity = coll.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_phical_exam_history_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.pehi_date);
			viewHolder.tvTestingInstitution = (TextView) convertView.findViewById(R.id.pehi_testingInstitution);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvDate.setText(entity.getDate());
		viewHolder.tvTestingInstitution.setText(entity.getTestingInstitution());
		return convertView;
	}
	
	static class ViewHolder {
		public TextView tvDate;
		public TextView tvTestingInstitution;
	}

}
