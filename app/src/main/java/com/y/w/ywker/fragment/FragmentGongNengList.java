package com.y.w.ywker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.y.w.ywker.R;
import com.y.w.ywker.activity.ActivityXunJian;
import com.y.w.ywker.activity.WeiXiuHomeActivity;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.views.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lxs on 16/4/12.
 * 特殊功能页面
 */

public class FragmentGongNengList extends Fragment {

    @Bind(R.id.gridview)
    MyGridView gridview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gridview, container, false);
        ButterKnife.bind(this, rootView);
        //新建适配器
        String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};
        gridview.setAdapter(new SimpleAdapter(getContext(),//获取数据
                getData(), R.layout.item, from, to));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                case 0:
//                    Toast.makeText(getContext(),"巡检被点击了",Toast.LENGTH_SHORT).show();
                    Utils.start_Activity(getActivity(), ActivityXunJian.class, new YBasicNameValuePair[]{});
                break;
                case 1:
                    Utils.start_Activity(getActivity(), WeiXiuHomeActivity.class, new YBasicNameValuePair[]{});
                 break;
                default:
                    Toast.makeText(getContext(),"此功能有待开发 敬请期待",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });
        return rootView;
    }

    private int[] icon ={R.drawable.xunjian,R.drawable.weixiu,R.drawable.pandian,R.drawable.renwu,R.drawable.kuozhan};
    private String[] iconName ={"巡检","维修","盘点","任务","扩展"};
    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}

