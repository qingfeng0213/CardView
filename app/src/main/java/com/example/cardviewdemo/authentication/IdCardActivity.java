package com.example.cardviewdemo.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import com.example.cardviewdemo.R;
import com.example.cardviewdemo.util.FileUtil;

import java.io.File;

public class IdCardActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 103;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 104;
    private SharedPreferences sp1;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        //获取SP
        sp = getSharedPreferences("front", MODE_PRIVATE);
        sp1 = getSharedPreferences("back", MODE_PRIVATE);

        // 初始化
        initAccessTokenWithAkSk();
        //点击事件
        setonClick();
    }
    //点击事件
    private void setonClick(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdCardActivity.this.finish();
            }
        });
        // 正面(自动)
        findViewById(R.id.id_card_front_button_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IdCardActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE, true);
                // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                // 请手动使用CameraNativeHelper初始化和释放模型
                // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);

                /*
                 * 手动识别（可展示照片供用户观看）
                 * */
//                Intent intent = new Intent(IdCardActivity.this, CameraActivity.class);
//                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
//                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
//                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
//                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });

        // 反面(自动)
        findViewById(R.id.id_card_back_button_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IdCardActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_NATIVE_ENABLE, true);
                // KEY_NATIVE_MANUAL设置了之后CameraActivity中不再自动初始化和释放模型
                // 请手动使用CameraNativeHelper初始化和释放模型
                // 推荐这样做，可以避免一些activity切换导致的不必要的异常
                intent.putExtra(CameraActivity.KEY_NATIVE_MANUAL, true);
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);

                /*
                * 手动识别（可展示照片供用户观看）
                * */
//                Intent intent = new Intent(IdCardActivity.this, CameraActivity.class);
//                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
//                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
//                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
//                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
    }
    //初始化
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(IdCardActivity.this).initAccessTokenWithAkSk(
                new OnResultListener<AccessToken>() {
                    @Override
                    public void onResult(AccessToken result) {

                        // 本地自动识别需要初始化
                        initLicense();

                        Log.d("MainActivity", "onResult: " + result.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(IdCardActivity.this, "初始化认证成功", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(IdCardActivity.this, "初始化认证失败,请检查 key", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, getApplicationContext(),
                // 需要自己配置 https://console.bce.baidu.com
                "88R6a3Fky9l5jcpOV0qNuFVh",
                // 需要自己配置 https://console.bce.baidu.com
                "a0GPi4bzLzuT9dDpU5Zp1Ce7InFDAjUw");
    }
    //初始化回调监听
    private void initLicense() {
        CameraNativeHelper.init(this, OCR.getInstance(IdCardActivity.this).getLicense(),
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
                                Toast.makeText(IdCardActivity.this,
                                        "本地质量控制初始化错误，错误原因： " + msg, Toast.LENGTH_SHORT).show();
                            }
                        });
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
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }

    }

    /**
     * 解析身份证图片
     *
     * @a0GPi4bzLzuT9dDpU5Zp1Ce7InFDAjUw param idCardSide 身份证正反面
     * @param filePath   图片路径
     */
    private void recIDCard(final String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(40);
        OCR.getInstance(IdCardActivity.this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {


            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {

                    String name = "";
                    String sex = "";
                    String nation = "";
                    String num = "";
                    String address = "";
                    String issueAuthority = "";
                    String expiryDate = "";
                    if (result.getName() != null) {
                        name = result.getName().toString();
                    }
                    if (result.getGender() != null) {
                        sex = result.getGender().toString();
                    }
                    if (result.getEthnic() != null) {
                        nation = result.getEthnic().toString();
                    }
                    if (result.getIdNumber() != null) {
                        num = result.getIdNumber().toString();
                    }
                    if (result.getAddress() != null) {
                        address = result.getAddress().toString();
                    }
                    if (result.getIssueAuthority() != null) {
                        issueAuthority = result.getIssueAuthority().toString();
                    }
                    if (result.getExpiryDate() != null) {
                        expiryDate = result.getExpiryDate().toString();
                    }
                    if (idCardSide.equals("front")){
                        Toast.makeText(IdCardActivity.this, "姓名: " + name + "\n" +
                                "性别: " + sex + "\n" +
                                "民族: " + nation + "\n" +
                                "身份证号码: " + num + "\n" +
                                "住址: " + address + "\n", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor= sp.edit();
                        editor.putBoolean("frontyes",true);
                        editor.putBoolean("",false);
                        editor.putString("name", name);
                        editor.putString("sex", sex);
                        editor.putString("address", address);
                        editor.putString("num", num);
                        editor.commit();
                    }else if (idCardSide.equals("back")){
                        Toast.makeText(IdCardActivity.this, "签发机关: " + issueAuthority + "\n" +
                                "到期时间: " + expiryDate + "\n" , Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor1= sp1.edit();
                        editor1.putString("expiryDate", expiryDate);
                        editor1.putBoolean("backyes",true);
                        editor1.commit();
                        getIntentOne();

                    }
                }
            }
            //判断跳转
            private void getIntentOne() {
                if (sp.getBoolean("frontyes",true) && sp1.getBoolean("backyes",true)){
                    startActivity(new Intent(IdCardActivity.this,ConfirmActivity.class));
                }
            }

            @Override
            public void onError(OCRError error) {
                Toast.makeText(IdCardActivity.this, "识别出错,请查看log错误代码", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "onError: " + error.getMessage());
            }
        });
    }
    @Override
    protected void onDestroy() {
        CameraNativeHelper.release();
        // 释放内存资源
        OCR.getInstance(IdCardActivity.this).release();
        super.onDestroy();

    }
}
