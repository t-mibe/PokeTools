package com.mibe.lib_bean;

import java.io.Serializable;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 種族データの構造体
 * 
 * @author mibe
 *
 */
public class SpeciesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name, id;
	private int bs_H,bs_A, bs_B,bs_C,bs_D,bs_S;
	private String type1, type2;
	private String ability_1, ability_2, ability_3;
	private boolean evolite;
	private float maleProb;
	private float weight;
	
	//CSVReader r;
	
	@Override
	public String toString(){
		return "hoge";
	}
}
