package com.y.w.ywker.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.y.w.ywker.R;
import com.y.w.ywker.activity.ActivityOrderReplayDetails;
import com.y.w.ywker.activity.ActivityVideo;
import com.y.w.ywker.activity.WeiXiuOrderDetailActivity;
import com.y.w.ywker.entry.ReplayMsgEntry;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.utils.AudioPlayerUtils;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxs on 16/5/3.
 * 聊天会话适配器
 */

public class AdapterReplayOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    enum MSG_LIST_ITEM{
        LEFT,
        RIGHT
    }

    private String userName = "";

    /**
     * 播放录音动画
     */
    private AnimationDrawable animationDrawable;
    private AudioPlayerUtils audioPlayerUtils;

    private List<ReplayMsgEntry> lists = new ArrayList<ReplayMsgEntry>();

    private Context ctx;
    private String source;
    public AdapterReplayOrder(Context ctx,String source){
        this.ctx = ctx;
        this.source = source;
        String userJson = OfflineDataManager.getInstance(ctx).getUser();
        Gson gson = new Gson();
        UserEntry userEntry = gson.fromJson(userJson,UserEntry.class);
        if (userEntry != null){
            userName = userEntry.getUserName();
        }
        audioPlayerUtils = AudioPlayerUtils.getInstance(ctx);
    }

    public void addEntry(ReplayMsgEntry entry){
        if (lists != null){
            lists.add(entry);
            notifyDataSetChanged();
        }
    }

    public void removeEntry(ReplayMsgEntry entry){
        if (lists != null && lists.contains(entry)){
            lists.remove(entry);
            notifyDataSetChanged();
        }
    }

    public void addAllEntry(List<ReplayMsgEntry> list){
        if (lists != null){
            lists.addAll(0,list);
//            notifyDataSetChanged();
        }
    }

    public void removeAll(){
        if (lists != null){
            lists.clear();
//            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == MSG_LIST_ITEM.LEFT.ordinal()){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_left_order_replay_item,parent,false);
            return new ReplayOrderLeftVH(itemView);
        }else if(viewType == MSG_LIST_ITEM.RIGHT.ordinal()){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_right_order_replay_item,parent,false);
            return new ReplayOrderRightVH(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null){
            if (holder instanceof ReplayOrderLeftVH){
                ((ReplayOrderLeftVH) holder).bindData(lists.get(position));
            }else if(holder instanceof ReplayOrderRightVH){
                ((ReplayOrderRightVH) holder).bindData(lists.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (this.lists != null && !this.lists.isEmpty()){
            return this.lists.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (lists != null && position < lists.size()){
            String name = lists.get(position).getUserName();
            if (name.equals(userName)){
                //返回右边的布局
                return MSG_LIST_ITEM.RIGHT.ordinal();
            }else{
                //返回左边的布局
                return MSG_LIST_ITEM.LEFT.ordinal();
            }
        }
        return super.getItemViewType(position);
    }

    class ReplayOrderLeftVH extends RecyclerView.ViewHolder{

        TextView userName;
        TextView userRole;
        TextView time;
        TextView msg;
        ImageView header;

        ImageView video_img;
        ImageView voice;

        public ReplayOrderLeftVH(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.replay_left_user_name);
//            userRole = (TextView) itemView.findViewById(R.id.replay_left_user_role);
            time = (TextView) itemView.findViewById(R.id.replay_left_user_time);
            msg = (TextView) itemView.findViewById(R.id.replay_left_user_msg);
            header = (ImageView) itemView.findViewById(R.id.replay_left_user_icon);
            video_img = (ImageView) itemView.findViewById(R.id.replay_left_user_img_video);
            voice = (ImageView) itemView.findViewById(R.id.replay_left_user_voice);
        }

        public void bindData(ReplayMsgEntry entry){
            userName.setText(entry.getUserName());
//            String role = entry.getUserRole();
//            if (role != null && !role.equals("")){
//                userRole.setText(role);
////                userRole.setVisibility(View.VISIBLE);
//            }else{
//                userRole.setVisibility(View.GONE);
//            }
//            time.setText(TimeUtils.formatYwkerDate(entry.getSendTime()));
            time.setText(TimeUtils.setTime(entry.getSendTime()));

            initData(entry);
        }

        private void initData(final ReplayMsgEntry entry){
            String type = entry.getMessageType();
            if (type.equalsIgnoreCase("pic")||type.equalsIgnoreCase("video")){
                msg.setVisibility(View.GONE);
                voice.setVisibility(View.GONE);
                video_img.setVisibility(View.VISIBLE);

                if (type.equalsIgnoreCase("video")){
                    Log.e(getClass().getSimpleName(),"type = " + type);
                    video_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx, ActivityVideo.class);
                            i.putExtra("video_url", entry.getFileDown());
                            ctx.startActivity(i);
                        }
                    });
                    video_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    video_img.setImageResource(R.drawable.video_play);
                }
                if (type.equalsIgnoreCase("pic")){
                    Log.e(getClass().getSimpleName(),"type = " + type);
                    video_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(source.equals("weixiu")){
                                ((WeiXiuOrderDetailActivity) ctx).showImage(entry.getFileDown());
                            }else {
                                ((ActivityOrderReplayDetails) ctx).showImage(entry.getFileDown());
                            }

                        }
                    });
                    video_img.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(ctx)
                            .load(entry.getFileDown())
                            .placeholder(R.drawable.photo_default)
                            .error(R.drawable.photo_default)
                            .crossFade()
                            .into(video_img);
                }

            }else if(type.equalsIgnoreCase("voice")){
                msg.setVisibility(View.GONE);
                voice.setVisibility(View.VISIBLE);
                video_img.setVisibility(View.GONE);
                voice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 播放音频
                         */
                        audioPlayerUtils.setEndListner(new AudioPlayerUtils.OnPlayEndListner() {
                            @Override
                            public void onPlayEnd() {
                                voice.setImageResource(R.drawable.chatfrom_voice_playing);
                            }
                        });
                        if (audioPlayerUtils.isPlaying() && audioPlayerUtils.getCurrentUrl().equals(entry.getFileDown())){
                            voice.setImageResource(R.drawable.chatfrom_voice_playing);
                            audioPlayerUtils.stopPlayVoice();
                        }else{
                            audioPlayerUtils.playVoice(entry.getFileDown());
                            voice.setImageResource(R.drawable.voice_frame);
                        }

                    }
                });

            }else if(type.equalsIgnoreCase("adr")||type.equalsIgnoreCase("asset")||
                    type.equalsIgnoreCase("character")){
                msg.setVisibility(View.VISIBLE);
                voice.setVisibility(View.GONE);
                video_img.setVisibility(View.GONE);
                if (entry.getMessageType().equalsIgnoreCase("adr")){
                    msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.reply_gpsa, 0);
                }else{
                    msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                /**
                 * 设置内容
                 */
                msg.setText(entry.getSendDetail());
            }else{
                msg.setVisibility(View.VISIBLE);
                voice.setVisibility(View.GONE);
                video_img.setVisibility(View.GONE);
            }
        }
    }

    class ReplayOrderRightVH extends RecyclerView.ViewHolder{

        TextView userName;
        TextView userRole;
        TextView time;
        TextView msg;
        ImageView header;
        ImageView video_img;
        ImageView voice;

        public ReplayOrderRightVH(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.replay_right_user_name);
//            userRole = (TextView) itemView.findViewById(R.id.replay_right_user_role);
            time = (TextView) itemView.findViewById(R.id.replay_right_time);
            msg = (TextView) itemView.findViewById(R.id.replay_right_msg);
            header = (ImageView) itemView.findViewById(R.id.replay_right_user_icon);
            video_img = (ImageView) itemView.findViewById(R.id.replay_right_user_img_video);
            voice = (ImageView) itemView.findViewById(R.id.replay_right_user_voice);
        }

        public void bindData(ReplayMsgEntry entry){
            userName.setText(entry.getUserName());
//            String role = entry.getUserRole();
//            if (role != null && !role.equals("")){
//                userRole.setText(role);
//                userRole.setVisibility(View.VISIBLE);
//            }else{
//                userRole.setVisibility(View.GONE);
//            }

            String timeStr = entry.getSendTime();
            if (timeStr.contains("Date")){
                time.setText(TimeUtils.setTime(timeStr));
//                time.setText(TimeUtils.formatYwkerDate(timeStr));
            }else{
                time.setText(timeStr);
            }
            initData(entry);
        }

        private void initData(final ReplayMsgEntry entry){
            String type = entry.getMessageType();
            if (type.equalsIgnoreCase("pic")||type.equalsIgnoreCase("video")){
                msg.setVisibility(View.GONE);
                voice.setVisibility(View.GONE);
                video_img.setVisibility(View.VISIBLE);

                if (type.equalsIgnoreCase("video")){
                    Log.e(getClass().getSimpleName(),"type = " + type);
                    video_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx, ActivityVideo.class);
                            i.putExtra("video_url", entry.getFileDown());
                            ctx.startActivity(i);
                        }
                    });
                    video_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    video_img.setImageResource(R.drawable.video_play);
                }

                if (type.equalsIgnoreCase("pic")){
                    Log.e(getClass().getSimpleName(),"type = " + type);
                    video_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(source.equals("weixiu")){
                                ((WeiXiuOrderDetailActivity) ctx).showImage(entry.getFileDown());
                            }else {
                                ((ActivityOrderReplayDetails) ctx).showImage(entry.getFileDown());
                            }
                        }
                    });
                    video_img.setScaleType(ImageView.ScaleType.FIT_XY);
                    Glide.with(ctx)
                            .load(entry.getFileDown())
                            .placeholder(R.drawable.photo_default)
                            .error(R.drawable.photo_default)
                            .crossFade()
                            .into(video_img);
                }

            }else if(type.equalsIgnoreCase("voice")){
                msg.setVisibility(View.GONE);
                voice.setVisibility(View.VISIBLE);
                video_img.setVisibility(View.GONE);
                voice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 播放音频
                         */
                        audioPlayerUtils.setEndListner(new AudioPlayerUtils.OnPlayEndListner() {
                            @Override
                            public void onPlayEnd() {
                                voice.setImageResource(R.drawable.chatfrom_voice_playing);
                            }
                        });
                        if (audioPlayerUtils.isPlaying() && audioPlayerUtils.getCurrentUrl().equals(entry.getFileDown())){
                            voice.setImageResource(R.drawable.chatfrom_voice_playing);
                            audioPlayerUtils.stopPlayVoice();
                        }else{
                            audioPlayerUtils.playVoice(entry.getFileDown());
                            voice.setImageResource(R.drawable.voice_frame);
                        }
                    }
                });

            }else if(type.equalsIgnoreCase("adr")||type.equalsIgnoreCase("asset")||
                    type.equalsIgnoreCase("character")){
                msg.setVisibility(View.VISIBLE);
                voice.setVisibility(View.GONE);
                video_img.setVisibility(View.GONE);
                if (entry.getMessageType().equalsIgnoreCase("adr")){
                    msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.reply_gpsa, 0, 0, 0);
                }else{
                    msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                /**
                 * 设置内容
                 */
                msg.setText(entry.getSendDetail());
            }else{
                msg.setVisibility(View.VISIBLE);
                voice.setVisibility(View.GONE);
                video_img.setVisibility(View.GONE);
            }
        }
    }
}
