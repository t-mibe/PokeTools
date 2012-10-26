package com.tim.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.tim.other.MyParse;
import com.tim.poketools.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public abstract class CSVReader {
	
	////////////////////////////
	// ここからグローバル変数 //
	////////////////////////////

	// 呼び出し元のContext
	public Context context = null;

	// ホームディレクトリのフルパス
	private String homeDir = null;
	
	// 読み込むファイルのフルパス
	private String filePath = null;
	
	// レコードの一部を格納する配列（0番要素や略称）
	public ArrayList<String> list_short;
	
	// レコードの全体を格納する配列（もしくは正式名称）
	public ArrayList<String> list_long;
	
	
	// ファイル展開ステータス
	private boolean state = false;
	
	////////////////////////
	// ここからクラス宣言 //
	////////////////////////

	public CSVReader(Context context, int localPath_ID){

		// 呼び出し元のContextを保存
		this.context = context;

		// ローカルパスの文字列を取得する
		String localPath = context.getString(localPath_ID);
		
		// オブジェクト作成時に独自に行う処理
		init(localPath);
	}

	public CSVReader(Context context,String localPath){

		// 呼び出し元のContextを保存
		this.context = context;

		// オブジェクト作成時に独自に行う処理
		init(localPath);
	}
	
	////////////////////////////
	// ここから初期化メソッド //
	////////////////////////////

	// オブジェクト作成時に独自に行う処理
	private void init(String localPath){

		// パスが不正なら終了
		if(localPath == null || localPath.equals(""))return;

		// 設定情報を取得し変数に格納する
		setOption(localPath);

		// CSVファイルの展開を試みる
		state = trySetArrayList();
	}

	// 設定情報を取得し変数に格納する
	private void setOption(String localPath){

		// 変数チェック
		if(context == null || localPath == null || localPath.equals(""))return;

		// 設定オブジェクトを取得
		SharedPreferences sp = context.getSharedPreferences(
				context.getString(R.string.app_name), Context.MODE_PRIVATE);

		// ホームディレクトリを取得
		homeDir = sp.getString(context.getString(R.string.key_homedir), "");

		// 展開するCSVファイルのパスを設定する
		filePath = homeDir.concat(localPath);
	}

	//////////////////
	// ファイル展開 //
	//////////////////

	/**
	 * CSVファイルの展開を試みる
	 * 失敗した場合，エラーする
	 * 
	 */
	public boolean trySetArrayList(){

		// CSVファイルを読み込む
		if(!setArrayList()){

			// CSVファイルの読み込みに失敗したとき

			// トースト出力
			Toast.makeText(context,
					"CSVファイル\n\""
					.concat(filePath)
					.concat("\"\nを開けませんでした"),
					Toast.LENGTH_LONG).show();

			return false;
		}

		return true;
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
	 * 基本的には最初の要素のみを入れる
	 * @param longRecord : 1つのレコード全体
	 * @return           : 表示用配列に入れる文字列
	 */
	public String toShortRecord(String longRecord){
		
		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(longRecord);
		
		// レコード長が不正な時，ダミーデータを返す
		if(list_record.size() < 1)return "ダミー";
		
		// レコード内の第1要素を返す
		return list_record.get(0);
	}


	//////////////
	// 情報取得 //
	//////////////
	
	public boolean getState(){
		return state;
	}
	
	public String getHomeDir(){
		return homeDir;
	}

	public String getFilePath(){
		return filePath;
	}

	public boolean isFile(){
		return new File(filePath).isFile();
	}
	
	// 
	public String getRecord(String text){

		// ファイル展開チェック
		if(!getState())return null;

		// 登録名からレコード番号を探索する
		int position = list_short.indexOf(text);

		// 見つからなかった場合，nullを返す
		if(position < 0) return null;

		// 該当するレコード文を返す
		return list_long.get(position);
	}
}
