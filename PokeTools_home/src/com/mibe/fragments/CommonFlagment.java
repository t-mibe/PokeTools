package com.mibe.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.mibe.pt_home.R;
import com.mibe.pt_species.SpeciesViewActivity;

/**
 * 環境タブの表示を定義するフラグメント
 * @author mibe
 *
 */
public class CommonFlagment extends android.support.v4.app.Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// リソースファイルを取得する
		View view = inflater.inflate(R.layout.tab_common, container, false);
		
		// ボタンの動作設定を行う
		setButtonAction(view);
		
		return view;
	}

	/**
	 * ボタンの動作設定を行う
	 * @param view : 設定するタブのフラグメント
	 */
	private void setButtonAction(View view) {
		
		// 種族閲覧ボタンを設定する
		setSpeciesButton(view);
		
		// タイプ閲覧ボタンを設定する
		setTypeButton(view);
		
		// 技閲覧ボタンを設定する
		setMoveButton(view);
		
		// 特性閲覧ボタンを設定する
		setAbilityButton(view);
		
		// 道具閲覧ボタンを設定する
		setItemButton(view);
	}
	
	// 種族閲覧ボタンを設定する
	private void setSpeciesButton(View view) {
		Button button = (Button)view.findViewById(R.id.button_common_species);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				//Toast.makeText(getActivity(), "種族", Toast.LENGTH_SHORT).show();
				
				// 種族閲覧モードのIntentを作成する
				Intent intent = new Intent(getActivity(), SpeciesViewActivity.class);
				startActivity(intent);
			}
		});
	}
	
	// タイプ閲覧ボタンを設定する
	private void setTypeButton(View view){
		Button button = (Button)view.findViewById(R.id.button_common_type);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Toast.makeText(getActivity(), "タイプ", Toast.LENGTH_SHORT).show();
				
				// タイプ閲覧モードのIntentを作成する
				//Intent intent = new Intent(getActivity(), TypeActivity.class);
				//startActivityForResult(intent, REQUESTCODE_TYPE);
			}
		});
	}
	
	//TODO 技閲覧ボタンを設定する
	private void setMoveButton(View view){
		Button button = (Button)view.findViewById(R.id.button_common_move);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Toast.makeText(getActivity(), "技", Toast.LENGTH_SHORT).show();
				
				// タイプ閲覧モードのIntentを作成する
				//Intent intent = new Intent(getActivity(), MoveView.class);
				//startActivityForResult(intent, REQUESTCODE_MOVE);
			}
		});
	}
	
	//TODO 特性閲覧ボタンを設定する
	private void setAbilityButton(View view){
		Button button = (Button)view.findViewById(R.id.button_common_ability);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Toast.makeText(getActivity(), "特性", Toast.LENGTH_SHORT).show();
				
				// タイプ閲覧モードのIntentを作成する
				//Intent intent = new Intent(getActivity(), MoveView.class);
				//startActivityForResult(intent, REQUESTCODE_MOVE);
			}
		});
	}
	
	//TODO 道具閲覧ボタンを設定する
	private void setItemButton(View view){
		Button button = (Button)view.findViewById(R.id.button_common_item);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Toast.makeText(getActivity(), "道具", Toast.LENGTH_SHORT).show();
				
				// タイプ閲覧モードのIntentを作成する
				//Intent intent = new Intent(getActivity(), MoveView.class);
				//startActivityForResult(intent, REQUESTCODE_MOVE);
			}
		});
	}

}
