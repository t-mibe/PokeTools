package com.tim.data;

import java.util.ArrayList;

import com.tim.csv.CSVReader;
import com.tim.other.MyParse;
import com.tim.poketools.R;

import android.content.Context;

public class Alias extends CSVReader {

	//////////////////////
	// ここから定数定義 //
	//////////////////////
	
	public static final int RECORD_SIZE =		2;	// レコードのサイズ
	public static final int RECORD_ID_SHORT =	0;	// 略称のID
	public static final int RECORD_ID_LONG =	1;	// 正式名称のID
	
	// エイリアス変換を行う最大回数
	private static final int MAX_CHANGE = 3;
	
	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	public Alias(Context context) {
		super(context, R.string.path_alias);
	}

	/**
	 * 文字列に対してエイリアスのチェックを行う
	 * こちらは使いまわせる省略バージョン
	 * MyAlias(this)とする必要がある
	 */
	public String checkAlias(String text){
		
		// ファイル読み込みに失敗している時，そのまま出力する
		if(!getState()) return text;
		
		// 文字列に対してエイリアスのチェックを行う
		return checkAlias(list_short, list_long, text);
	}

	/**
	 * 文字列に対してエイリアスのチェックを行う
	 * @param alias_short	: エイリアス情報を入れてある配列
	 * @param alias_long	: エイリアス情報を入れてある配列
	 * @param src			: チェックする文字列
	 * @return				: チェックする文字列 or 変換した文字列
	 */
	public String checkAlias(ArrayList<String> alias_short, ArrayList<String> alias_long, String text){
		
		// 配列番号を格納する値
		int num;
		
		for(int i = 0; i < MAX_CHANGE; i++){
			
			// 略称にないか探索する，無ければ変換終了
			if((num = alias_short.indexOf(text)) < 0) return text;
			
			// レコード番号からレコード文を取得し分割する
			ArrayList<String> list_record = MyParse.splitRecord(list_long.get(num));
			
			// レコードから正式名称を抽出して文字列を更新させる
			text = list_record.get(RECORD_ID_LONG);
		}
		
		return text;
	}
}

