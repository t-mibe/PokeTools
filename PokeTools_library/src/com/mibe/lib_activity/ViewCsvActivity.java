package com.mibe.lib_activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mibe.pt_library.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
	
	private final Context context = this;

	// トースト出力の表示時間
	private static final int duration = Toast.LENGTH_SHORT;

	// ホームディレクトリのパス
	private String homeDir;

	// レベルの値
	private int level;

	// ファイルパス
	private String filePath;

	// 展開したCSV全データ
	private List<Serializable> list_data;
	
	// 選択モードフラグ（本来はfalse）
	private boolean select = true;

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


		//showSettings();
		//showDummyList();
	}

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

		// ファイルの有無をチェックする
		if(!(new File(filePath).isFile()))return;

		CSVReader reader = null;

		try {
			reader = new CSVReader(new InputStreamReader(new FileInputStream(filePath),"Shift_JIS"));

			list_data = new ArrayList<Serializable>();

			String[] record;
			while((record = reader.readNext()) != null){

				Serializable serializable = makeBean(record);
				list_data.add(serializable);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// レコードを対応したJavaBeanに変換する
	public abstract Serializable makeBean(String[] record);

	// 表示する
	private void showList(){

		if(list_data == null)return;
		if(list_data.size() == 0)return;

		// ListViewのスクロールバーにつまみをつける
		getListView().setFastScrollEnabled(true);

		// 表示用の配列
		List<String> list_view = new ArrayList<String>();

		int size = list_data.size();
		for(int i = 0; i < size; i++){
			list_view.add(list_data.get(i).toString());
		}

		// Beanに展開してから表示したい
		setListAdapter(new ArrayAdapter<String>(this, R.layout.listview, list_view));
	}

	//////////////////////////////////
	// アイテムが選択された時の処理 //
	//////////////////////////////////

	// アイテムが選択された時の処理（必要ならオーバーライドする）
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id){
		super.onListItemClick(listView, listView, position, id);

		// 選択したアイテムを表示するダイアログを作成，表示する
		onListItemClick(list_data.get(position));
	}
	
	// 指定したレコードに対応した処理（必要ならオーバーライドする
	public void onListItemClick(Serializable record){
		// 選択したアイテムを表示するダイアログを作成，表示する
		showItemDialog(record);
	}

	// 選択したアイテムに対応したダイアログを表示する
	private void showItemDialog(Serializable record){

		
		// ダイアログのタイトルを作成する
		View title = getItemDialogTitle(record);

		// ダイアログのビューを作成する
		View view = getItemDialogView(record);
		
		// ダイアログを作成する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setCustomTitle(title);
		builder.setView(view);
		builder.setNegativeButton("閉じる", new OnItemCloseClickListener());
		
		// 選択モードのとき，決定ボタンを追加する
		if(select)builder.setPositiveButton("決定", new OnItemSelectClickListener());
		
		// 画面の回転ロックを有効にする
		setOrientation(true);
		
		// ダイアログを表示する
		builder.show();
	}
	
	// ダイアログのタイトルを作成する
	public View getItemDialogTitle(Serializable record){
		
		TextView textView = new TextView(this);
		textView.setText(record.toString());
		
		return textView;
	}

	// ダイアログのビューを作成する
	public View getItemDialogView(Serializable record){
		
		TextView textView = new TextView(this);
		textView.setText(record.toString());
		
		return textView;
	}
	
	// ダイアログのボタンを押したときの動作
	private class OnItemClickListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// 画面の回転ロックを解除する
			setOrientation(false);
		}
	}
	
	// アイテムの閉じるボタンを押したときの動作
	private class OnItemCloseClickListener extends OnItemClickListener{
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			super.onClick(dialog, which);
			Toast.makeText(context, "close", duration).show();
		}
	}
	
	// アイテムの決定ボタンを押したときの動作
	private class OnItemSelectClickListener extends OnItemClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			super.onClick(dialog, which);
			Toast.makeText(context, "select", duration).show();
		}
	}
	
	// 画面の回転ロックを制御する
	private void setOrientation(boolean mode){
		if(mode){
			Toast.makeText(context, "spin: lock", duration).show();
		} else {
			Toast.makeText(context, "spin: unlock", duration).show();
		}
	}
	
	//////////////
	// 情報取得 //
	//////////////
	
	public String getHomeDir(){
		return homeDir;
	}
	
	public Drawable getDefaultIcon(){
		return getResources().getDrawable(R.drawable.ic_launcher);
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

