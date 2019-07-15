package com.example.cardviewdemo.authentication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cardviewdemo.R;
import com.lljjcoder.citypickerview.widget.CityPicker;

import cn.qqtheme.framework.picker.OptionPicker;


public class PersoonalActivity extends AppCompatActivity {


    private TextView select_address;
    private TextView select_profession;
    private SharedPreferences mysp;
    private SharedPreferences addressp;
    private SharedPreferences.Editor myeditor;
    private SharedPreferences.Editor addressedit;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persoonal);
        TextView name = findViewById(R.id.name);
        TextView sex = findViewById(R.id.sex);
        TextView address = findViewById(R.id.address);
        TextView num = findViewById(R.id.num);
        TextView end = findViewById(R.id.end);
        select_address = findViewById(R.id.select_address);
        select_profession = findViewById(R.id.select_profession);
        LinearLayout mymessage = findViewById(R.id.mymessage);
        LinearLayout profession = findViewById(R.id.profession);
        LinearLayout myaddress = findViewById(R.id.myaddress);
        //获取SP
        SharedPreferences sp=getSharedPreferences("front", MODE_PRIVATE);
        SharedPreferences sp1=getSharedPreferences("back", MODE_PRIVATE);
        if (sp.getBoolean("frontyes",false) && sp1.getBoolean("backyes",false)){
            mymessage.setVisibility(View.VISIBLE);
            String num1=sp.getString("num","");
            String name1=sp.getString("name","");
            String sex1=sp.getString("sex","");
            String address1=sp.getString("address","");
            String expiryDate1=sp1.getString("expiryDate","");
            name.setText(name1);
            sex.setText(sex1);
            address.setText(address1);

            num.setText(hideId(num1));
            end.setText(expiryDate1);
        }else {
            profession.setVisibility(View.VISIBLE);
            myaddress.setVisibility(View.VISIBLE);
        }
        sp.edit().clear();
        sp1.edit().clear();
        sp.edit().commit();
        sp1.edit().commit();

        setonClick();



        SharedPreferences profession1 = getSharedPreferences("profession", MODE_PRIVATE);
        SharedPreferences addresssp1 = getSharedPreferences("address", MODE_PRIVATE);
        String option = profession1.getString("option","");
        String province = addresssp1.getString("province", "");
        String city = addresssp1.getString("city", "");
        String district = addresssp1.getString("district", "");
        select_address.setText(province.trim()   +   "-"   +   city.trim()   +   "-"   +   district.trim());
        select_address.setTextColor(R.color.over);
        select_profession.setText(option);
        select_profession.setTextColor(R.color.over);
    }



    //点击事件
    private void setonClick(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersoonalActivity.this.finish();
            }
        });
        select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddress();

            }
        });
        select_profession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profession();
            }
        });

    }
    //隐藏身份证信息
    public static String hideId(String num){
        StringBuilder stringBuilder = new StringBuilder(num);
        stringBuilder.replace(1, 16, "******************");
        return stringBuilder.toString();
    }
    //职业选择器
    @SuppressLint("ResourceAsColor")
    private void profession(){
        OptionPicker picker = new OptionPicker(this, new String[]{
                "军人", "生产制造有关人员", "专业技术人员","办事人员和有关人员","社会生产服务和生活服务人员",
                "农、林、牧、渔业生产及辅助人员","党的机关、国家机关、群众团体和社会组织、企事业单位负责人",
                "不便分类的其他从业人员"
        });
        picker.setOffset(2);
        picker.setSelectedIndex(4);
        picker.setTextSize(15);
        picker.setLineColor(R.color.colorAccent);
        picker.setTextColor(R.color.colorAccent);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(String option) {

                if (option != null) {
                    mysp = getSharedPreferences("profession", MODE_PRIVATE);
                    myeditor = mysp.edit();
                    select_profession.setText(option);
                    select_profession.setTextColor(R.color.over);
                    myeditor.putString("option",option);
                    myeditor.clear().commit();
                }else {
                    select_profession.setText("未填写");
                }
            }
        });
        picker.show();
    }
    //地址选择器
    private void getAddress()   {
        CityPicker cityPicker=new CityPicker.Builder(PersoonalActivity.this)
                .textSize(14)
                .title("地址选择")
                .titleBackgroundColor("#FFFFFF")
                .confirTextColor("#F85B75")
                .cancelTextColor("#333333")
                .province("江苏省")
                .city("常州市")
                .district("天宁区")
//                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();

        //监听事件，获取结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener()   {



            @SuppressLint("ResourceAsColor")
            @Override
            public void onSelected(String...   citySelected)   {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];
                if (province != null && city != null && district != null) {
                    //为展示区赋值
                    select_address.setText(province.trim()   +   "-"   +   city.trim()   +   "-"   +   district.trim());
                    select_address.setTextColor(R.color.over);
                    addressp = getSharedPreferences("address", MODE_PRIVATE);
                    addressedit = addressp.edit();
                    addressedit.putString("province",province);
                    addressedit.putString("city",city);
                    addressedit.putString("district",district);
                    addressedit.clear().commit();
                }else {
                    select_address.setText("未填写");
                }
            }
        });
    }
}
