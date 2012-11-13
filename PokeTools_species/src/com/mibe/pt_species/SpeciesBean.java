package com.mibe.pt_species;

import java.io.Serializable;

/**
 * 種族データの構造体
 * 
 * @author mibe
 *
 */
public class SpeciesBean implements Serializable{

	private static final long serialVersionUID = 1L;	

	private static final int length = 16;

	private String name, id;
	private int[] bStat = new int[6];
	private String[] type = new String[2];
	private String[] ability = new String[3];
	private boolean evolite = false;
	private float maleProb;
	private float weight;

	//CSVReader r;

	/**
	 * CSVレコードを各値に展開する
	 * @param record: String配列形式のCSVレコード
	 * @return 
	 */
	public SpeciesBean create(String[]	record){

		// レコード長のチェック
		if(record.length != length)return null;

		name = record[0];	// 種族名
		id = record[1];		// 画像ID

		// 種族値
		for(int i = 0; i < 6; i++)bStat[i] = Integer.valueOf(record[i + 2]);

		// タイプ
		for(int i = 0; i < 2; i++)type[i] = record[i + 8];

		// 特性
		for(int i = 0; i < 3; i++)ability[i] = record[i + 10];

		// 輝石
		if(record[13].equals("1"))evolite = true;
		else evolite = false;

		// ♂率
		maleProb = Float.valueOf(record[14]);

		// 体重
		weight = Float.valueOf(record[15]);

		return this;
	}

	public String getName(){return name;}
	public String getId(){return id;}
	public int[] getBaseStat(){return bStat;}
	public String[] getType(){return type;}
	public String[] getAbility(){return ability;}
	public boolean isEvolite(){return evolite;}
	public float getMaleProb(){return maleProb;}
	public float getWeight(){return weight;}

	// 表示用文字列を生成する
	@Override
	public String toString(){
		return name;
	}
}
