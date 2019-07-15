package com.example.cardviewdemo.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GetBankNameUtil {


    private static String bankName;
    private static String binName;

    private static String openBinNum(Context context) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String str = null;
        try {
            //打开资源目录文件，获取银行卡号对应的查询库
            InputStream is = context.getResources().getAssets().open("binNum.txt");
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }
            is.close();
            outputStream.close();
            str = outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    //获得Bank card的前缀
    private static List<Long> getBinNum(Context context) {
        String binNum = openBinNum(context);
        String[] binArr = binNum.split(",");
        List<Long> lon = new ArrayList<>();
        for (int i = 0; i < binArr.length; i++) {
            if (i % 2 == 0)
                lon.add(Long.parseLong(binArr[i]));
        }
        return lon;
    }
    //获得BankName
    private static List<String> getBinName(Context context) {
        String binNum = openBinNum(context);
        String[] binArr = binNum.split(",");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < binArr.length; i++) {
            if (i % 2 != 0) {
                if (binArr[i].contains("-")) {
                    //截取首尾部分
                    bankName = binArr[i].substring(0, binArr[i].indexOf("-")) + binArr[i].substring(binArr[i].lastIndexOf("-"));
                } else {
                    bankName = binArr[i];
                }
                list.add(bankName);
            }
        }
        return list;
    }
    //通过输入的卡号获得银行卡信息
    public static String getNameOfBank(Context context, long binNum) {
        int index = 0;
        index = binarySearch(getBinNum(context), binNum);
        //银行名
        if (index!=-1){
            binName = getBinName(context).get(index);
            return binName + "\n";
        }
        return "未查询到相关银行信息\n";
    }
    //数量有上千条，利用二分查找算法来进行快速查找法
    public static int binarySearch(List<Long> srcArray, long des) {
        int low = 0;
        int high = srcArray.size() - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (des == srcArray.get(middle)) {
                return middle;
            } else if (des < srcArray.get(middle)) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return -1;
    }
}
