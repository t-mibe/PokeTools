package com.tim.poketools;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tim.common.RaceActivity;
import com.tim.common.TypeActivity;
import com.tim.user.AliasActivity;
import com.tim.user.MemberActivity;


/**
 * ポケモン対戦サポートツール
1. 対戦
	チーム選択
	ルール，形式選択
	対戦の新規作成ボタン
2. 個人データ（全て編集）
	個体
	チーム
	試合ログ
	人物
	エイリアス
3. 環境データ（全て編集）
	調整パターン
	種族別調整パターン
	種族別技パターン
	種族別道具パターン
	種族別デフォルト特性
	夢特性解禁ステータス
3. 一般データ（夢特性のみ編集）
	種族
	タイプ
	技
	特性
	道具
4. 設定
	ホームディレクトリ
	ディレクトリ作成
	ログ削除
	人物削除
	ホームディレクトリ削除
	※ 以下，更新版
	使用するGoogleアカウント名
	ホームディレクトリのパス
 * @author mibe
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private static final String TAG = "MainActivity";
	
	// Intent呼び出し時のリクエストコード
	private static final int REQUESTCODE_MEMBERS = 1;
	private static final int REQUESTCODE_ALIAS = 2;
	private static final int REQUESTCODE_RACE = 3;
	private static final int REQUESTCODE_TYPE = 4;
	//private static final int REQUESTCODE_MOVE = 5;
	
	// ホームディレクトリのパス
	private String homeDir = "";
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments
	 * for each of the sections. 
	 * We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
	 * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.v(TAG, "onCreate");
		
		setContentView(R.layout.activity_main);
		
		// 設定情報を取得する
		getSettings();
		
		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding tab.
		// We can also use ActionBar.Tab#select() to do this if we have a reference to the
		// Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, 
			// as the listener for when this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0: return getString(R.string.tab_name_battle).toUpperCase();
			case 1: return getString(R.string.tab_name_user).toUpperCase();
			case 2: return getString(R.string.tab_name_envi).toUpperCase();
			case 3: return getString(R.string.tab_name_common).toUpperCase();
			case 4: return getString(R.string.tab_name_settings).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public DummySectionFragment() {
		}
		
		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			// レイアウトIDを格納する場所
			int layoutID = 0;
			
			// 選択したタブのID
			int tabID = getArguments().getInt(ARG_SECTION_NUMBER);
			
			// タブによってレイアウトIDを決定する
			switch(tabID){
			default:
				layoutID = 0;
				break;
			case 1:
				// 対戦タブのレイアウト
				layoutID = R.layout.tab_battle;
				break;
			case 2:
				// 個人タブのレイアウト
				layoutID = R.layout.tab_user;
				break;
			case 3:
				// 環境タブのレイアウト
				layoutID = R.layout.tab_battle;
				break;
			case 4:
				// 一般タブのレイアウト
				layoutID = R.layout.tab_common;
				break;
			case 5:
				// 設定タブのレイアウト
				layoutID = R.layout.tab_settings;
				break;
			}
			
			// レイアウトIDが不正な時，ダミーを表示する
			if(layoutID == 0){
				TextView textView = new TextView(getActivity());
				textView.setGravity(Gravity.CENTER);
				Bundle args = getArguments();
				textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
				return textView;
			}
			
			// 指定したレイアウトを取得する
			View view = inflater.inflate(layoutID, container, false);
			
			// レイアウト内部の設定を行う
			setTabLayout(view, tabID);
			
			return view;
		}
		
		// 指定されたレイアウトの内部を設定する
		private void setTabLayout(View view, int tabID){
			
			// タブIDによって処理を分岐させる
			switch(tabID){
			default:
				break;
			case 1:
				// バトルタブのレイアウトを設定する
				setBattleTabLayout(view);
				break;
			case 2:
				// 個人データタブのレイアウトを設定する
				setUserTabLayout(view);
				break;
			case 3:
				// 環境データタブのレイアウトを設定する
				setEnviTabLayout(view);
				break;
			case 4:
				// 一般データタブのレイアウトを設定する
				setCommonTabLayout(view);
				break;
			case 5:
				// 設定タブのレイアウトを設定する
				setSettingsTabLayout(view);
				break;
			}
		}
		
		//TODO バトルタブのレイアウトを設定する
		private void setBattleTabLayout(View view){
			
		}
		
		// 個人データタブのレイアウトを設定する
		private void setUserTabLayout(View view) {
			
			// 個体編集ボタンを設定する
			setMembersButton(view);
			
			// チーム編集ボタンを設定する
			setTeamsButton(view);
			
			// 試合ログ編集ボタンを設定する
			setLogButton(view);
			
			// 人物編集ボタンを設定する
			setTrainerButton(view);
			
			// エイリアス編集ボタンを設定する
			setAliasButton(view);
		}
		
		// 個体データボタンを設定する
		private void setMembersButton(View view){
			
			Button button = (Button)view.findViewById(R.id.button_members);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// 個体編集モードのIntentを作成する
					//Intent intent = new Intent(getActivity(), MembersList.class);
					Intent intent = new Intent(getActivity(), MemberActivity.class);
					
					startActivityForResult(intent, REQUESTCODE_MEMBERS);
				}
			});
		}
		
		//TODO チーム編集ボタンを設定する
		private void setTeamsButton(View view){
			Button button = (Button)view.findViewById(R.id.button_teams);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// トースト出力する
					Toast.makeText(getActivity(), "チーム", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		//TODO 試合ログ編集ボタンを設定する
		private void setLogButton(View view){
			Button button = (Button)view.findViewById(R.id.button_log);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// トースト出力する
					Toast.makeText(getActivity(), "試合ログ", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		//TODO 人物編集ボタンを設定する
		private void setTrainerButton(View view){
			Button button = (Button)view.findViewById(R.id.button_trainer);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// トースト出力する
					Toast.makeText(getActivity(), "トレーナーメモ", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		// エイリアス編集ボタンを設定する
		private void setAliasButton(View view){
			Button button = (Button)view.findViewById(R.id.button_alias);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// 個体編集モードのIntentを作成する
					//Intent intent = new Intent(getActivity(), AliasList.class);
					Intent intent = new Intent(getActivity(), AliasActivity.class);
					
					startActivityForResult(intent, REQUESTCODE_ALIAS);
				}
			});
		}
		
		// 環境データタブのレイアウトを設定する
		private void setEnviTabLayout(View view){

			// 夢特性編集ボタンを設定する
			setDreamButton(view);
		}

		//TODO 夢特性編集ボタンを設定する
		private void setDreamButton(View view){
			
			// 環境タブに移動する
			
		}
		
		// 一般データタブのレイアウトを設定する
		private void setCommonTabLayout(View view) {
			
			// 種族閲覧ボタンを設定する
			setRaceButton(view);
			
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
		private void setRaceButton(View view) {
			Button button = (Button)view.findViewById(R.id.button_race);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// 種族閲覧モードのIntentを作成する
					Intent intent = new Intent(getActivity(), RaceActivity.class);
					startActivityForResult(intent, REQUESTCODE_RACE);
				}
			});
		}
		
		// タイプ閲覧ボタンを設定する
		private void setTypeButton(View view){
			Button button = (Button)view.findViewById(R.id.button_type);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// タイプ閲覧モードのIntentを作成する
					Intent intent = new Intent(getActivity(), TypeActivity.class);
					startActivityForResult(intent, REQUESTCODE_TYPE);
				}
			});
		}
		
		//TODO 技閲覧ボタンを設定する
		private void setMoveButton(View view){
			Button button = (Button)view.findViewById(R.id.button_move);
			button.setOnClickListener(new OnClickListener() {
				@Override
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
			Button button = (Button)view.findViewById(R.id.button_ability);
			button.setOnClickListener(new OnClickListener() {
				@Override
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
			Button button = (Button)view.findViewById(R.id.button_item);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Toast.makeText(getActivity(), "道具", Toast.LENGTH_SHORT).show();
					
					// タイプ閲覧モードのIntentを作成する
					//Intent intent = new Intent(getActivity(), MoveView.class);
					//startActivityForResult(intent, REQUESTCODE_MOVE);
				}
			});
		}

		// 設定タブのレイアウトを設定する
		private void setSettingsTabLayout(View view){
			
			// 設定データを取得する
			FragmentActivity fa = getActivity();
			SharedPreferences sp = fa.getSharedPreferences(
					getActivity().getString(R.string.app_name),MODE_PRIVATE);

			// 設定タブの再読み込みボタンを設定する
			setReloadSettingsButton(view, fa, sp);
			
			// 設定タブの保存ボタンを設定する
			setSaveSettingsButton(view, fa, sp);
			
			// 設定タブの再読み込みを実行する
			reloadSettings(view, fa, sp);
		}
		
		// 設定タブの再読み込みボタンを設定する
		private void setReloadSettingsButton(final View view, final FragmentActivity fa,
				final SharedPreferences sp){
			
			// 再読み込みボタンを取得し設定する
			Button button = (Button)view.findViewById(R.id.button_reload);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// 設定タブの再読み込みを実行する
					reloadSettings(view, fa, sp);
				}
			});
		}
		
		//TODO 設定タブの保存ボタンを設定する
		private void setSaveSettingsButton(final View view, final FragmentActivity fa,
				final SharedPreferences sp){
			
			// 保存ボタンを取得し設定する
			Button button = (Button)view.findViewById(R.id.button_save);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// 設定の保存を実行する
					saveSettings(view, fa, sp);
					
					// 設定タブの再読み込みを実行する
					reloadSettings(view, fa, sp);
				}
			});
		}
		
		// 設定の保存を実行する
		private void saveSettings(View view, FragmentActivity fa, SharedPreferences sp){
			
			// 書き込み用のエディターを作成する
			Editor editor = sp.edit();
			
			// ホームディレクトリの新しい値を取得する
			String path = ((EditText)view.findViewById(R.id.editText_homeDir)).getText().toString();
			
			// ホームディレクトリの値を更新する
			editor.putString(fa.getString(R.string.key_homedir), path);
			
			//TODO 名前の新しい値を取得する
			// 名前の値を更新する
			
			// 変更を保存する
			if(editor.commit()){
				
				// トースト出力をする
				Toast.makeText(getActivity(), "設定を保存しました", Toast.LENGTH_SHORT).show();
			}
		}
		
		// 設定タブの再読み込みを実行する
		private void reloadSettings(View view, FragmentActivity fa, SharedPreferences sp){

			// 設定タブのホームディレクトリパス欄を設定する
			setHomePathEdit(view, fa, sp);
			
			// 設定タブの名前欄を設定する
			setTrainerNameEdit(view, fa, sp);
		}
		
		// 設定タブのホームディレクトリパス欄を設定する
		private void setHomePathEdit(View view, FragmentActivity fa, SharedPreferences sp){
			
			// テキスト入力欄を取得する
			EditText editText = (EditText)view.findViewById(R.id.editText_homeDir);
			
			// テキスト入力欄に設定値を入れる
			editText.setText(sp.getString(fa.getString(R.string.key_homedir), ""));
		}
		
		//TODO 設定タブの名前欄を設定する
		private void setTrainerNameEdit(View view, FragmentActivity fa, SharedPreferences sp){
			
			// テキスト入力欄を取得する
			
			// テキスト入力欄に設定値を入れる
		}
	}
	
	/**
	 * 別アクティビティから戻ってきた時
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.d(TAG, "onActivityResult");
		
		switch(requestCode){
		default:
			break;
		case REQUESTCODE_MEMBERS:
			Log.d(TAG, "request: MembersList");
			if (resultCode == RESULT_OK) {
				Log.d(TAG, "result: OK");
			}
		case REQUESTCODE_ALIAS:
			Log.d(TAG, "request: AliasList");
			if (resultCode == RESULT_OK) {
				Log.d(TAG, "result: OK");
			}
		}
	}
	
	/**
	 * 設定情報を取得する
	 */
	private void getSettings(){
		
		// 設定を取得する
		SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		
		// ホームディレクトリのパスを取得する
		homeDir = sp.getString(this.getString(R.string.key_homedir), "");
		
		// ホームディレクトリが未設定のとき
		if(homeDir.equals("")){
			
			// デフォルト値の設定を確認する
			setHomeDirDialog();
		}
	}
	
	/**
	 * ホームディレクトリのデフォルト値を確認する
	 */
	private void setHomeDirDialog(){
		
		// タブ位置を設定に移動させる
		
		// デフォルト値を取得する
		final String path = getString(R.string.path_homedir);
		
		// 確認用のダイアログを表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("ホームディレクトリの設定");
		builder.setMessage("以下の場所をホームディレクトリとします\n"
				.concat(path).concat("\nよろしいですか？"));
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ホームディレクトリのデフォルト値を保存する
				saveHomeDir(path);
			}
		});
		builder.setNegativeButton("キャンセル", null);
		builder.show();
	}
	
	/**
	 * ホームディレクトリの値を保存する
	 * @param path : 新しいホームディレクトリの値
	 */
	protected void saveHomeDir(String path) {
		
		// 設定を編集モードで取得する
		Editor editor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
		
		// 設定にホームディレクトリを追加する
		editor.putString(this.getString(R.string.key_homedir), path);
		
		// 設定を保存する
		editor.commit();
	}
}
