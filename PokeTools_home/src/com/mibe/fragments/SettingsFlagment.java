package com.mibe.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mibe.pt_home.R;

/**
 * 設定タブの定義
 * 表示する度に再読み込みでいい
 * @author mibe
 *
 */
public class SettingsFlagment extends android.support.v4.app.Fragment{

	// 親のFlagmentActivity
	private FragmentActivity fa;

	// アプリが持つ設定オブジェクト
	private SharedPreferences sp;

	// 親ActivityのContext
	//private Context context;

	public SettingsFlagment(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// 親のFlagmentActivityを取得する
		fa = getActivity();
		
		// 設定オブジェクトを取得する
		sp = fa.getPreferences(FragmentActivity.MODE_WORLD_READABLE);

		// リソースファイルからレイアウトを取得する
		View view = inflater.inflate(R.layout.tab_settings, container, false);

		// 各ボタンを設定する
		setButton(view);

		// 2回目以降の読み込みの場合，処理を中断する
		if(savedInstanceState !=null)return view;


		// 対応した設定オブジェクトを開く
		//sp = fa.getSharedPreferences(getActivity().getString(R.string.app_name),FragmentActivity.MODE_PRIVATE);


		// 

		// 設定データを取得する
		//FragmentActivity fa = getActivity();
		//SharedPreferences sp = fa.getSharedPreferences(getActivity().getString(R.string.app_name),FragmentActivity.MODE_PRIVATE);

		return view;
	}

	// 各ボタンの設定
	private void setButton(final View view){

		// 再読み込みボタンを設定する
		setReloadSettingsButton(view);

		// 保存ボタンを設定する
		setSaveSettingsButton(view);

		// レベル指定ボタンを設定する
		setLevelSettingButton(view, 1);
		setLevelSettingButton(view, 50);
		setLevelSettingButton(view, 100);
	}

	// 再読み込みボタンを設定する
	private void setReloadSettingsButton(final View view){

		// 再読み込みボタンを取得し設定する
		Button button = (Button)view.findViewById(R.id.button_reload);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reloadSetting(view);
			}
		});
	}

	// 再読み込み処理
	private void reloadSetting(View view){

		// ホームディレクトリのパスを取得する
		String homeDir = sp.getString(getString(R.string.key_homedir), getString(R.string.default_homeDir));
		
		// レベルの値を取得する
		int level = sp.getInt(getString(R.string.key_level), Integer.parseInt(getString(R.string.text_settings_level_50)));
		
		// ホームディレクトリのEditTextをリセットする
		((EditText)view.findViewById(R.id.edittext_homedir)).setText(homeDir);
		
		// レベルをリセットする
		((EditText)view.findViewById(R.id.edittext_level)).setText(String.valueOf(level));
	}
	

	// 保存ボタンを設定する
	private void setSaveSettingsButton(final View view){

		// 再読み込みボタンを取得し設定する
		Button button = (Button)view.findViewById(R.id.button_save);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveSetting(view); // 保存処理
			}
		});
	}

	// 保存処理
	private void saveSetting(View view){

		// ホームディレクトリのパスを取得する
		String homeDir = ((EditText)view.findViewById(R.id.edittext_homedir)).getText().toString();

		// レベルの値を取得する
		int level = Integer.parseInt(((EditText)view.findViewById(R.id.edittext_level)).getText().toString());

		//TODO 外部アクセスできる形で保存する
		
		// 設定オブジェクトのエディターを取得する
		Editor editor = sp.edit();
		
		// ホームディレクトリのパスを保存する
		editor.putString(getString(R.string.key_homedir), homeDir);
		
		// レベルを保存する
		editor.putInt(getString(R.string.key_level), level);
		
		// 変更した設定を保存する
		editor.commit();
		
		// トースト出力
		Toast.makeText(fa, "設定を保存しました", Toast.LENGTH_SHORT).show();
	}

	private void setLevelSettingButton(final View view, final int level){

		// 指定されたレベルによって分岐する
		int id = 0;

		switch(level){
		default:
			id = 0;
			break;
		case 1:
			id = R.id.Button_Level_1;
			break;
		case 50:
			id = R.id.Button_level_50;
			break;
		case 100:
			id = R.id.Button_level_100;
			break;
		}

		// リソースが取得できなかった時のエラー処理
		if(id == 0){
			Toast.makeText(fa, "Error: level = ".concat(String.valueOf(level)), Toast.LENGTH_SHORT).show();
			return;
		}

		// ボタンを取得し設定する
		Button button = (Button)view.findViewById(id);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// レベル指定のテキストボックスを取得する
				EditText editText = (EditText)view.findViewById(R.id.edittext_level);

				// テキストボックスに指定されたレベルの値を設定する
				editText.setText(String.valueOf(level));
			}
		});
	}
}
