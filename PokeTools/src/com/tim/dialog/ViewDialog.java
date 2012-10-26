package com.tim.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.tim.csv.CSVReader;
import com.tim.other.MyParse;
import com.tim.poketools.R;

/**
 * ダイアログ作成の抽象クラス
 * 
 * @author mibe
 *
 */
public abstract class ViewDialog {
	
	//////////////////
	// ここから定数 //
	//////////////////
	
	// アイコンの表示サイズ
	public static final float ICON_SIZE = 85f;

	////////////////////////////
	// ここからグローバル変数 //
	////////////////////////////

	// 呼び出しクラスのContext
	public Context context = null;

	// 種族アイコンディレクトリのパス
	public String imagePath = "";
	
	// 個体レベルの値
	public int level = 50;

	// 参照するCSVファイルのリーダー
	private CSVReader csv;

	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	public ViewDialog(Context context, CSVReader csv){

		// 子クラスのContextを保持する
		this.context = context;

		// 子クラスで生成したCSVリーダーを保持する
		this.csv = csv;

		// 種族アイコンディレクトリのパスを設定する
		imagePath = csv.getHomeDir().concat(context.getString(R.string.path_pk));
		
	}

	//////////////////////////////////////
	// ここからダイアログのタイトル生成 //
	//////////////////////////////////////

	/**
	 * 0番要素からレコードを取得し，タイトル文を作成する
	 * @param name : 探索するテキスト
	 * @return     : 生成されたタイトル文
	 */
	public String getDialogTitleByName(String text){

		// 該当するレコードを取得する
		String record = csv.getRecord(text);

		// レコードが取得できなかったときnullを返す
		if(record == null)return null;

		// 該当するレコードを取得し，タイトル文を作成する
		return getDialogTitleByRecord(record);
	}

	/**
	 * レコード文からタイトル文を作成する
	 * @param record : レコード文
	 * @return       : 生成したタイトル文
	 */
	public String getDialogTitleByRecord(String record) {

		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードサイズが不正ならエラー文を返す
		if(list_record.size() < 1)return "不正なデータです";

		// レコードの0番要素をタイトル文として返す
		return list_record.get(0);
	}

	//////////////////////////////////////
	// ここからダイアログの内部表示生成 //
	//////////////////////////////////////

	/**
	 * 0番要素からレコードを取得し，タイトル文を作成する
	 * @param name : 探索する登録名
	 * @return     : 生成したView
	 */
	public View getDialogViewByName(String text){

		// 該当するレコードを取得する
		String record = csv.getRecord(text);

		// レコードが取得できなかったときnullを返す
		if(record == null)return null;

		// 該当するレコードを取得し，Viewを作成する
		return getDialogViewByRecord(record);
	}
	
	/**
	 * レコード文からViewを作成する
	 * @param record : 表示するレコード文
	 * @return       : 生成したView
	 */
	abstract public View getDialogViewByRecord(String record);
}
