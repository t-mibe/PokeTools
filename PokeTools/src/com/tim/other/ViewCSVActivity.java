package com.tim.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tim.poketools.R;

public abstract class ViewCSVActivity extends ListActivity {

	// レコードの長さ
	public int record_length = 0;

	// ステータス計算時のレベル
	public int level = 50;

	// 閲覧するCSVファイルのパス
	public String dataPath = "";

	// 表示用の配列
	public ArrayList<String> list_short;

	// 編集中のCSVデータを保存する配列
	public ArrayList<String> list_long;

	////////////////////////////////////////
	// ここからオーバーライドメソッド宣言 //
	////////////////////////////////////////

	/**
	 * アクティビティ作成時
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		// スーパークラスの処理
		super.onCreate(savedInstanceState);

		// アクティビティ作成時に独自に行う処理
		init();
	}

	// アイテムが選択された時
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id){
		super.onListItemClick(listView, listView, position, id);

		// 種族データを表示するダイアログを作成，表示する
		onItemClicked(position);
	}


	////////////////////////
	// ここから初期化関連 //
	////////////////////////

	// アクティビティ作成時に独自に行う処理
	private void init(){

		// 画面の向きを固定する
		Orientation.lockOrientation(this);

		// 設定情報を取得し変数に格納する
		setOption();

	}

	// 設定情報を取得し変数に格納する
	private void setOption(){

		// 設定オブジェクトを取得
		SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

		// ホームディレクトリを取得
		String homeDir = sp.getString(this.getString(R.string.key_homedir), "");

		// 展開するCSVファイルのパスを設定する
		dataPath = homeDir.concat(getString(R.string.path_race));

		//FIXME レベルの値を取得する
		level = sp.getInt(this.getString(R.string.key_level), 50);
	}

	//////////////////////////
	// ここから初期設定関連 //
	//////////////////////////

	/**
	 * リソースIDから編集するCSVファイルのフルパスを設定する
	 * @param id : ファイルパスが記述されているリソースID
	 * @return   : ファイルとして有効ならTrue
	 */
	public boolean setDataPath(int id){
		return setDataPath(getString(id));
	}

	/**
	 * 編集するCSVファイルのフルパスを設定する
	 * @param localPath : ホームディレクトリ以降のファイルパス
	 * @return          : ファイルが存在すればtrue
	 */
	public boolean setDataPath(String localPath){

		// ホームディレクトリのパスを取得する
		String homeDir = getHomeDirPath();

		// 編集するファイルのパスを取得する
		dataPath = homeDir.concat(localPath);

		// ファイルが存在するかをチェックする
		return (new File(dataPath)).isFile();
	}

	private String getHomeDirPath(){
		SharedPreferences sp = 
				getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);

		return sp.getString(this.getString(R.string.key_homedir), "");
	}

	/////////////////////////
	// ここからCSV読込関連 //
	/////////////////////////

	/**
	 * CSVファイルを展開し，リストビューに表示する
	 */
	public void openCSV(){

		// CSVファイルの展開を試みる
		trySetArrayList();

		// DragListViewを作成し表示する
		setListView();
	}

	/**
	 * CSVファイルの展開を試みる
	 * 失敗した場合，エラーを出力して終了する
	 */
	private void trySetArrayList(){

		// CSVファイルを読み込む
		if(!setArrayList()){

			// CSVファイルの読み込みに失敗したとき

			// トースト出力
			Toast.makeText(this,
					"CSVファイル\n\""
					.concat(dataPath)
					.concat("\"\nを開けませんでした"),
					Toast.LENGTH_LONG).show();

			finish();
			return;
		}

		/**
		// CSVファイルの読み込み成功をトースト出力する
		Toast.makeText(this, 
				"CSVファイル\n\""
				.concat(dataPath)
				.concat("\"\nをロードしました"),
				Toast.LENGTH_SHORT).show();*/
	}

	/**
	 * CSVファイルを展開し，各配列に格納する
	 * 失敗した場合，エラーを出力して終了する
	 * @return
	 */
	private boolean setArrayList(){

		// Array配列を初期化する
		list_short = new ArrayList<String>();
		list_long = new ArrayList<String>();

		// CSVファイルを開く;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(dataPath), "MS932"));
		} catch (FileNotFoundException e) {

			// ファイルが無いときの処理
			return false;
		} catch (UnsupportedEncodingException e) {

			// 文字コードに未対応の時の処理
			return false;
		}

		// 最終レコードまで読み込む
		String record = "";
		try {
			while((record = new MyParse().buildRecord(br)) != null){

				// 表示用配列に追加する文字列を生成する
				String shortLine = toShortRecord(record);

				// 表示配列に追加する
				list_short.add(shortLine);

				// 連動配列に追加する
				list_long.add(record);
			}
		} catch (IOException e1) {
			// 読込エラーが発生した時の処理
			return false;
		}

		// 読込成功を返す
		return true;
	}

	/**
	 * 表示用配列に追加する文字列を生成する
	 * @param longRecord : 1つのレコード全体
	 * @return           : 表示用配列に入れる文字列
	 */
	abstract public String toShortRecord(String longRecord);

	//////////////////////////////////
	// ここからリストビュー表示関連 //
	//////////////////////////////////

	/**
	 * ListViewを表示する
	 */
	private void setListView(){

		// ListViewのスクロールバーにつまみをつける
		getListView().setFastScrollEnabled(true);

		// リストビューに短縮配列の内容を貼り付ける
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_short.toArray(new String[0])));
	}

	// アイテム選択ダイアログのタイトルを作成する
	abstract public String getItemViewTitle(int position);

	// アイテム選択ダイアログのViewを作成する
	abstract public View getItemViewBody(int position);

	//////////////////////////////
	// ここからレコード表示関連 //
	//////////////////////////////

	/**
	 * アイテムが選択された時の処理
	 * @param position : 選択されたアイテムの番号
	 * @return         : 成功したらtrue
	 */
	private boolean onItemClicked(final int position){

		// list_long.get(position)で選択したレコードが取得できる

		// 対象となるレコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(list_long.get(position));

		// ダイアログを作成する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// レコードのサイズによって分岐
		if(list_record.size() < record_length){

			// レコードサイズが不適正な時

			// ダイアログのタイトルを設定する
			builder.setTitle("レコードが不正です");
		} else {

			// レコードサイズが適正な時

			// ダイアログのタイトルを作成，設定する
			builder.setTitle(getItemViewTitle(position));

			// ダイアログ内部のレイアウトを作成，設定する
			View view = getItemViewBody(position);
			if(view != null)builder.setView(view);
		}

		// 戻るボタンの処理を登録する
		builder.setNegativeButton("戻る", null);

		builder.show();

		return true;
	}
}

