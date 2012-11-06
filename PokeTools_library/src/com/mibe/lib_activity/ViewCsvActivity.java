package com.mibe.lib_activity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mibe.pt_library.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

/**
 * CSVファイルを表示するだけのListActivity
 * 編集しない，並び替えないこと前提
 * 
 * TODO 回転禁止について
 * ダイアログ表示中は画面の回転を禁止すること
 * ダイアログの各処理で回転禁止を解除すること
 * 
 * @author mibe
 *
 */
public abstract class ViewCsvActivity extends ListActivity {

	// トースト出力の表示時間
	private static final int duration = Toast.LENGTH_SHORT;

	// ホームディレクトリのパス
	private String homeDir;

	// レベルの値
	private int level;

	// ファイルパス
	private String filePath;
	
	// 展開したCSV全データ
	private List<String[]> list_data;

	//////////////////
	// Activity制御 //
	//////////////////

	// アクティビティ作成時の処理
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// 初回起動時のみ行う処理
		//if(savedInstanceState == null){}

		// 設定パラメータを取得する
		getSettings();

		// 表示するファイルのローカルパスを設定する
		filePath = homeDir.concat(getLocalPath());
		
		// ファイルを展開する
		readCsv();
		
		// 表示するテキスト配列を生成する
		
		// 展開したデータを表示する
		showList();
		

		showSettings();
		showDummyList();
	}

	/* 
	// Bundleに状態を保存，いらないかも
	@Override  
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// 展開するCSVファイルのフルパスを保存する
		//outState.putString(getString(R.string.key_filePath), filePath);

		// スクロール位置を保存する
		//outState.putInt(getString(R.string.key_scroll), getListView().getFirstVisiblePosition());
	}

	// Bundleから状態を復元
	@Override  
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// 展開するCSVファイルのフルパスを取得する
		
		// ListViewを作成，表示する

		// スクロール位置を復元する
	}
	*/

	////////////////
	// 初期化関連 //
	////////////////

	// 設定パラメータを取得する
	private void getSettings(){

		// ホームアプリのContextを取得する
		Context hContext = getHomeAppContext();

		// ホームアプリのContextを取得する，結果によって分岐
		if((hContext = getHomeAppContext()) != null){

			// Contextを取得できたとき，設定オブジェクトからパラメータを取得する
			getSharedPrefernces(hContext);
		} else {

			// Contextを取得できなかったとき，設定にデフォルト値を代入する
			setDefaultSettings();
		}
	}

	// ホームアプリのContextを取得する
	private Context getHomeAppContext(){

		// ホームアプリのContext
		Context hContext = null;

		// ホームアプリのパッケージ名を取得する
		String hPackage = getString(R.string.homeapp_package);

		try {
			// ホームアプリのContextを取得する
			hContext = createPackageContext(hPackage, CONTEXT_RESTRICTED);
		} catch (NameNotFoundException e) {
			Toast.makeText(this, "パッケージ名\n\"".concat(hPackage).concat("\"\nがありません"), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		return hContext;
	}

	// 設定オブジェクトからパラメータを取得する
	private void getSharedPrefernces(Context hContext){

		// ホームアプリの設定オブジェクトを取得する
		SharedPreferences sp = hContext.getSharedPreferences(getString(R.string.homeapp_class), MODE_MULTI_PROCESS);

		// ホームディレクトリのパスを取得する
		homeDir = sp.getString(getString(R.string.key_homeDir), getString(R.string.default_homeDir));

		// レベルの値を取得する
		level = sp.getInt(getString(R.string.key_level), Integer.valueOf(this.getString(R.string.default_level)));
	}

	// 設定にデフォルト値を代入する
	private void setDefaultSettings(){

		// ホームディレクトリのパスを取得する
		homeDir = getString(R.string.default_homeDir);

		// レベルの値を取得する
		level = Integer.valueOf(this.getString(R.string.default_level));
	}

	// 表示するファイルのローカルパスを設定する
	public abstract String getLocalPath();
	
	// ファイルを展開する
	private void readCsv(){
		CSVReader reader = null;

		try {
			reader = new CSVReader(new FileReader(filePath));
			list_data = reader.readAll();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 表示する
	private void showList(){
		
		// Beanに展開してから表示したい
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.listview, list_data.toArray(new String[0])));
	}

	//////////////////////////////////
	// アイテムが選択された時の処理 //
	//////////////////////////////////

	// アイテムが選択された時
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id){
		super.onListItemClick(listView, listView, position, id);

		// 種族データを表示するダイアログを作成，表示する
		Toast.makeText(this, listView.getItemAtPosition(position).toString(), duration).show();
	}

	//////////////////
	// デバッグ処理 //
	//////////////////

	// デバッグ用処理: 設定を表示する
	public void showSettings(){

		// テキストを作成する
		String text = "homeDir = \"".concat(homeDir).concat("\"")
				.concat("\nlevel = ").concat(String.valueOf(level))
				.concat("\nfilePath = \"").concat(filePath).concat("\"");

		// トースト出力する
		Toast.makeText(this, text, duration).show();
	}

	// デバッグ処理: ダミーのArrayListを表示する
	public void showDummyList(){

		// ListViewのスクロールバーにつまみをつける
		getListView().setFastScrollEnabled(true);

		// 表示するArrayListを作成する
		ArrayList<String> arrayList = new ArrayList<String>();
		for(int i = 0; i < 1000; i++){
			arrayList.add(String.valueOf(i));
		}

		// ArrayListを表示する
		setListAdapter(new ArrayAdapter<String>(this, R.layout.listview, arrayList));
	}

}

