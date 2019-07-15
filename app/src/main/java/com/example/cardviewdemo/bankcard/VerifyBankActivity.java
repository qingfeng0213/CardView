package com.example.cardviewdemo.bankcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardviewdemo.R;
import com.example.cardviewdemo.util.CountDownTimerUtils;

public class VerifyBankActivity extends AppCompatActivity {

    private TextView bank_name;
    private TextView bank_type;
    private TextView card_name;
    private SharedPreferences banksp;
    private SharedPreferences hand_banksp;
    private SharedPreferences sp;
    private TextView send_verify;
    private EditText verify_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_bank);
        bank_name = findViewById(R.id.bank_name);
        bank_type = findViewById(R.id.bank_type);
        card_name = findViewById(R.id.card_name);
        send_verify = findViewById(R.id.send_verify);
        verify_num = findViewById(R.id.verify_num);
        verify_num.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        //获取身份证名字
        sp = getSharedPreferences("front", MODE_PRIVATE);
        String name1= sp.getString("name","");
        card_name.setText("您将绑定"+name1+"的银行卡");
        sp.edit().commit();
        banksp = getSharedPreferences("bank", MODE_PRIVATE);
        hand_banksp = getSharedPreferences("handbank", MODE_PRIVATE);
        String handname = hand_banksp.getString("handname", "");
        String bankNumber = hand_banksp.getString("bankNumber", "");
        String bankname = banksp.getString("bankname", "");
        String type = banksp.getString("type", "");
        Toast.makeText(this, bankNumber, Toast.LENGTH_SHORT).show();
        if (banksp.getBoolean("",false)) {
            bank_name.setText(bankname);
            bank_type.setText(type);
        }else {
            String substring = handname.substring(0, handname.indexOf("-"));
            bank_name.setText(substring);
            String substring1 = handname.substring(handname.indexOf("-")+1);
            bank_type.setText(substring1);
        }


       setOnClick();
    }
    //点击事件
    private void setOnClick(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyBankActivity.this.finish();
                hand_banksp.edit().clear().commit();
                banksp.edit().clear().commit();
            }
        });
        findViewById(R.id.bank_true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VerifyBankActivity.this, "绑卡成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerifyBankActivity.this,CardBagActivity.class));
            }
        });
        send_verify.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                int numcode = (int) ((Math.random() * 9 + 1) * 100000);
                String smstext = "你本次生成的6位安全验证码为：" + numcode;
                Toast.makeText(VerifyBankActivity.this, smstext, Toast.LENGTH_SHORT).show();
                verify_num.setText(numcode+"");
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(send_verify, 60000, 1000);
                send_verify.setTextColor(R.color.over);
                mCountDownTimerUtils.start();
            }
        });
    }
}
