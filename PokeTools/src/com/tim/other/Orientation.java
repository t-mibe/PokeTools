package com.tim.other;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class Orientation {
	
	/**
	 * 画面の向きを固定する
	 */
	public static boolean lockOrientation(Activity activity){

		// Activityの状態を取得する
		Configuration configuration = activity.getResources().getConfiguration();

		int orientation = configuration.orientation;

		switch (orientation) {
		default:
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			
			// 画面が縦向きの時
			if(isScreenReverse(activity)){
				activity.setRequestedOrientation(
						ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			break;
		case Configuration.ORIENTATION_LANDSCAPE:

			// 画面が横向きの時

			// 画面が逆向きかで分岐
			if(isScreenReverse(activity)){
				activity.setRequestedOrientation(
						ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			break;
		}

		return false;
	}
	
	/**
	 * 画面が縦向きかチェックする
	 * @param activity	: 確認したいActivity（thisでいい）
	 * @return			: 縦向きならtrue
	 */
	public static boolean isPortrait(Activity activity){
		
		// 画面の向きを取得する
		int orientation = getOrientation(activity);
		
		// 画面の向きによって分岐する
		switch (orientation) {
		default:
			return false;
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
			return true;
		case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
			return false;
		}
	}
	
	/**
	 * 画面の向きを取得する
	 * 形式は ActivityInfo.SCREEN_ORIENTATION_???
	 */
	public static int getOrientation(Activity activity){
		
		// 結果保存用
		int result = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		
		// Activityの状態を取得する
		Configuration configuration = activity.getResources().getConfiguration();
		
		// 画面の向きを保存する（逆向き判別不可）
		int orientation = configuration.orientation;
		
		// 画面の逆向き判定を取得する
		boolean reverse = isScreenReverse(activity);
		
		// 画面の向きで分岐する
		switch(orientation){
		default:
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			
			// 画面が縦向きのとき
			
			// 逆向き判定によって分岐する
			if(reverse) result = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
			else result = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			
			// 画面が横向きのとき
			
			// 逆向き判定によって分岐する
			if(reverse) result = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			else result = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			
			break;
		}
		
		return result;
	}
	
	/**
	 * 画面が逆向きかを調べる
	 * @return : 逆向きならTrue
	 */
	private static boolean isScreenReverse(Activity activity){

		final boolean isReverse;
		final int rotation;

		Display display = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();

		Method method = null;
		Method[] declaredMethods = display.getClass().getDeclaredMethods();
		for (Method m : declaredMethods) {
			if (m.getName().equals("getRotation")) {
				method = m;
				break;
			}
		}

		//Android2.1以前ではgetRotationメソッドがない = Reverseなし
		if (method == null){
			return false;
		}

		//getRotationメソッドを実行
		try {
			rotation = (Integer) method.invoke(display, (Object[])null);
		} catch (Exception e) {
			return false;
		}

		//逆向きならtrueを返す
		switch(rotation){
		case Surface.ROTATION_180:
		case Surface.ROTATION_270:
			isReverse = true;
			break;
		default:
			isReverse = false;
			break;
		}

		return isReverse;
	}
}

