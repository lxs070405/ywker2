package com.y.w.ywker.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.y.w.ywker.R;

/**
 * Created by lxs on 2016/9/24.
 */
public class LingJianView extends LinearLayout implements View.OnClickListener {

    Button addbt;
    EditText edt;
    Button subbt;
    EditText edtLingjianName;
    LinearLayout llLingjian;

    public LingJianView(Context context) {
        this(context, null);
    }

    public LingJianView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LingJianView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Context ctx;

    private void init(Context context, AttributeSet attrs) {
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view =  inflater.inflate(R.layout.lingjian_view, this);
        addbt = (Button) view.findViewById(R.id.addbt);
        subbt = (Button) view.findViewById(R.id.subbt);
        edt = (EditText) view.findViewById(R.id.edt);
        edtLingjianName = (EditText) view.findViewById(R.id.edt_lingjianName);
        llLingjian = (LinearLayout) view.findViewById(R.id.ll_lingjian);

    }

    int num = 1; //数量

    private LingJianViewListener listener ;
    public void setViewListener(){
        addbt.setOnClickListener(this);
        subbt.setOnClickListener(this);
        edt.addTextChangedListener(new OnTextChangeListener());
    }
    public void SetViewListener(LingJianViewListener l) {
        listener = l;
    }

    public interface LingJianViewListener{
        void setListener(Button subbt,LingJianView view,int num);
    }
    @Override
    public void onClick(View v) {
        String numString = edt.getText().toString();
        if (numString == null || numString.equals("")) {
            num = 0;
            edt.setText("0");
        }
        switch (v.getId()){
            case R.id.subbt:
                if (++num < 0)  //先加，再判断
                {
                    num--;
                    Toast.makeText(ctx, "请输入一个大于0的数字",
                            Toast.LENGTH_SHORT).show();
                } else {
                    edt.setText(String.valueOf(num));
                    isGone();
                }
//                listener.setListener(subbt,this,num);
                break;
            case R.id.addbt:
                if (--num < 0)  //先减，再判断
                {
                    num++;
                    Toast.makeText(ctx, "请输入一个大于0的数字",
                            Toast.LENGTH_SHORT).show();
                } else {
                    edt.setText(String.valueOf(num));
                    isGone();
                }
                break;

        }
    }

    /**
     * 设置显示零件数量
     * @param lingjianNumber
     */
    public void setLingjianData(String lingjianNumber,String lingjianName){
        edt.setText(lingjianNumber);
        edtLingjianName.setText(lingjianName);
    }

    /**
     * EditText输入变化事件监听器
     */
    class OnTextChangeListener implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            String numString = s.toString();
            if (numString == null || numString.equals("")) {
                num = 0;

            } else {
                int numInt = Integer.parseInt(numString);
                if (numInt < 0) {
                    Toast.makeText(ctx, "请输入一个大于0的数字",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //设置EditText光标位置 为文本末端
                    edt.setSelection(edt.getText().toString().length());
                    num = numInt;
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    }

    /**
     * 判断是否显示隐藏该布局
     */
    public void isGone() {
        if (edt.getText().toString().equals("0")) {
            edt.setText("");
            edtLingjianName.setText("");
            llLingjian.setVisibility(View.GONE);
        }else {
            llLingjian.setVisibility(View.VISIBLE);
        }
    }



    /**
     * 获取零件数据 返回值:为""说明没有数据
     * @return   数量+","+零件名称
     */
    public String getLingJianData(){
        String number = edt.getText().toString().trim();
        String ling = edtLingjianName.getText().toString().trim();

        if(number.equals("0")||ling.equals("")){
            return "";
        }else {
            return number+","+ling;
        }
    }

}
