package com.tim.user;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.tim.csv.CSVEditActivity;
import com.tim.data.Member;
import com.tim.dialog.MemberViewDialog;
import com.tim.other.Orientation;
import com.tim.other.MyParse;
import com.tim.poketools.R;

public class MemberActivity extends CSVEditActivity {
	
	//////////////////////
	// ここから定数宣言 //
	//////////////////////
	

	////////////////////////////////
	// ここからグローバル変数宣言 //
	////////////////////////////////
	
	// 個体データ表示ダイアログ作成オブジェクト
	private MemberViewDialog mDialog;
	
	////////////////////////////////////////
	// ここからオーバーライドメソッド宣言 //
	////////////////////////////////////////

	/**
	 * アクティビティ作成時
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// レイアウトを表示
		setContentView(R.layout.activity_draglistview);

		// 描画以外の初期化処理を行う
		init();
	}

	////////////////////////////////////////
	// ここから自作オーバーライドメソッド //
	////////////////////////////////////////

	/**
	 * 連動配列用データを表示配列用データに変換する
	 */
	@Override
	public String toShortRecord(String record_long) {

		final String space = "　";
		int len = 0;

		// 連結した文字列
		String record_short;

		// 1行をデータの要素に分割
		ArrayList<String> rList_long = MyParse.splitRecord(record_long);
		
		// レコード長が不正ならfalse
		if(rList_long.size() < Member.RECORD_SIZE) return "";

		// 登録名と種族名を取得する
		String memberName = rList_long.get(Member.RECORD_ID_NAME);
		String memberRace = rList_long.get(Member.RECORD_ID_RACE);

		// 隙間の全角スペース
		len = 5 - memberName.length();

		// 間隔を揃える
		for(int i = 0; i < len; i++){
			memberName = memberName.concat(space);
		}

		record_short = memberName.concat("：").concat(memberRace);
		
		// 画面が横向きの時，メモも追加する
		if(!Orientation.isPortrait(this)){
			
			// 隙間の全角スペース
			len = 5 - memberRace.length();

			// 間隔を揃える
			for(int i = 0; i < len; i++){
				record_short = record_short.concat(space);
			}
			
			record_short = record_short.concat("：").concat(rList_long.get(Member.RECORD_ID_MEMO));
		}

		return record_short;
	}
	

	// アイテム選択ダイアログのタイトルを作成する
	@Override
	public String getItemViewTitle(int position, String item) {
		
		// 個体データ表示ダイアログのタイトルを生成する
		return mDialog.getDialogTitleByRecord(list_long.get(position));
	}

	// アイテム選択ダイアログのビューを作成する
	@Override
	public View getItemViewBody(int position, String item) {
		
		// 個体データ表示ダイアログのViewを生成する
		return mDialog.getDialogViewByRecord(list_long.get(position));
	}
	
	@Override
	public View getItemEditBody(String record_long) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void saveRecord(int position, View view) {
		// TODO 自動生成されたメソッド・スタブ

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
		titleID = R.string.title_activity_members;

		// 個体データ表示用ダイアログ生成オブジェクトを作成
		mDialog = new MemberViewDialog(this);

		// 扱うCSVファイルのレコード長を設定する
		record_length = Member.RECORD_SIZE;

		// 編集するファイルパスを設定する
		setDataPath(R.string.path_members);
		
		// 各種オプションを設定する
		setOption();

		// CSVファイルを開く（失敗したら終了）
		openCSV();
		
	}
	
	// その他オプションを設定する
	private void setOption(){
		
		// 設定オブジェクトを取得する
		SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		
		// ホームディレクトリを取得する
		String homeDir = sp.getString(getString(R.string.key_homedir), "");
		
		homeDir.concat(getString(R.string.path_pk));
	}
}

