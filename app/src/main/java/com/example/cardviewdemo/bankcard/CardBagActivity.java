package com.example.cardviewdemo.bankcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cardviewdemo.R;
import com.example.cardviewdemo.authentication.IdCardActivity;
import com.example.cardviewdemo.authentication.MainActivity;

public class CardBagActivity extends AppCompatActivity {
    PopupWindow popupWindow;
    private ImageView remove;
    private Button goapprove;
    private CardView user_renzheng;
    private SharedPreferences banksp;
    private SharedPreferences hand_banksp;
    private ImageView bound_background;
    private Button boundcard;
    private NestedScrollView bank_scroll;
    private TextView main_bankname;
    private TextView main_banknum;
    private TextView main_banktype;
    private LinearLayout add_bank;
    private Button add_new_bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_bag);
        bound_background = findViewById(R.id.bound_background);
        boundcard = findViewById(R.id.boundcard);
        bank_scroll = findViewById(R.id.bank_scroll);
        add_bank = findViewById(R.id.add_bank);
        main_bankname = findViewById(R.id.main_bankname);
        main_banknum = findViewById(R.id.main_banknum);
        main_banktype = findViewById(R.id.main_banktype);
        add_new_bank = findViewById(R.id.add_new_bank);

        //获取SP
        banksp = getSharedPreferences("bank", MODE_PRIVATE);
        hand_banksp = getSharedPreferences("handbank", MODE_PRIVATE);
        String cardnum = banksp.getString("cardnum", "");
        String bankname = banksp.getString("bankname", "");
        String type = banksp.getString("type", "");
        String bankNumber = hand_banksp.getString("bankNumber", "");
        String handname = hand_banksp.getString("handname", "");
        //判断是手动输入还是直接拍照获取
        if (banksp.getBoolean("",false)) {
            bound_background.setVisibility(View.GONE);
            boundcard.setVisibility(View.GONE);
            bank_scroll.setVisibility(View.VISIBLE);
            add_bank.setVisibility(View.VISIBLE);
            main_bankname.setText(bankname);
            main_banknum.setText(getHideBankCardNum(cardnum));
            main_banktype.setText(type);
        }else if (hand_banksp.getBoolean("",false)){
            bound_background.setVisibility(View.GONE);
            boundcard.setVisibility(View.GONE);
            bank_scroll.setVisibility(View.VISIBLE);
            add_bank.setVisibility(View.VISIBLE);
            String substring = handname.substring(0, handname.indexOf("-"));
            main_bankname.setText(substring);
            String substring1 = handname.substring(handname.indexOf("-")+1);
            main_banktype.setText(substring1);
            main_banknum.setText(getHideBankCardNum(bankNumber));

        }else {
            bound_background.setVisibility(View.VISIBLE);
            boundcard.setVisibility(View.VISIBLE);
            bank_scroll.setVisibility(View.GONE);
            add_bank.setVisibility(View.GONE);
        }
        setOnClick();
    }

    //点击事件
    private void setOnClick(){
        boundcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAnimation();
            }
        });
        add_new_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CardBagActivity.this,AddBankActivity.class));
            }
        });
    }
    //弹框
    private void showAnimation(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.activity_dialog, null, false);//引入弹窗布局
        popupWindow = new PopupWindow(vPopupWindow, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        remove = vPopupWindow.findViewById(R.id.remove);
        goapprove = vPopupWindow.findViewById(R.id.goapprove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        goapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardBagActivity.this, MainActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        //设置背景透明
        addBackground();

        //设置进出动画
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);

        //引入依附的布局
        View parentView = LayoutInflater.from(CardBagActivity.this).inflate(R.layout.activity_dialog, null);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    //设置弹框弹出背景变暗
    private void addBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }
    //给银行卡号加*
    public static String getHideBankCardNum(String bankCardNum) {
        try {
            int length = bankCardNum.length();

            if (length > 4) {
                String startNum = bankCardNum.substring(0, 4);
                String endNum = bankCardNum.substring(length - 4, length);
                bankCardNum = startNum + " **** **** " + endNum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bankCardNum;
    }
}
