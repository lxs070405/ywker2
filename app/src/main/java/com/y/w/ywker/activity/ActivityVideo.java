package com.y.w.ywker.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.y.w.ywker.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lxs on 16/5/7.
 */

/**
 * 主要用于播放工单回复里面的视频信息
 */
public class ActivityVideo extends SuperActivity {
    @Bind(R.id.video_view)
    VideoView videoView;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.btn_video_back)
    ImageView btnVideoBack;

    String video_url = "";
    private MediaController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_video);
        ButterKnife.bind(this);

        layoutCommToolbarTitle.setText("播放视频");

        video_url = getIntent().getStringExtra("video_url");

        /**
         * 网络视频,从服务器上获得的网络url
         */
        if (video_url.contains("http://")) {
            Uri uri = Uri.parse(video_url);
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(uri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            videoView.requestFocus();
        } else {//本地视频
            File file = new File(video_url);
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoPath(file.getAbsolutePath());
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            videoView.requestFocus();
        }

        btnVideoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
