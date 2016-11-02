package com.y.w.ywker.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by lxs on 16/10/25.
 */
public class EditFocusLayout extends LinearLayout {
    private final static String TAG = "lxs";

    private Context mContext;
    public EditFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View targetView = getTouchTargetView(this, x, y);
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (targetView == null) {
                imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            } else if (!(targetView instanceof EditText)){
                imm.hideSoftInputFromWindow(targetView.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private View getTouchTargetView(ViewGroup viewGroup, int x, int y) {
        View touchView = null;
        for (int i = 0; i < viewGroup.getChildCount(); i ++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                View tempView = getTouchTargetView((ViewGroup) view, x, y);
                if (tempView != null) touchView = tempView;
            } else {
                if (isTouchPointInView(view, x, y)) touchView = view;
            }
        }
        return touchView;
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect.contains(x, y);
    }
}
