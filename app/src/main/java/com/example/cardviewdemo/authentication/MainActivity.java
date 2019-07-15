package com.example.cardviewdemo.authentication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cardviewdemo.R;

public class MainActivity extends AppCompatActivity {
    PopupWindow popupWindow;
    private ImageView remove;
    private Button goapprove;
    private CardView user_renzheng;
    private ImageView head;
    private SharedPreferences sp;
    private SharedPreferences sp1;
    private String num1;
    private TextView tv_name;
    private TextView tv_num;
    private TextView zjzp;
    private TextView personal;
    private ImageView right;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_name = findViewById(R.id.tv_name);
        tv_num = findViewById(R.id.tv_num);
        zjzp = findViewById(R.id.zjzp);
        personal = findViewById(R.id.personal);
        right = findViewById(R.id.right);

        user_renzheng = findViewById(R.id.user_renzheng);
        head = findViewById(R.id.head);
        //获取身份证正反面信息
        sp = getSharedPreferences("front", MODE_PRIVATE);
        sp1 = getSharedPreferences("back", MODE_PRIVATE);
        num1 = sp.getString("num","");
        String name1= sp.getString("name","");

        if (sp.getBoolean("frontyes",false) && sp1.getBoolean("backyes",false)){
            user_renzheng.setVisibility(View.VISIBLE);
            head.setVisibility(View.GONE);
            tv_name.setText(name1);
            tv_num.setText(hideId(num1));
            zjzp.setTextColor(R.color.over);
            personal.setTextColor(R.color.over);
            zjzp.setText("已完善");
            personal.setText("已完善");
            //设置为不可点击状态
//            zjzp.setOnClickListener(null);
//            right.setVisibility(View.GONE);
        }else {
            user_renzheng.setVisibility(View.GONE);
            head.setVisibility(View.VISIBLE);

        }
        sp.edit().clear();
        sp1.edit().clear();
        sp.edit().commit();
        sp1.edit().commit();

       setonClick();
    }

//    private boolean isShow=false;
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if (hasFocus&&!isShow && sp.getBoolean("",true)) {
////            showAnimation();
//            isShow=true;
//        }
//    }

    //点击事件
    private void setonClick(){
        findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersoonalActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.personal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersoonalActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.zjzp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IdCardActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.face).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FaceActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });
    }
//    //弹框
//    private void showAnimation(){
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View vPopupWindow = inflater.inflate(R.layout.activity_dialog, null, false);//引入弹窗布局
//        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
//        remove = vPopupWindow.findViewById(R.id.remove);
//        goapprove = vPopupWindow.findViewById(R.id.goapprove);
//        remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow.dismiss();
//            }
//        });
//        goapprove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, IdCardActivity.class);
//                startActivity(intent);
//                popupWindow.dismiss();
//            }
//        });
//        //设置背景透明
//        addBackground();
//
//        //设置进出动画
//        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
//
//        //引入依附的布局
//        View parentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_dialog, null);
//        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
//        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
//    }
//
//    //设置弹框弹出背景变暗
//    private void addBackground() {
//        // 设置背景颜色变暗
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.7f;//调节透明度
//        getWindow().setAttributes(lp);
//        //dismiss时恢复原样
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = 1f;
//                getWindow().setAttributes(lp);
//            }
//        });
//    }
    //隐藏身份证信息
    public static String hideId(String num){
        StringBuilder stringBuilder = new StringBuilder(num);
        stringBuilder.replace(1, 16, "******************");
        return stringBuilder.toString();
    }

}
