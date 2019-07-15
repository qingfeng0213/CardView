package com.example.cardviewdemo.bankcard;
/*
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *     ┃　　　┃
 *     ┃　　　┃
 *     ┃　　　┗━━━┓
 *     ┃　　　　　　　┣┓
 *     ┃　　　　　　　┏┛
 *     ┗┓┓┏━┳┓┏┛
 *       ┃┫┫　┃┫┫
 *       ┗┻┛　┗┻┛
 *        神兽保佑
 *        代码无BUG!
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import com.example.cardviewdemo.R;
import com.example.cardviewdemo.authentication.IdCardActivity;
import com.example.cardviewdemo.util.FileUtil;
import com.example.cardviewdemo.util.GetBankNameUtil;
import com.example.cardviewdemo.util.RegexUtils;

import java.io.File;

public class AddBankActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 103;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 104;
    int beforeTextLength = 0;
    int onTextLength = 0;
    private EditText bank_card;
    private EditText phone_num;
    private Button bank_msg;
    private SharedPreferences banksp;
    private SharedPreferences hand_banksp;
    private SharedPreferences sp;
    private TextView card_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        bank_card = findViewById(R.id.bank_card);
        phone_num = findViewById(R.id.pohne_num);
        bank_msg = findViewById(R.id.bank_msg);
        card_name = findViewById(R.id.card_name);
        bank_card.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        phone_num.setInputType(EditorInfo.TYPE_CLASS_PHONE);
//        bank_card.addTextChangedListener(watcher);
        initView();
        //获取SP
        banksp = getSharedPreferences("bank", MODE_PRIVATE);
        hand_banksp = getSharedPreferences("handbank", MODE_PRIVATE);
        //获取身份证名字
        sp = getSharedPreferences("front", MODE_PRIVATE);
        String name1= sp.getString("name","");
        card_name.setText("请绑定"+name1+"的银行卡");
        sp.edit().commit();
        setOnClick();
        initAccessTokenWithAkSk();

    }

    //点击事件
    private void setOnClick(){
        //验证信息的点击事件
        bank_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myphone = phone_num.getText().toString();
                String banknum = bank_card.getText().toString();
                if(myphone!=null&&myphone.length()>=11) {
                    //这里面调用 工具类里面 手机号的方法
                    boolean checkPhone = RegexUtils.checkPhone(myphone.toString());
                    if (checkPhone) {
                        startActivity(new Intent(AddBankActivity.this,VerifyBankActivity.class));
                    }
                }else {
                    Toast.makeText(AddBankActivity.this, "手机号输入有误，请重新输入", Toast.LENGTH_SHORT).show();
                    phone_num.getText().clear();
                }
                if(banknum!=null&&banknum.length()>=19) {
                    //这里面调用 工具类里面 手机号的方法
                    boolean checkBankCard = RegexUtils.checkBankCard(banknum.toString());
                    if (checkBankCard) {
                        startActivity(new Intent(AddBankActivity.this,VerifyBankActivity.class));
                    }
                }else {
                    Toast.makeText(AddBankActivity.this, "银行卡输入有误，请重新输入", Toast.LENGTH_SHORT).show();
                    phone_num.getText().clear();
                }
            }
        });

        //调起相机获取银行卡信息
        findViewById(R.id.bank_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBankActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBankActivity.this.finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_BANK_CARD.equals(contentType)) {
                        recCreditCard(filePath);
                    }
                }
            }
        }
    }
    //初始化
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(AddBankActivity.this).initAccessTokenWithAkSk(
                new OnResultListener<AccessToken>() {
                    @Override
                    public void onResult(AccessToken result) {

                        // 本地自动识别需要初始化
                        initLicense();

                        Log.d("MainActivity", "onResult: " + result.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddBankActivity.this, "初始化认证成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(OCRError error) {
                        error.printStackTrace();
                        Log.e("MainActivity", "onError: " + error.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddBankActivity.this, "初始化认证失败,请检查 key", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, getApplicationContext(),
                // 需要自己配置 https://console.bce.baidu.com
                "88R6a3Fky9l5jcpOV0qNuFVh",
                // 需要自己配置 https://console.bce.baidu.com
                "a0GPi4bzLzuT9dDpU5Zp1Ce7InFDAjUw");
    }
    private void initLicense() {
        CameraNativeHelper.init(this, OCR.getInstance(AddBankActivity.this).getLicense(),
                new CameraNativeHelper.CameraNativeInitCallback() {
                    @Override
                    public void onError(int errorCode, Throwable e) {
                        final String msg;
                        switch (errorCode) {
                            case CameraView.NATIVE_SOLOAD_FAIL:
                                msg = "加载so失败，请确保apk中存在ui部分的so";
                                break;
                            case CameraView.NATIVE_AUTH_FAIL:
                                msg = "授权本地质量控制token获取失败";
                                break;
                            case CameraView.NATIVE_INIT_FAIL:
                                msg = "本地质量控制";
                                break;
                            default:
                                msg = String.valueOf(errorCode);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(AddBankActivity.this,
//                                        "本地质量控制初始化错误，错误原因： " + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
    /**
     * 解析银行卡
     *
     * @param filePath 图片路径
     */
    private void recCreditCard(String filePath) {
        // 银行卡识别参数设置
        BankCardParams param = new BankCardParams();
        param.setImageFile(new File(filePath));

        // 调用银行卡识别服务
        OCR.getInstance(AddBankActivity.this).recognizeBankCard(param, new OnResultListener<BankCardResult>() {
            @Override
            public void onResult(BankCardResult result) {
                if (result != null) {

                    String type;
                    if (result.getBankCardType() == BankCardResult.BankCardType.Credit) {
                        type = "信用卡";
                    } else if (result.getBankCardType() == BankCardResult.BankCardType.Debit) {
                        type = "借记卡";
                    } else {
                        type = "不能识别";
                    }
                    bank_card.addTextChangedListener(watcher);
                    bank_card.setText( result.getBankCardNumber());
                    String bankCardNumber = result.getBankCardNumber();

                    String bankName = result.getBankName();
                    SharedPreferences.Editor editor= banksp.edit();
                    editor.putBoolean("",true);
                    editor.putString("cardnum",bankCardNumber);
                    editor.putString("bankname",bankName);
                    editor.putString("type",type);
                    editor.clear().commit();
                }
            }

            @Override
            public void onError(OCRError error) {
                Toast.makeText(AddBankActivity.this, "识别出错,请查看log错误代码", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "onError: " + error.getMessage());
            }
        });
    }
   // 对输入的银行卡号进行分割，每四个数字隔开
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString().trim().replace(" ", "");
            String result = "";
            if (str.length() >= 4) {
                bank_card.removeTextChangedListener(watcher);
                for (int i = 0; i < str.length(); i++) {
                    result += str.charAt(i);
                    if ((i + 1) % 4 == 0) {
                        result += " ";
                    }
                }
                if (result.endsWith(" ")) {
                    result = result.substring(0, result.length() - 1);
                }
                bank_card.setText(result);
                bank_card.addTextChangedListener(watcher);
                bank_card.setSelection(bank_card.getText().toString().length());//焦点到输入框最后位置
            }
        }
    };
    private void initView() {


        bank_card.addTextChangedListener(new TextWatcher() {

            private String bankName;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("tag", "输入的数字：" + s);
                String bankNumber = bank_card.getText().toString().trim();
                onTextLength = bankNumber.length();
                if (onTextLength > beforeTextLength) {
                    if (bankNumber.length() == 5 || bankNumber.length() == 10 || bankNumber.length() == 15 || bankNumber.length() == 20) {
                        bank_card.setText(new StringBuffer(bankNumber).insert(
                                bankNumber.length() - 1, " ").toString());
                        bank_card.setSelection(bank_card.getText()
                                .length());
                    }
                } else {
                    if (bankNumber.startsWith(" ")) {
                        bank_card.setText(new StringBuffer(bankNumber).delete(
                                onTextLength - 1, onTextLength).toString());
                        bank_card.setSelection(bank_card.getText()
                                .length());
                    }
                }
                //根据银行卡号前6位查询银行信息
                if (bankNumber.length() >= 7) {
                    //移除空格
                    String noSpaceNumber = removeAllSpace(bankNumber);
                    //截取前6位
                    String divideStr = noSpaceNumber.substring(0, 6);
                    //通过银行卡号前6位查询开户行信息
                    bankName = GetBankNameUtil.getNameOfBank(AddBankActivity.this, Long.parseLong(divideStr));
                }
                SharedPreferences.Editor editor= hand_banksp.edit();
                editor.putString("bankNumber",bankNumber);
                editor.putString("handname",bankName);
                editor.putBoolean("",true);
                editor.clear().commit();
            }
        });
    }
    /**
     * 移除空格
     * @param str
     * @return
     */
    public String removeAllSpace(String str) {
        String tmpStr = str.replace(" ", "");
        return tmpStr;
    }

}
