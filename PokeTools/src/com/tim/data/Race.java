package com.tim.data;

import java.util.ArrayList;

import android.content.Context;

import com.tim.csv.CSVReader;
import com.tim.other.MyParse;
import com.tim.poketools.R;

public class Race extends CSVReader {

	//////////////////////
	// ここから定数定義 //
	//////////////////////

	// 種族名（正式名称），画像番号，種族値HABCDS，タイプ12，特性12夢，輝石，♂率，体重

	public static final int RECORD_SIZE =			16;	// レコードのサイズ
	public static final int RECORD_ID_NAME =		0;	// 種族名のID
	public static final int RECORD_ID_RACEID =		1;	// 種族番号のID（文字列として扱う事）
	public static final int RECORD_ID_VALUE_R =		2;	// 種族値の開始ID
	public static final int RECORD_ID_TYPES =		8;	// タイプの開始ID
	public static final int RECORD_ID_ABILITIES =	10;	// 特性の開始ID
	public static final int RECORD_ID_EVOLUTION = 	13;	// 未進化の判定ID
	public static final int RECORD_ID_MALERATE =	14;	// ♂率のID
	public static final int RECORD_ID_WEIGHT =		15;	// 体重のID

	// 体重による倍率
	public static final float WEIGHT_MAX_RANK_1 = 10f;	// 体重ランク1の最大重量
	public static final float WEIGHT_MAX_RANK_2 = 25f;	// 体重ランク1の最大重量
	public static final float WEIGHT_MAX_RANK_3 = 50f;	// 体重ランク1の最大重量
	public static final float WEIGHT_MAX_RANK_4 = 100f;	// 体重ランク1の最大重量
	public static final float WEIGHT_MAX_RANK_5 = 200f;	// 体重ランク1の最大重量

	////////////////////////////
	// ここからグローバル変数 //
	////////////////////////////

	// エイリアス変換クラス
	private Alias alias = null;

	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	public Race(Context context) {
		super(context, R.string.path_race);

		alias = new Alias(context);
	}

	//////////////////////////
	// ここから情報取得処理 //
	//////////////////////////

	/**
	 * 画像に用いる種族ID（文字列）を取得する
	 * @param race_long : 種族の正式名称）
	 * @return          : 種族IDの文字列
	 */
	public String getRaceID(String race){

		// 該当するレコードを取得する
		String record = getRecord(race);

		// レコードが取得できなかったときnullを返す
		if(record == null)return null;

		// 該当種族のレコードを取得する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// 種族IDを返す
		return list_record.get(RECORD_ID_RACEID);
	}

	/**
	 * 指定した種族の種族値を取得する
	 * @param race : 種族名（略称可）
	 * @return     : 種族値のint配列（HABCDS）
	 */
	public int[] getRaceValue(String race){

		// 種族の正式名称を取得する
		String race_long = alias.checkAlias(race);

		// 該当するレコードを取得する
		String record = getRecord(race_long);

		// レコードが取得できなかったときnullを返す
		if(record == null)return null;

		// 該当種族のレコードを取得する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードの要素数が不正な時nullを返す
		if(list_record.size() != RECORD_SIZE)return null;

		// 種族値を取得する
		int rValue[] = new int[6];
		for(int i = 0; i < 6; i++){
			rValue[i] = Integer.valueOf(list_record.get(i + RECORD_ID_VALUE_R));
		}

		return rValue;
	}

	/**
	 * 指定した種族の体重ランクを取得する
	 * @param race : 種族名（略称可）
	 * @return     : 体重ランク（失敗時には-1）
	 */
	public int getWeightRankByRace(String race){
		
		// 指定した種族の体重を取得する
		float weight = getWeightByRace(race);
		
		// 体重の値をチェックする
		if(weight < 0f)return -1;

		// 体重から体重ランクを取得する
		return getWeightRankByWeight(weight);
	}

	/**
	 * 指定した種族の体重を取得する
	 */
	public float getWeightByRace(String race){
		
		// 種族の正式名称を取得する
		String race_long = alias.checkAlias(race);

		// 該当するレコードを取得する
		String record = getRecord(race_long);

		// レコードが取得できなかったときnullを返す
		if(record == null)return -1;

		// 該当種族のレコードを取得する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードの要素数が不正な時nullを返す
		if(list_record.size() != RECORD_SIZE)return -1;

		// 体重を取得する
		return Float.valueOf(list_record.get(RECORD_ID_WEIGHT));
	}

	/**
	 * 体重から体重ランクを取得する
	 */
	public int getWeightRankByWeight(float weight){

		// 体重によって分岐する
		if(weight <= WEIGHT_MAX_RANK_1) return 1;
		else if(weight <= WEIGHT_MAX_RANK_2) return 2;
		else if(weight <= WEIGHT_MAX_RANK_3) return 3;
		else if(weight <= WEIGHT_MAX_RANK_4) return 4;
		else if(weight <= WEIGHT_MAX_RANK_5) return 5;
		else return 6;
	}
}
