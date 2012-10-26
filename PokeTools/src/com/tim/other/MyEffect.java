package com.tim.other;

import java.util.ArrayList;

import com.tim.csv.CSVReader;
import com.tim.poketools.R;

import android.content.Context;

public class MyEffect extends CSVReader {
	
	//////////////////////
	// ここから定数宣言 //
	//////////////////////
	
	public static final int RECORD_LENGTH =		3; // レコードの要素数
	public static final int RECORD_ID_NUM =		0; // 登録番号のID
	public static final int RECORD_ID_TEXT_A =	1; // 常時効果のID
	public static final int RECORD_ID_TEXT_B =	2; // 追加効果のID
	
	

	
	public MyEffect(Context context) {
		super(context, context.getString(R.string.path_effect));
	}
	
	////////////////////////
	// ここから技効果探索 //
	////////////////////////
	
	/**
	 * 技コードから効果を取得する
	 * @param num : 技コード番号（0詰めしない10進数の文字列）
	 * @return    : 技効果のString配列（常時，確率）
	 */
	public String[] getMoveEffectAB(String num){
		
		// ファイルのチェック
		if(!getState())return null;
		
		// 技効果の文字列
		String text[] = new String[2];
		
		text[0] = getMoveEffect(num, RECORD_ID_TEXT_A);
		text[1] = getMoveEffect(num, RECORD_ID_TEXT_B);
		
		return text;
	}
	
	/**
	 * 技コードと番号から効果テキストを取得する
	 * @param num : 技コード番号（0詰めしない10進数の文字列）
	 * @param id  : 1->常時効果, 2->追加効果
	 * @return    : 技効果のテキスト
	 */
	public String getMoveEffect(String num, int id){
		
		// ファイルのチェック
		if(!getState())return null;
		
		// 対象のレコードを取得する
		ArrayList<String> list_record = MyParse.splitRecord(getRecord(num));
		
		// 引数によって分岐
		switch(id){
		default:
			return null;
		case RECORD_ID_TEXT_A: // 常時効果のテキストを取得する
			return list_record.get(RECORD_ID_TEXT_A);
		case RECORD_ID_TEXT_B: // 追加効果のテキストを取得する
			return list_record.get(RECORD_ID_TEXT_B);
		}
	}

}
