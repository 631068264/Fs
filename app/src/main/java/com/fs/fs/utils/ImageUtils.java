package com.fs.fs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by wyx on 2017/1/4.
 */

public class ImageUtils {
    public static Bitmap resize(String path, int weight, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options = calculateInSampleSize(options, weight, height);
        return BitmapFactory.decodeFile(path, options);
    }

    private static BitmapFactory.Options calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > 400 || width > 450) {
            if (height > reqHeight || width > reqWidth) {
                // 计算出实际宽高和目标宽高的比率
                int heightRatio = Math.round((float) height
                        / (float) reqHeight);
                int widthRatio = Math.round((float) width
                        / (float) reqWidth);
                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
                // 一定都会大于等于目标的宽和高。
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
        }
        // 设置压缩比例
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return options;
    }

}
