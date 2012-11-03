package com.mibe.pt_library;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.Toast;

public abstract class ViewCsvActivity extends ListActivity {

	// トースト出力の表示時間
	private static final int duration = Toast.LENGTH_SHORT;

	// ホームディレクトリのパス
	private String homeDir = "";

	// レベルの値
	private int level = 50;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// 設定パラメータを取得する
		getSettings();

		showSettings();
	}

	// 設定パラメータを取得する
	private void getSettings(){

		// ホームアプリのパッケージ名を取得する
		String hPackage = getString(R.string.homeapp_package);

		// ホームアプリのContext
		Context hContext = null;

		try {
			// ホームアプリのContextを取得する
			hContext = createPackageContext(hPackage, CONTEXT_RESTRICTED);
		} catch (NameNotFoundException e) {
			Toast.makeText(this, "パッケージ名\n\"".concat(hPackage).concat("\"\nがありません"), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// エラーチェック
		if(hContext == null)return;

		// ホームアプリの設定オブジェクトを取得する
		SharedPreferences sp = hContext.getSharedPreferences(getString(R.string.homeapp_class), MODE_MULTI_PROCESS);

		// ホームディレクトリのパスを取得する
		homeDir = sp.getString(getString(R.string.key_homedir), getString(R.string.default_homeDir));

		// レベルの値を取得する
		level = sp.getInt(getString(R.string.key_level), Integer.valueOf(this.getString(R.string.default_level)));
	}

	// デバッグ用：設定パラメータを出力する
	public void showSettings(){

		// テキストを作成する
		String text = "homeDir = \"".concat(homeDir).concat("\"\nlevel = ").concat(String.valueOf(level));

		// トースト出力する
		Toast.makeText(this, text, duration).show();
	}

}

