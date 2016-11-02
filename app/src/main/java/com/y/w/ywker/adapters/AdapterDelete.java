package com.y.w.ywker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.y.w.ywker.R;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.views.SlidingButtonView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxs on 2016/9/2.
 */
public class AdapterDelete extends RecyclerView.Adapter<AdapterDelete.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener {

    private Context mContext;

    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private List<String> mDatas = new ArrayList<String>();

    private SlidingButtonView mMenu = null;
    public AdapterDelete(Context context,List<String> mDatas) {
        mContext = context;
//        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;
       this.mDatas = mDatas;
    }

    public void setDeleteBtnClickListener(IonSlidingViewClickListener mIDeleteBtnClickListener){
        this.mIDeleteBtnClickListener = mIDeleteBtnClickListener;
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        String[] str = mDatas.get(position).split(",");
        holder.textView.setText("   "+str[0]);
        holder.codeView.setText(str[1]);

        //设置内容布局的宽为屏幕宽度
        holder.layout_content.getLayoutParams().width = Utils.getScreenWidth(mContext);

        holder.textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onItemClick(v, n);
                }

            }
        });
            holder.btn_Delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = holder.getLayoutPosition();
                    mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
                }
            });

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_zongjie_shebei_item, arg0,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView btn_Delete;
        public TextView textView;
        public TextView codeView;
        public ViewGroup layout_content;
        public MyViewHolder(View itemView) {
            super(itemView);
            btn_Delete = (TextView) itemView.findViewById(R.id.tv_delete);
            textView = (TextView) itemView.findViewById(R.id.text);
            codeView = (TextView) itemView.findViewById(R.id.code);
            layout_content = (ViewGroup) itemView.findViewById(R.id.layout_content);
            ((SlidingButtonView) itemView).setSlidingButtonListener(AdapterDelete.this);
        }
    }

    public void addData(int position) {
        mDatas.add(position, "添加项");
        notifyItemInserted(position);
    }

    public void removeData(int position){
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(mMenu != slidingButtonView){
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;
    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(mMenu != null){
            return true;
        }
        return false;
    }



    public interface IonSlidingViewClickListener {
        /**
         * 点击条目
         * @param view
         * @param position
         */
        void onItemClick(View view,int position);

        /**
         * 删除条目
         * @param view
         * @param position
         */
        void onDeleteBtnCilck(View view,int position);
    }
}

