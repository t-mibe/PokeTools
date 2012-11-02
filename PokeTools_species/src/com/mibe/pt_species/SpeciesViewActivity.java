package com.mibe.pt_species;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.Toast;

public class SpeciesViewActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.species);
		
		// ホームアプリのContext
		Context hContext = null;

		try {
			// ホームアプリのContextを取得する
			hContext = createPackageContext("com.mibe.pt_home", CONTEXT_RESTRICTED);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// エラー処理
		if(hContext == null)return;

		// ホームアプリの設定オブジェクトを取得する
		SharedPreferences sp = hContext.getSharedPreferences("MainActivity", MODE_MULTI_PROCESS);

		// ホームディレクトリのパスを取得する
		String homeDir = sp.getString("key_homedir", "");

		Toast.makeText(this, homeDir, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
