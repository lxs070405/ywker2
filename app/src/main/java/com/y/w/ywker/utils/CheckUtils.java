package com.y.w.ywker.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**校验工具类*/
public class CheckUtils {
	/**
	 * @description 判断是否是有效的手机号码
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {

		String regExp = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\\\d{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(phoneNumber);
		return m.find();//boolean
	}

	/**
	 * 校验密码强度必须是包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间
	 * @param str
	 * @return
     */
	public static boolean isPSDValid(String str){
		String string = "^(?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
		Pattern p = Pattern.compile(string);
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 校验url是否合法
	 * 
	 * @param url
	 *            url是否合法
	 * @return true-合法，false-不合法
	 */
	public static boolean isMatchURL(String url) {
		String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
				+ "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
				+ "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
				+ "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
				+ "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
				+ "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
				+ "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
				+ "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
		return url.matches(regEx);
	}

	/**
	 * @description 检测SD卡是否存在
	 * @return
	 */
	public static boolean checkSDCard() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static void saveBitmap(String path, Bitmap bm, String name) {
		// Log.d("TaAG", "here is the saveBitmap===>");
		if (bm == null) {
			// Log.d("TaAG", "the ____ bm is null____----- ");
			return;
		}
		File f = new File(path, name);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// Log.d("TaAG",
			// "FileNotFoundException here is the save bitmap error===>" + e);
		} catch (IOException e) {
			e.printStackTrace();
			// Log.d("TaAG", "IOException here is the save bitmap error===>" +
			// e);
		}

	}


	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 防止用户快速点击按钮
	 */
	private static long lastClickTime;

	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isEmail(String mail) {
		String regExp = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(mail);
		return m.find();
	}
}
