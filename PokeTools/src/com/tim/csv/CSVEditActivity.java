package com.tim.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.mibe.draglistview.DragListAdapter;
import com.mibe.draglistview.DragListView;
import com.tim.other.MyParse;
import com.tim.other.Orientation;
import com.tim.poketools.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public abstract class CSVEditActivity extends Activity {
	
	// タイトル文字のリソースID
	public int titleID = 0;
	
	// レコードの長さ
	public int record_length = 0;

	// ステータス計算時のレベル
	int level = 50;

	// 編集するCSVファイルのパス
	String dataPath = "";

	// 表示用の配列
	public ArrayList<String> list_short;

	// 編集中のCSVデータを保存する配列
	public ArrayList<String> list_long;

	// 変更を確認するための配列
	private ArrayList<String> list_old;

	// リストビュー
	DragListView listView;

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

	/**
	 * アクティビティの通常終了時
	 * 確認ダイアログを経由してから終了する
	 * 経由せずに終了したい場合はsuper.finish();
	 */
	@Override
	public void finish(){
		checkUpdate(); // 個体リストに変更があれば，保存を確認する
	}

	/**
	 * オプションメニュー作成
	 * 起動時に実行される
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// XMlファイルからメニューを読み込む
		getMenuInflater().inflate(R.menu.activity_listview, menu);

		return true;
	}

	/**
	 *  オプションメニューアイテムが選択された時に呼び出される
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;

		// 選択したオプションのIDを取得する
		int option_id = item.getItemId();

		// オプションのIDによって処理を分岐させる
		switch (option_id) {
		default:
			ret = super.onOptionsItemSelected(item);
			break;
		case R.id.menu_add: // 追加が選択されたとき
			ret = addList();
			break;
		case R.id.menu_reload: // 再読み込みが選択された時
			ret = reloadList();
			break;
		case R.id.menu_save:	// 保存が選択された時
			ret = saveList();
			break;
		}
		return ret;
	}

	//////////////////////////////
	// ここから自作メソッド宣言 //
	//////////////////////////////

	/**
	 * アクティビティを確認せずに終了する
	 */
	private void appEnd(){
		super.finish();
	}

	/**
	 * リストビューを更新する
	 */
	public void refresh(){
		listView.invalidateViews();
	}
	
	/**
	 * ウィンドウタイトルを更新する
	 */
	private void reloadTitle(){

		// タイトルの文字列を取得する
		//String title_base = getString(R.string.title_activity_membersList);
		String title_base = getString(titleID);

		// リストのサイズを取得する
		int listSize = list_short.size();

		// タイトルの文字列を生成する
		String title = title_base.concat("(").concat(Integer.toString(listSize)).concat(")");

		setTitle(title);
	}

	////////////////////////
	// ここから初期化関連 //
	////////////////////////

	// アクティビティ作成時に独自に行う処理
	private void init(){

		// 画面の向きを固定する
		Orientation.lockOrientation(this);

		// レベルの値を取得する
		setLevel();
	}

	// レベルの値を取得する
	private int setLevel(){

		SharedPreferences sp = 
				getSharedPreferences(this.getString(R.string.app_name), MODE_PRIVATE);

		level = sp.getInt(this.getString(R.string.key_level), 50);

		return level;
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

		// ホームディレクトリのパスを設定する
		String homeDir = setHomeDirPath();

		// 編集するファイルのパスを取得する
		dataPath = homeDir.concat(localPath);

		// ファイルが存在するかをチェックする
		return (new File(dataPath)).isFile();
	}

	private String setHomeDirPath(){
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
		setDragListView();

		// DragListViewのアイテムがクリックされた時の処理を登録する
		setDragListViewClickListener();
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

			appEnd();
			return;
		}
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

		// 比較用に連動配列を複製する
		list_old = new ArrayList<String>(list_long);

		// タイトルの文字列を設定する
		reloadTitle();

		// 読込成功を返す
		return true;
	}

	/**
	 * 表示用配列に追加する文字列を生成する
	 * @param longRecord : 1つのレコード全体
	 * @return           : 表示用配列に入れる文字列
	 */
	abstract public String toShortRecord(String longRecord);

	//////////////////////
	// ここから表示関連 //
	//////////////////////
	
	/**
	 * DragListViewを作成，表示する
	 * 		1. Arrayの型を指定する
	 * 		2. オブジェクトIDを指定する
	 * 
	 * @return: 作成したDragListView
	 */
	private void setDragListView(){
		// ソート可能なListAdapterを作成する
		DragListAdapter<String> adapter = 
				new DragListAdapter<String>(this);

		// 表示用配列と連動配列を設定する
		adapter.list_view = list_short;
		adapter.list_data = list_long;


		// ソート可能なListViewを作成し配列を読み込ませる
		listView = (DragListView) findViewById(R.id.dragListView);

		//ListViewのスクロールバーにつまみをつける
		listView.setFastScrollEnabled(true);

		// ListViewと配列管理アダプタを接続する
		listView.setAdapter(adapter);
	}

	/**
	 * DragListViewのアイテムがクリックされた時の処理を登録する
	 * 特に変更しないはず
	 * @param listView: 既に作成したDragListView
	 */
	private void setDragListViewClickListener(){

		// リストビューのアイテムがクリックされた時の処理を登録する
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// 対象となるリスト配列を取得します
				DragListView listView = (DragListView) parent;

				// クリックしたアイテムの文字列を取得します
				String item = (String) listView.getItemAtPosition(position);

				// アイテムがクリックされた時の処理を実行します
				onItemClicked(position, item);
			}
		});
	}


	/**
	 * アイテムがクリックされた時の処理
	 * 最初に表示させるダイアログを表示して，編集or削除orキャンセルを聞く
	 * 編集が指定されたら編集用ダイアログを開く
	 * @param position	: 指定したアイテムの順番（数値処理等に使う）
	 * @param item		: 指定したアイテムの文字列（list_shortのもの）
	 */
	//abstract public boolean onItemClicked(int position, String item);
	private boolean onItemClicked(final int position, String item){
		
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
			builder.setTitle(getItemViewTitle(position, item));
			
			// ダイアログ内部のレイアウトを作成，設定する
			View view = getItemViewBody(position, item);
			if(view != null)builder.setView(view);
		}
		
		// 編集ボタンの処理を登録する
		builder.setPositiveButton("編集", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// メンバーの編集を行うダイアログを表示する
				boolean result = editRecord(position);

				// 変更があったらリストビューを更新する
				if(result) listView.invalidateViews();
			}
		});

		// 削除ボタンの処理を登録する
		builder.setNeutralButton("削除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// メンバーの削除を行うダイアログを表示する
				showDeleteMemberDialog(position);
			}
		});

		// 戻るボタンの処理を登録する
		builder.setNegativeButton("戻る", null);

		builder.show();

		return true;
	}

	// アイテム選択ダイアログのタイトルを作成する
	abstract public String getItemViewTitle(int position, String item);

	// アイテム選択ダイアログのビューを作成する
	abstract public View getItemViewBody(int position, String item);

	/////////////////////////
	// ここからCSV保存関連 //
	/////////////////////////

	/**
	 * CSVと配列を比較して，変更点があるかチェックする
	 * @return: 変更点があればTrue
	 */
	private boolean isNewData() {
		if(list_long == null || list_old == null) return false;
		return !list_old.equals(list_long);
	}

	/**
	 * 変更があれば，保続するか確認する
	 */
	private void checkUpdate(){

		// 変更があるかをチェックする
		boolean update = isNewData();

		if(update){

			// CSVを更新するか確認するダイアログを設定する
			setSaveDialog();
		} else {

			// 変更がなければそのまま終了する
			appEnd();
		}
	}

	/**
	 * CSVを更新するか確認するダイアログを設定する
	 */
	private void setSaveDialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("データに変更があります");
		builder.setMessage("選択してください");
		builder.setNegativeButton("キャンセル", null);
		builder.setNeutralButton("破棄", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// アクティビティを終了する
				appEnd();
			}
		});
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// CSVの保存を試みる
				boolean result = saveCSV();

				// 結果を出力してから終了する
				setSaveResultDialog(result);
			}
		});
		builder.show();
	}

	/**
	 * CSVの保存を試みる
	 * @return: 成功したらTrue
	 */
	private boolean saveCSV(){

		// ファイルを開く
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(dataPath);
		} catch (FileNotFoundException e) {
			return false;
		}

		// 連動配列を順番に書き込む
		int len = list_long.size();

		// 最初の行を
		for(int i = 0; i < len; i++){

			// 配列の内容を取得する
			String text = list_long.get(i);

			// 文字列の末尾に改行を加える
			text = text.concat("\r\n");

			try {
				fileOutputStream.write(text.getBytes("SJIS"));
			} catch (UnsupportedEncodingException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		
		// 比較用の配列を更新する
		list_old = new ArrayList<String>(list_long);
		
		return true;
	}

	/**
	 * 保存の成否を表示する（終了するのはここ）
	 * 保存に成功したらトースト出力して終了
	 * 保存に失敗したらダイアログ表示
	 * @param result: 保存が成功していたらTrue
	 */
	private void setSaveResultDialog(boolean result){

		// 保存に成功していた場合
		if(result){
			Toast.makeText(this, "保存に成功しました", Toast.LENGTH_LONG).show();
			appEnd();
			return;
		}

		// ダイアログを作成し表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("保存に失敗しました");
		builder.setMessage("選択してください");
		builder.setNegativeButton("戻る", null);
		builder.setPositiveButton("破棄", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appEnd(); // アクティビティを終了する
			}
		}).show();
	}
	
	//////////////////////////
	// ここからメニュー処理 //
	//////////////////////////

	/**
	 * オプションメニューの追加が選択された時の処理
	 */
	private boolean addList(){

		// 追加するレコードを作成する
		String record = createNewRecord(record_length);

		// 表示用配列に追加する文字列を生成する
		String shortLine = toShortRecord(record);

		// 配列に追加する
		list_short.add(shortLine);
		list_long.add(record);

		// 新しい最終レコード番号を取得する
		int lastPositon = list_short.size() - 1;

		// 空白のレコードを編集する
		boolean result = editRecord(lastPositon);

		// 編集がキャンセルされた時レコードを削除して終了する
		if(!result){
			list_short.remove(lastPositon);
			list_long.remove(lastPositon);

			return false;
		}

		// リストビューを再描画する
		listView.invalidateViews();

		// 一番下までスクロールする
		listView.setSelection(lastPositon);

		// タイトルを更新する
		reloadTitle();

		return true;
	}

	/**
	 * 追加する空白レコードを作成する
	 * @param len	: レコードのフィールド数
	 * @return		: 空白のレコード
	 */
	private String createNewRecord(int len) {

		// 出力する文字列
		String result = "";

		// （カンマ数 = フィールド数 - 1）だけ繰り返す
		for(int i = 1; i < len; i++){
			result = result.concat(",");
		}
		return result;
	}

	/**
	 * オプションメニューの再読み込みが選択された時の処理
	 * @return
	 */
	private boolean reloadList(){

		// 再初期化前の確認ダイアログを表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("再読み込み");
		builder.setMessage("保存していない変更点を破棄します\nよろしいですか？");
		builder.setPositiveButton("再読み込み", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 再読み込み
				openCSV();
			}
		});
		builder.setNegativeButton("キャンセル", null);
		builder.show();

		return true;
	}

	/**
	 * オプションメニューの保存が選択された時の処理
	 * @return : 保存成功したらTrue
	 */
	private boolean saveList(){
		
		if(!isNewData()){
			Toast.makeText(this, "変更箇所はありません", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		if(saveCSV()){
			Toast.makeText(this, "保存に成功しました", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			Toast.makeText(this, "保存に失敗しました", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	////////////////////////////////
	// ここからレコード操作の処理 //
	////////////////////////////////

	/**
	 * レコードの編集を行うダイアログを表示する
	 * @param index		: 編集するレコードの番号
	 * @return 			: 変更したらTrue
	 */
	private boolean editRecord(final int position){
		
		// 編集するレコードを取得する
		String record_long = list_long.get(position);
		
		// 編集用ダイアログのビュー
		final View view = getItemEditBody(record_long);
		
		// 編集用ダイアログ
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("編集モード");
		builder.setView(view);
		builder.setPositiveButton("決定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// レコードの上書き処理を行う
				saveRecord(position, view);
				
				// 表示の更新を行う
				listView.invalidateViews();
			}
		});
		builder.setNegativeButton("中止", null);
		builder.show();
		
		return true;
	}
	
	// 編集用ダイアログのビューを作成する
	abstract public View getItemEditBody(String record_long);
	
	// レコードの上書き処理
	abstract public void saveRecord(int position, View view);
	
	/**
	 * メンバーの削除を行うダイアログを表示する
	 * @param position : 削除するレコードのID
	 */
	private void showDeleteMemberDialog(final int position){
		
		// 削除を確認するダイアログを表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("確認");
		builder.setMessage("削除します");
		builder.setPositiveButton("削除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// レコードを削除する
				list_short.remove(position);
				list_long.remove(position);

				// タイトルを更新する
				reloadTitle();

				// 描画を更新する
				listView.invalidateViews();
			}
		});
		builder.setNegativeButton("キャンセル", null);
		builder.show();

		// 変更後にArrayの更新と表示の更新をする

	}
}

