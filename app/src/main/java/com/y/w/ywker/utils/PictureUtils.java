package com.y.w.ywker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lxs on 16/5/17.
 */
public class PictureUtils {

    /**
     * 把bitmap转换成文件并返回路径
     *
     * @param filePath
     * @return
     */
    public static String compressPic(String filePath,String newFileName) {

        Bitmap bm = getSmallBitmap(filePath);
        String littlePath = "";

        try {
            File sFile = new File(filePath);
            if (!sFile.exists()){
                return filePath;
            }
            littlePath = sFile.getParent() + "/" + newFileName;
            Log.e("TAG","小文件路径 = " + littlePath);
            File saveFile = new File(littlePath);
            if (!saveFile.exists()){
                if(!saveFile.createNewFile()){
                    Log.e("TAG","创建文件失败 : " + littlePath);
                    return filePath;
                }
            }

            Log.e("TAG","将图片内容写入到文件..");
            FileOutputStream foutStream = new FileOutputStream(saveFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 40, foutStream);
            foutStream.flush();
            foutStream.close();
            bm.recycle();
        }catch (Exception e){
            Log.e("TAG","图片压缩存储失败");
            return filePath;
        }
        return littlePath;
    }

    /**
     * 根据路径获得图片并压缩返回bitmap
     *
     * @param filePath
     * @return
     */

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 240, 400);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static byte[] Bitmap2Stream(Bitmap bitmap){
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
        bitmap.recycle();//自由选择是否进行回收
        byte[] result = output.toByteArray();//转换成功了
        try {
            output.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;

    }

    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    public static Bitmap url2Bitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
//            HttpURLConnection conn = (HttpURLConnection) fileUrl
//                    .openConnection();
//            conn.setDoInput(true);
//            conn.connect();
            InputStream is =  fileUrl.openStream();

            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
 }
