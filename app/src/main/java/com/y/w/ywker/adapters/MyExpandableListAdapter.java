package com.y.w.ywker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.y.w.ywker.R;

import java.util.List;
import java.util.Map;

/**
 * Created by lxs on 2016/8/23.
 * 可折叠的listview适配器
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private List<String> parentdata;
    private Map<String, List<String>> map;
    private Context ctx;
    public MyExpandableListAdapter(List<String> parentdata, Map<String, List<String>> map, Context ctx){
       this.parentdata = parentdata;
        this.map = map;
        this.ctx = ctx;
    }
    //ExpandableListAdapter里面的所有条目都可用吗？
    //如果是yes，就意味着所有条目可以选择和点击了。返回值：返回True表示所有条目均可用。
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    //得到子item需要关联的数据   获取指定组中的指定子元素数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String key = parentdata.get(groupPosition);
        return (map.get(key).get(childPosition));
    }

    //得到子item的ID
    /**
     * 获取指定组中的指定子元素ID，这个ID在组里一定是唯一的。
     * 联合ID（getCombinedChildId(long, long)）在所有条目（所有组和所有元素）中也是唯一的。
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    /**
     * 获取一个视图对象，显示指定组中的指定子元素数据。  参数：groupPosition  组位置（该组内部含有子元素）
     childPosition 子元素位置（决定返回哪个视图）
     isLastChild   子元素是否处于组中的最后一个
     　　　　	 convertView   重用已有的视图(View)对象。注意：在使用前你应该检查一下这个视图对象是否非空并且这个对象的类型是否合适。由此引伸出，如果该对象不能被转换并显示正确的数据，这个方法就会调用getChildView(int, int, boolean, View, ViewGroup)来创建一个视图(View)对象。
     parent       返回的视图(View)对象始终依附于的视图组。
     返回值：   指定位置上的子元素返回的视图对象
     */
    //设置子item的组件
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String key = parentdata.get(groupPosition);
        String info = map.get(key).get(childPosition).split(",")[1];
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_children, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.second_textview);
        tv.setText(info);
        return tv;
    }


    /**
     * 获取指定组中的子元素个数
     参数：  groupPosition 组位置（决定返回哪个组的子元素个数）
     返回值 ：指定组的子元素个数
     */
    //获取当前父item下的子item的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        String key = parentdata.get(groupPosition);
        int size=map.get(key).size();
        return size;
    }

    /**
     * 从列表所有项(组或子项)中获得一个唯一的子ID号。可折叠列表要求每个元素(组或子项)在所有的子元素和组中有一个唯一的ID。本方法负责根据所给的子ID号和组ID号返回唯一的ID。此外，若hasStableIds()是true，那么必须要返回稳定的ID。
     参数：groupId 包含该子元素的组ID
     childId  子元素的ID
     返回值：    列表所有项(组或子项)中唯一的(和可能稳定)的子元素ID号。（译者注：ID理论上是稳定的，不会发生冲突的情况。也就是说，这个列表会有组、子元素，它们的ID都是唯一的。）
     */
    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return super.getCombinedChildId(groupId, childId);
    }


    /**
     * 获取指定组中的数据  参数 ：  groupPosition 组位置
     返回值 ：返回组中的数据，也就是该组中的子元素数据
     */
    @Override
    public Object getGroup(int groupPosition) {
        return parentdata.get(groupPosition);
    }

    //获取组的个数     返回值：  组的个数
    @Override
    public int getGroupCount() {
        return parentdata.size();
    }

    /**
     * 获取指定组的ID，这个组ID必须是唯一的。联合ID(参见getCombinedGroupId(long))在所有条目(所有组和所有元素)中也是唯一的。
     *  参数： groupPosition 组位置
     返回值：  返回组相关ID
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    /**
     * 获取显示指定组的视图对象。这个方法仅返回关于组的视图对象，要想获取子元素的视图对象，就需要调用getChildView(int, int, boolean, View, ViewGroup)。
     　　　　　　参数
     　　groupPosition 组位置（决定返回哪个视图）
     　　isExpanded    该组是展开状态还是伸缩状态
     　　　　	  convertView  重用已有的视图对象。注意：在使用前你应该检查一下这个视图对象是否非空并且这个对象的类型是否合适。由此引伸出，如果该对象不能被转换并显示正确的数据，这个方法就会调用getGroupView(int, boolean, View, ViewGroup)来创建一个视图(View)对象。
     parent   返回的视图对象始终依附于的视图组。
     　　	     返回值： 返回指定组的视图对象
     */
    //设置父item组件
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_parent, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.parent_textview);

        tv.setText(parentdata.get(groupPosition));
        return tv;
    }

    /**
     * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
     返回值：返回一个Boolean类型的值，如果为TRUE，意味着相同的ID永远引用相同的对象
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     *  是否选中指定位置上的子元素。

     参数：  groupPosition 组位置（该组内部含有这个子元素）

     childPosition  子元素位置

     返回值：  是否选中子元素
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     *  返回值：如果当前适配器不包含任何数据则返回True。经常用来决定一个空视图是否应该被显示。
     *  一个典型的实现将返回表达式getCount() == 0的结果，但是由于getCount()包含了头部和尾部，适配器可能需要不同的行为。
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }




}
