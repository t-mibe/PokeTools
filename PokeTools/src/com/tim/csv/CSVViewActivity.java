package com.tim.csv;

import java.util.ArrayList;

import com.tim.other.MyParse;
import com.tim.other.Orientation;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public abstract class CSVViewActivity extends ListActivity{

	//////////////////////////////////////////
	// ここからグローバル変数orオブジェクト //
	//////////////////////////////////////////

	// CSVリーダー
	public CSVReader csv = null;
	
	// CSVファイルのレコード文の配列
	public ArrayList<String> list_long;
	
	// ListViewに表示するレコード省略文の配列
	public ArrayList<String> list_short;

	// 1レコード内の要素数
	public int record_length = 0;

	////////////////////////////////////
	// ここからオーバーライドメソッド //
	////////////////////////////////////

	// アクティビティ作成時
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// 回転を禁止する
		Orientation.lockOrientation(this);
	}

	// アイテムが選択された時
	@Override
	public void onListItemClick(ListView listView, View v, int position, long id){
		super.onListItemClick(listView, listView, position, id);

		// 種族データを表示するダイアログを作成，表示する
		onItemClicked(position);
	}

	//////////////////////////
	// ここから自作メソッド //
	//////////////////////////

	/**
	 * アクティビティの初期化を行う
	 * @param localPath_ID : 展開するCSVファイルのローカルパス
	 */
	public void init(int localPath_ID) {

		// CSVリーダーを作成する
		csv = new CSVReader(this, localPath_ID){
			@Override
			public String toShortRecord(String longRecord) {
				// レコードの省略文を作成する
				return ((CSVViewActivity)context).toShortRecord(longRecord);
			}
		};
		
		// ArrayListを取得する
		list_long = csv.list_long;
		list_short = csv.list_short;

		// ListViewを表示する
		setListView();
	}

	// レコードの省略文を作成する
	public String toShortRecord(String longRecord){
		
		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(longRecord);
		
		if(list_record.size() < 1)return null;
		
		return list_record.get(0);
	}

	// ListViewを表示する
	private void setListView(){

		// ListViewのスクロールバーにつまみをつける
		getListView().setFastScrollEnabled(true);

		// リストビューに短縮配列の内容を貼り付ける
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, csv.list_short.toArray(new String[0])));
	}

	//////////////////////////////
	// ここから要素選択後の処理 //
	//////////////////////////////
	
	/**
	 * アイテムが選択された時の処理
	 * @param position : 選択されたアイテムの番号
	 * @return         : 成功したらtrue
	 */
	private boolean onItemClicked(final int position){

		// list_long.get(position)で選択したレコードが取得できる

		// 対象となるレコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(csv.list_long.get(position));

		// ダイアログを作成する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// レコードのサイズによって分岐
		if(list_record.size() != record_length){

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

	// アイテム選択ダイアログのタイトルを作成する
	abstract public String getItemViewTitle(int position);

	// アイテム選択ダイアログのViewを作成する
	abstract public View getItemViewBody(int position);
	
}
