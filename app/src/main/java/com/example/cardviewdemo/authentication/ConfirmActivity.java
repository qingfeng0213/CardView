package com.example.cardviewdemo.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cardviewdemo.R;
import com.example.cardviewdemo.bankcard.AddBankActivity;
import com.example.cardviewdemo.bankcard.CardBagActivity;

public class ConfirmActivity extends AppCompatActivity {


    private SharedPreferences sp1;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        EditText name = findViewById(R.id.name);
        EditText num = findViewById(R.id.num);
        TextView end = findViewById(R.id.end);
        //获取SP
        sp1 = getSharedPreferences("front", MODE_PRIVATE);
        sp = getSharedPreferences("back", MODE_PRIVATE);
        String num1= sp1.getString("num","");
        String name1= sp1.getString("name","");
        String expiryDate1= sp.getString("expiryDate","");
        //赋值
        name.setText(name1);
        num.setText(num1);
        end.setText(expiryDate1);


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmActivity.this.finish();
                sp.edit().clear().commit();
                sp1.edit().clear().commit();
            }
        });
        findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConfirmActivity.this,AddBankActivity.class));
            }
        });

    }
}
