package com.tim.user;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tim.csv.CSVEditActivity;
import com.tim.other.MyParse;
import com.tim.poketools.R;

public class AliasActivity extends CSVEditActivity {

	private static final int RECORD_SIZE = 2;
	private static final int RECORD_ID_SHORT = 0;
	private static final int RECORD_ID_LONG = 1;

	////////////////////////////////////
	// ここからオーバーライドメソッド //
	////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// レイアウトを表示
		setContentView(R.layout.activity_draglistview);

		// 設定やファイルを読み込む
		init();
	}

	////////////////////////////////////////
	// ここから自作オーバーライドメソッド //
	////////////////////////////////////////

	/**
	 * 表示用配列に追加する文字列を生成する
	 * @param longRecord : 1つのレコード全体
	 * @return           : 表示用配列に入れる文字列
	 */
	@Override
	public String toShortRecord(String longRecord){

		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(longRecord);

		// 短縮レコードのサイズを取得する
		int record_size = list_record.size();
		
		// サイズによって分岐させる
		switch (record_size){
		default:
			return list_record.get(0).concat("\n").concat(list_record.get(1));
		case 0:
			return "";
		case 1:
			return list_record.get(0);
		}
	}
	
	/**
	 * アイテム選択ダイアログのタイトルを作成する
	 * レコードサイズのチェックはすでに行っている
	 */
	@Override
	public String getItemViewTitle(int position, String item){
		
		// レコードを取得し分割する
		ArrayList<String> list_record = MyParse.splitRecord(list_long.get(position));
		
		// レコードの0番要素を取得する
		String title = (String) list_record.get(RECORD_ID_SHORT);
		
		// 選択アイテムの文字列が空白ならエラーを返す
		if(title.equals(""))return "レコードが不正です";
		
		// 選択アイテムの文字列を返す
		return title;
	}
	
	/**
	 * アイテム選択時のビューを作成する
	 * レコードサイズのチェックはすでに行っている
	 */
	@Override
	public View getItemViewBody(int position, String item){
		
		// レコードを分割して，正式名称を取得する
		String text_long = MyParse.splitRecord(list_long.get(position)).get(RECORD_ID_LONG);

		// 文字列が空白ならnullを返す
		if(text_long.equals(""))return null;
		
		TextView textView = new TextView(this);
		
		textView.setText(text_long);
		
		return textView;
	}
	
	/**
	 * アイテム編集時のビューを作成する
	 */
	@Override
	public View getItemEditBody(String record_long){
		//TODO エイリアス編集用のビューXMLを作成する
		
		// とりあえず対象のレコードを表示しておく
		EditText editText = new EditText(this);
		editText.setId(1);
		editText.setText(record_long);
		
		return editText;
	}
	
	/**
	 * レコード上書きの処理
	 */
	@Override
	public void saveRecord(int position, View view){
		//TODO エイリアス上書きの処理
		
		// とりあえずテキスト欄の中身を上書き
		String newRecord_long = ((EditText)view.findViewById(1)).getText().toString();
		Toast.makeText(this, "レコード更新\n\"".concat(newRecord_long).concat("\""),
				Toast.LENGTH_SHORT).show();
		
		// 各配列を上書きする
		list_long.set(position, newRecord_long);
		list_short.set(position, toShortRecord(newRecord_long));
	}
	
	//////////////////////////
	// ここから自作メソッド //
	//////////////////////////
	
	/**
	 * 設定やファイルを読み込む
	 * 再読み込み時もこれを実行すればよい
	 */
	private void init(){
		
		// タイトルIDを設定する
		titleID = R.string.title_activity_alias;
		
		// 扱うCSVファイルのレコード長を設定する
		record_length = RECORD_SIZE;
		
		// 編集するファイルパスを設定する
		setDataPath(R.string.path_alias);

		// CSVファイルを開く（失敗したら終了）
		openCSV();
	}
}
