package com.tim.data;

import java.io.BufferedReader;
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

/**
 * タイプ関連の処理を行う
 * @author mibe
 *
 */
public class MyType {
	
	// 1レコード内の要素数
	public static final int RECORD_LENGTH = 19;

	// オプション取得ようのContext
	private Context context = null;
	
	// タイプ設定ファイルのファイルパス
	private String filePath = "";
	
	// ファイル展開が成功したらtrue
	private boolean state = false;
	
	// 各レコードを保存する配列
	public ArrayList<String> list_long;
	
	// 各レコードのタイトルを保存する配列
	public ArrayList<String> list_short;
	
	////////////////////////
	// ここから初期化処理 //
	////////////////////////
	
	// クラス宣言
	public MyType(Context context){
		
		// 呼び出しクラスのContextを保存する
		this.context = context;
		
		// ファイルパスを設定する
		setFilePath();
		
		// ファイルを展開する
		openCSV();
	}
	
	// ファイルパスを設定する
	private void setFilePath(){
		
		// Contextのチェック
		if(context == null)return;
		
		// 設定オブジェクトを取得する
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
		
		// ホームディレクトリのパスを取得する
		String homeDir = sp.getString(context.getString(R.string.key_homedir), "");
		
		// タイプ設定ファイルのパスを取得する
		filePath = homeDir.concat(context.getString(R.string.path_type));
	}
	
	// ファイルを展開する
	private void openCSV(){
		
		// ファイルパスのチェック
		if(filePath == null || filePath.equals("")) return ;
		
		// CSVファイル展開を試みる
		trySetArrayList();
	}
	
	// CSVファイル展開を試みる
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
		
		// ファイル状態を変更する
		state = true;
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

				// 1レコードを要素に分割する
				ArrayList<String> recordList = MyParse.splitRecord(record);

				// 略称用の配列に略称を追加する
				list_short.add(recordList.get(0));
				
				// レコード全体の配列に追加する
				list_long.add(record);
			}
		} catch (IOException e1) {
			// 読込エラーが発生した時の処理
			return false;
		}

		// 読込成功を返す
		return true;
	}
	
	//////////////////////////
	// ここから耐性計算関連 //
	//////////////////////////
	
	/**
	 * 指定したタイプの耐性をint配列にして取得する（2タイプ）
	 * @param type1 : タイプ1の正式名称
	 * @param type2 : タイプ2の正式名称
	 * @return : タイプ耐性を示した倍率の配列
	 */
	public float[] getTypeState(String type1, String type2){
		
		// ファイルチェック
		if(!state)return null;
		
		// タイプ1の耐性を取得する
		float[] param1 = getTypeState(type1);
		float[] param2 = getTypeState(type2);
		
		// 配列の長さを取得する
		int len = param1.length;
		
		// 耐性を掛け合わせる
		float[] result = new float[len];
		for(int i = 0; i < len; i++){
			result[i] = param1[i] * param2[i];
		}
		
		return result;
	}
	
	/**
	 * 指定したタイプの耐性をint配列にして取得する（1タイプ）
	 * @param type : タイプ1の正式名称
	 * @return : タイプ耐性を示した倍率の配列
	 */
	public float[] getTypeState(String type){

		// ファイルチェック
		if(!state)return null;
		
		// 指定したタイプのレコード文を分割して取得する
		ArrayList<String> list_record = MyParse.splitRecord(getTypeRecord(type));
		
		// レコードの長さを取得する
		int len = list_record.size();
		
		// 耐性の倍率を抽出する
		float[] result = new float[len-1];
		for(int i = 1; i < len; i++){
			result[i-1] = Float.valueOf(list_record.get(i));
		}
		
		return result;
	}
	
	/**
	 * 指定した1タイプのレコード文を取得する
	 * @param textType : タイプの正式名称
	 * @return : タイプの耐性を記述したレコード文
	 */
	public String getTypeRecord(String type){

		// ファイルチェック
		if(!state)return null;
		
		// 名前の配列からレコード番号を取得する
		int position = list_short.indexOf(type);
		
		// 対応したレコードを返す
		if(position < 0)return null;
		return list_long.get(position);
	}
	
	/**
	 * 指定したタイプの通し番号を取得する
	 */
	public int getTypeNum(String type){

		// ファイルチェック
		if(!state)return -1;
		
		return list_short.indexOf(type);
	}
}
