package com.y.w.ywker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.entry.DevicesTypeEntry;
import com.y.w.ywker.views.IphoneTreeView;

import java.util.HashMap;
import java.util.List;

public class DevicesTypeListAdapter extends BaseExpandableListAdapter implements IphoneTreeView.IphoneTreeHeaderAdapter {

	private Context mContext;
	private List<DevicesTypeEntry> parentdata;
	private HashMap<Integer, Integer> groupStatusMap;

	private ExpandableListView mIphoneTreeView;



	@SuppressLint("UseSparseArrays")
	public DevicesTypeListAdapter(Context context, List<DevicesTypeEntry> groupList, ExpandableListView mIphoneTreeView) {
		this.mContext = context;
		this.parentdata = groupList;
		this.mIphoneTreeView = mIphoneTreeView;
		groupStatusMap = new HashMap<Integer, Integer>();

	}

	//得到子item需要关联的数据   获取指定组中的指定子元素数据
	@Override
	public DevicesTypeEntry.ChildNodeBean getChild(int groupPosition, int childPosition) {
		return  parentdata.get(groupPosition).getChildNode().get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * 分组中的总人数
	 * @param groupPosition
	 * @return
	 */
	public int getChildrenCount(int groupPosition) {
		return parentdata.get(groupPosition).getChildNode().size();
	}


	public Object getGroup(int groupPosition) {
		return parentdata.get(groupPosition);
	}

	public int getGroupCount() {
		return parentdata.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	/**
	 * Child  子列表
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_devicestypelist_child, null);
			holder = new ChildHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.contact_list_item_name);// 昵称
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		DevicesTypeEntry.ChildNodeBean child = getChild(groupPosition, childPosition);
		if(childPosition == 0){
			holder.nameView.setText("-----");// 昵称
		}else {
			holder.nameView.setText(child.getTypeName());// 昵称
		}
		return convertView;
	}

	/**
	 * Group
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_devicestypelist_group, null);
			holder = new GroupHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.group_name);
			holder.iconView = (ImageView) convertView.findViewById(R.id.group_indicator);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
		holder.nameView.setText(parentdata.get(groupPosition).getTypeName());
		if (isExpanded) {
			holder.iconView.setImageResource(R.drawable.qb_down);
		} else {
			holder.iconView.setImageResource(R.drawable.qb_right);
		}
		return convertView;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			return 2;
		} else if (childPosition == -1 && !mIphoneTreeView.isGroupExpanded(groupPosition)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public void configureTreeHeader(View header, int groupPosition, int childPosition, int alpha) {
		((TextView) header.findViewById(R.id.group_name)).setText(parentdata.get(groupPosition).getTypeName());// 组名
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	class GroupHolder {
		TextView nameView;
		ImageView iconView;
	}

	class ChildHolder {
		TextView nameView;
	}




}
