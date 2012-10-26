package com.tim.common;

import com.tim.data.MyType;
import com.tim.poketools.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TypeActivity extends Activity {
	
	// タイプ情報を取得するオブジェクト
	private MyType myType = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typeview);
		
		// タイプ設定のスピナーを設定する
		setSpinner();
	}

	// タイプ設定のスピナーを設定する
	private void setSpinner(){
		
		// タイプ情報を取得するオブジェクトを作成する
		myType = new MyType(this);

		// レイアウトのスピナーを取得する
		Spinner type1 = (Spinner)findViewById(R.id.spinner_type1);
		Spinner type2 = (Spinner)findViewById(R.id.spinner_type2);
		
		// タイプ名の配列からアダプターを作成する
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, myType.list_short);
		
		// それぞれのスピナーにアダプタを設定する
		type1.setAdapter(adapter);
		type2.setAdapter(adapter);
		
		// スピナーの内容が変更された時のリスナを作成する
		OnItemSelectedListener listener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(
					AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// テキストビューの内容を更新する
				setTextView();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		};
		
		// スピナーの内容が変更された時の動作を指定する
		type1.setOnItemSelectedListener(listener);
		type2.setOnItemSelectedListener(listener);
		
	}
	
	// テキストビューの内容を更新する
	private void setTextView(){
		
		// 指定したタイプ2つのレコードを取得する
		String type1 = ((Spinner)findViewById(R.id.spinner_type1)).getSelectedItem().toString();
		String type2 = ((Spinner)findViewById(R.id.spinner_type2)).getSelectedItem().toString();
		
		// タイプ耐性の配列を取得する
		float param[] = myType.getTypeState(type1, type2);
		
		// タイプ耐性が取得できなかったら終了
		if(param == null)return;
		
		// タイプ耐性を表示するテキストを作成する
		String text = getTypeText(param);
		
		// テキストビューを取得して貼り付ける
		TextView textView = (TextView)findViewById(R.id.text_types);
		textView.setText(text);
	}
	
	// タイプ耐性を表示するテキストを作成する
	private String getTypeText(float param[]){
		
		// 結果の文字列
		String text = "";
		
		// 指定した倍率の耐性がある場合，文字列に追記する
		text = text.concat(getTypeTextNum(param, 4f));
		text = text.concat(getTypeTextNum(param, 2f));
		text = text.concat(getTypeTextNum(param, 1f));
		text = text.concat(getTypeTextNum(param, 0.5f));
		text = text.concat(getTypeTextNum(param, 0.25f));
		text = text.concat(getTypeTextNum(param, 0f));
		
		return text;
	}
	
	// 指定した倍率の耐性がある場合，文字列に追記する
	private String getTypeTextNum(float param[], float num){
		
		// 結果の文字列
		String text = "";

		// 配列の長さを取得する
		int len = param.length;
		
		// 該当するならtrue
		boolean state = false;
		
		for(int i = 1; i < len; i++){
			
			if(param[i] == num){
				if(!state){
					text = (Float.toString(num)).concat(" 倍\n");
					state = true;
				} else {
					text = text.concat("，");
				}
				
				text = text.concat(myType.list_short.get(i));
			}
		}
		
		if(state)text = text.concat("\n");
		
		return text;
	}
}


