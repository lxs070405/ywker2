package com.y.w.ywker.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by lxs on 16/5/7.
 */
public class AudioPlayerUtils {

    public interface OnPlayEndListner{
        public void onPlayEnd();
    }

    public void setEndListner(OnPlayEndListner endListner) {
        this.endListner = endListner;
    }

    private OnPlayEndListner endListner;
    private MediaPlayer mediaPlayer = null;
    public static AudioPlayerUtils audioPlayerUtils;
    private String currentUrl = "";

    private Context ctx;
    public static AudioPlayerUtils getInstance(Context ctx){
        if (audioPlayerUtils == null){
            audioPlayerUtils = new AudioPlayerUtils(ctx);
            audioPlayerUtils.setVoicePeker();
        }
        return audioPlayerUtils;
    }

    public AudioPlayerUtils(Context ctx){
        this.ctx = ctx;
    }
    /**
     * 设置外音播放
     */
    public void setVoicePeker(){
        AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);
    }

    public boolean isPlaying(){
        if (mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public String getCurrentUrl(){
        return this.currentUrl;
    }

    public void playVoice(String filePath) {

        if (TextUtils.isEmpty(filePath)){
            return;
        }

        /**
         * 播放的是同一个
         */
        if (isPlaying()){
            if (mediaPlayer != null){
                mediaPlayer.reset();
            }
            stopPlayVoice();
            /**
             * 判断播放的是否是相同的语音
             */
            if (filePath.equals(currentUrl)){
                return;
            }
        }

        currentUrl = filePath;
        if (currentUrl.contains("http://")){

        }else{
            if (!(new File(filePath).exists())) {
                return;
            }
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(currentUrl);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    stopPlayVoice();
                    mediaPlayer = null;
                    if (endListner != null){
                        endListner.onPlayEnd();
                    }
                    endListner = null;
                }
            });
//            Log.e(getClass().getSimpleName(),"开始播放音乐..");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
//            Log.e(getClass().getSimpleName(),"播放音乐:" + e.toString());
        }
    }

    public void stopPlayVoice() {
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
