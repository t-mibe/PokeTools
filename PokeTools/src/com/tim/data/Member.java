package com.tim.data;

import java.util.ArrayList;

import android.content.Context;
import android.util.FloatMath;

import com.tim.csv.CSVReader;
import com.tim.poketools.R;

public class Member extends CSVReader {

	//////////////////////
	// ここから定数宣言 //
	//////////////////////


	// 登録名，種族名（略称可），メモ，性別，性格，特性，個体値HABCDS，努力値HABCDS，技1234（略称可）

	public static final int RECORD_SIZE = 22;
	public static final int RECORD_ID_NAME =	0;	// 登録名のID
	public static final int RECORD_ID_RACE =	1;	// 種族名のID
	public static final int RECORD_ID_MEMO =	2;	// メモのID
	public static final int RECORD_ID_GENDER =	3;	// 性別のID
	public static final int RECORD_ID_NATURE =	4;	// 性格のID
	public static final int RECORD_ID_ABILITY =	5;	// 特性のID
	public static final int RECORD_ID_VALUE_I =	6;	// 個体値の開始ID
	public static final int RECORD_ID_VALUE_E =	12;	// 努力値の開始ID
	public static final int RECORD_ID_MOVE =	18;	// 技の開始ID


	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	public Member(Context context) {
		super(context, R.string.path_members);
	}

	//////////////////////////
	// ここから情報取得処理 //
	//////////////////////////

	/**
	 * 登録からレコードを取得する
	 * @param race : 探索する登録名
	 * @return     : 個体データのレコード文
	 */
	public String getRaceRecord(String name){

		// ファイル展開チェック
		if(!getState())return null;

		// 登録名からレコード番号を探索する
		int position = list_short.indexOf(name);

		// 見つからなかった場合，nullを返す
		if(position < 0) return null;

		// 該当するレコード文を返す
		return list_long.get(position);
	}

	//////////////////////
	// ここから計算処理 //
	//////////////////////

	// 指定したレコードからステータスの実値を算出する
	public int[] getStatValue(ArrayList<String> list_record, int level){

		// レコード長を確認する
		if(list_record.size() != Race.RECORD_SIZE)return null;

		// 種族名から種族値を取得する
		int rValues[] = new Race(context).getRaceValue(list_record.get(Member.RECORD_ID_RACE));

		// 個体値と努力値を取得する
		int iValues[] = new int[6];
		int eValues[] = new int[6];
		for(int i = 0; i < 6; i++){
			iValues[i] = Integer.parseInt("0".concat(list_record.get(i + Member.RECORD_ID_VALUE_I)));
			eValues[i] = Integer.parseInt("0".concat(list_record.get(i + Member.RECORD_ID_VALUE_E)));
		}

		// 性格から性格補正倍率を取得する
		float nValues[] = getNatureValue(list_record.get(Member.RECORD_ID_NATURE));

		return getStatValue(rValues, iValues, eValues, nValues, level);
	}
	
	/**
	 * 性格表記から性格補正率の配列を返す
	 * @param nature	: 性格表記
	 * @return			: 性格補正率のfloat配列
	 */
	public float[] getNatureValue(String nature) {

		float nValue[] = {1f, 1f, 1f, 1f, 1f, 1f};

		// 文字列が無効なとき終了する
		if(nature == null || nature.equals("")) return nValue;

		// 性格表記を数値を示す文字列に変換する
		String nNum = nature.replace('a', 'A').replace('b', 'B')
				.replace('c', 'C').replace('d', 'D').replace('s', 'S')
				.replace('A', '1').replace('B', '2')
				.replace('C', '3').replace('D', '4').replace('S', '5');

		// 1文字目を増加させるパラメータ名IDとして取得する
		int incID = Integer.valueOf(nNum) / 10;

		// 2文字目を減少させるパラメータ名として取得する
		int decID = Integer.valueOf(nNum) % 10;

		// 増加するパラメータの補正率を修正する
		nValue[incID] = 1.1f;

		// 減少するパラメータの補正率を修正する
		nValue[decID] = 0.9f;

		return nValue;
	}

	// 個体値と努力値と性格とレベルからステータスの実値を算出する
	public int[] getStatValue(int[] rValues, int[] iValues, int[] eValues, float[] nValues, int level){

		// 実値を計算する
		int value[] = new int[6];
		
		// HPだけ計算が特殊なので注意（加算する値とかヌケニン処理とか）
		for(int type = 0; type < 6; type++){

			// 指定された実値を計算する
			value[type] = getStat(type, rValues[type], iValues[type], eValues[type], nValues[type], level);
		}
		
		return value;
	}
	
	/**
	 * ステータスの実値を計算する
	 * レベルは全体オプションから取得する
	 * 
	 * @param type		: 計算するステータスのタイプ（HABCDS -> 012345)
	 * @param rValue	: 種族値
	 * @param iValue	: 個体値
	 * @param eValue	: 努力値
	 * @param nValue	: 性格補正の倍率
	 * @return
	 */
	private int getStat(int type, int rValue, int iValue, int eValue, float nValue, int level){

		// タイプがHPかつ種族値が1のとき，1を返す
		if(type == 0 && rValue == 1) return 1;

		// 共通部分まで計算する
		int state_temp =(int)FloatMath.floor(
				(rValue*2+iValue+(int)FloatMath.floor(eValue/4))*level/100)+5;

		// HPかそれ以外で分岐する
		if(type == 0){
			return state_temp + 5 + level;
		} else {
			return (int) FloatMath.floor(state_temp * nValue);
		}
	}
	
}
