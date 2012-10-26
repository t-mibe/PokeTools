package com.tim.dialog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.tim.other.MyEffect;
import com.tim.other.MyParse;
import com.tim.poketools.R;

public class MoveViewDialog {

	//////////////////////
	// ここから定数宣言 //
	//////////////////////

	// 種族名（正式名称），画像番号，種族値HABCDS，タイプ12，特性12夢，輝石，♂率，体重

	public static final int RECORD_SIZE =			11;	// レコードのサイズ
	public static final int RECORD_ID_NAME =		0;	// 技名のID
	public static final int RECORD_ID_POWER =		1;	// 威力のID
	public static final int RECORD_ID_HIT =			2;	// 命中率のID
	public static final int RECORD_ID_PP =			3;	// 基礎PPのID
	public static final int RECORD_ID_TYPE =		4;	// タイプのID
	public static final int RECORD_ID_CLASS =		5;	// 分類のID
	public static final int RECORD_ID_TOUCH =		6;	// 接触判定のID
	public static final int RECORD_ID_RANGE =		7;	// 対象のID
	public static final int RECORD_ID_RANK =		8;	// 優先度のID
	public static final int RECORD_ID_EFFECT =		9;	// わざ効果コードのID
	public static final int RECORD_ID_ADDRATE =		10;	// 追加効果の発動率のID


	////////////////////////////
	// ここからグローバル変数 //
	////////////////////////////

	// 呼び出しクラスのContext
	private Context context;

	// データ用CSVファイルのパス
	private String filePath = "";

	// ファイル展開フラグ（trueなら展開済み）
	private boolean state = false;

	// レコードを格納した配列
	private ArrayList<String> list_race;

	// 検索用に登録名だけを格納した配列
	private ArrayList<String> list_names;

	// 技効果取得用オブジェクト
	private MyEffect myEffect = null;

	////////////////////////
	// ここからクラス定義 //
	////////////////////////

	/**
	 *  Contextを引数とする時のクラス定義
	 * @param context   : 呼び出しクラスのContext（thisでいい）
	 */
	public MoveViewDialog(Context context) {

		// 呼び出しクラスのContextを保存する
		this.context = context;

		// 各種ファイルを展開する
		openFiles();
	}

	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	// 各種ファイルを展開する
	private void openFiles(){

		// CSVファイルを展開する
		openCSV();

		// 技効果取得用オブジェクトを作成する
		myEffect = new MyEffect(context);
		myEffect.getHomeDir();
	}

	/////////////////////////
	// ここからCSV展開処理 //
	/////////////////////////

	// CSVファイルを展開する
	private void openCSV(){

		// 設定オブジェクトを作成する
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

		// ホームディレクトリを取得する
		String homeDir = sp.getString(context.getString(R.string.key_homedir), "");

		// CSVのファイルパスを設定する
		filePath = homeDir.concat(context.getString(R.string.path_type));

		// CSVファイルを開く
		trySetArrayList();
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
			Toast.makeText(context,
					"CSVファイル\n\""
					.concat(filePath)
					.concat("\"\nを開けませんでした"),
					Toast.LENGTH_LONG).show();

			return;
		}

		// 状態をファイル読み込み済みに変更する
		state = true;
	}

	/**
	 * CSVファイルを展開し，各配列に格納する
	 * 失敗した場合，エラーを出力して終了する
	 * @return
	 */
	private boolean setArrayList(){

		// Array配列を初期化する
		list_race = new ArrayList<String>();
		list_names = new ArrayList<String>();

		// CSVファイルを開く;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "MS932"));
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

				// レコード全体を配列に登録する
				list_race.add(record);

				// レコードの0番要素（＝登録名）を配列に登録する
				list_names.add(MyParse.splitRecord(record).get(0));
			}
		} catch (IOException e1) {
			// 読込エラーが発生した時の処理
			return false;
		}

		// 読込成功を返す
		return true;
	}
	
	//////////////////////
	// ここから汎用処理 //
	//////////////////////
	
	/**
	 * 表示名からレコードを取得する
	 * @param race : 探索する種族名
	 */
	public String getRecord(String name){

		// ファイル展開チェック
		if(!state)return null;
		
		// 登録名からレコード番号を探索する
		int position = list_names.indexOf(name);
		
		// 見つからなかった場合，nullを返す
		if(position < 0) return null;
		
		// 該当するレコード文を返す
		return list_race.get(position);
	}

}

