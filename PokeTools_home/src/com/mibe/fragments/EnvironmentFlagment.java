package com.mibe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mibe.pt_home.R;

/**
 * 環境タブの表示を定義するフラグメント
 * @author mibe
 *
 */
public class EnvironmentFlagment extends android.support.v4.app.Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// リソースファイルを取得する
		View view = inflater.inflate(R.layout.tab_envilonment, container, false);
		
		return view;
	}
}
