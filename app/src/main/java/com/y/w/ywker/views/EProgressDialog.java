package com.y.w.ywker.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.y.w.ywker.R;

/**
 * Created by lxs on 16/5/25.
 */
public class EProgressDialog {

    private Dialog dialog;

    private Context ctx;

    private View rootView;

    ImageView spaceshipImage;

    AnimationDrawable animationDrawable;

    public EProgressDialog(Context ctx){
        dialog = new Dialog(ctx, R.style.loading_dialog);
        rootView = LayoutInflater.from(ctx).inflate(R.layout.comm_progress_dialog_view,null);
        dialog.setContentView(rootView);

        spaceshipImage = (ImageView) rootView.findViewById(R.id.loading_img);
        animationDrawable = (AnimationDrawable) spaceshipImage.getDrawable();
        animationDrawable.setOneShot(false);
        dialog.setContentView(rootView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

    }

    /**
     * 显示
     */

    public void show(){
        dialog.show();
        if (!animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }

    /**
     * 消失
     */
    public void dismiss(){
        if (animationDrawable.isRunning()){
            animationDrawable.stop();
        }
        dialog.dismiss();
        dialog = null;
    }


}
