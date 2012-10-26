package com.tim.dialog;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tim.data.Race;
import com.tim.other.MyParse;
import com.tim.other.Orientation;
import com.tim.poketools.R;

public class RaceViewDialog extends Race {

	//////////////////////
	// ここから定数宣言 //
	//////////////////////

	// アイコンの表示サイズ
	private static final float ICON_SIZE = 150f;

	////////////////////////////
	// ここからグローバル変数 //
	////////////////////////////

	// 種族アイコンディレクトリのパス
	private String imagePath = "";

	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	public RaceViewDialog(Context context) {
		super(context);

		// 画像アイコンディレクトリのパスを設定する
		imagePath = getHomeDir().concat(context.getString(R.string.path_pk));
	}

	//////////////////////////////////////
	// ここからダイアログのタイトル生成 //
	//////////////////////////////////////

	/**
	 * 登録名から種族名を取得し，タイトル文を作成する
	 * @param name : 探索する種族名
	 * @return     : 生成されたタイトル文
	 */
	public String getDialogTitleByName(String name){

		// 該当するレコードを取得する
		String record = getRecord(name);

		// レコードが取得できなかったときnullを返す
		if(record == null)return null;

		// 該当するレコードを取得し，タイトル文を作成する
		return getDialogTitleByRecord(record);
	}

	/**
	 * レコード文からタイトル文を作成する
	 * @param record : レコード文
	 * @return       : 生成したタイトル文
	 */
	public String getDialogTitleByRecord(String record) {

		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードサイズが不正ならエラー文を返す
		if(list_record.size() != RECORD_SIZE)return "不正なデータです";

		// 種族名を返す
		return list_record.get(RECORD_ID_NAME);
	}

	//////////////////////////////////////
	// ここからダイアログの内部表示生成 //
	//////////////////////////////////////

	/**
	 * 種族名からレコードを取得し，Viewを作成する
	 * @param name : 探索する登録名
	 * @return     : 生成したView
	 */
	public View getDialogViewByName(String name){

		// 該当するレコードを取得する
		String record = getRecord(name);

		// レコードが取得できなかったときnullを返す
		if(record == null)return null;

		// 該当するレコードを取得し，Viewを作成する
		return getDialogViewByRecord(record);
	}

	/**
	 * レコード文からViewを作成する
	 * @param record : 表示するレコード
	 * @return       : 生成したView
	 */
	public View getDialogViewByRecord(String record){

		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードサイズが不正ならnullを返す
		if(list_record.size() != RECORD_SIZE)return null;

		// XMLリソースからViewを作成する
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_race_view, null);

		// 画面が縦向きなら，レイアウトを変更する
		if(Orientation.isPortrait((Activity)context)) setViewPortrait(view);

		// 種族閲覧ダイアログのViewを設定する
		if(!setDialogView(view, list_record)){

			// 失敗を示すトーストを出力する
			Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();

			return null;
		}

		return view;
	}

	/**
	 * アイテム選択時のレイアウトを縦向き用に変更する
	 * @param view
	 */
	private void setViewPortrait(View view) {

		LinearLayout layout;

		layout = (LinearLayout)view.findViewById(R.id.race_view);
		layout.setOrientation(LinearLayout.VERTICAL);
	}

	/**
	 * 種族閲覧ダイアログのViewを設定する
	 * @param view
	 * @param list_record
	 * @return
	 */
	private boolean setDialogView(View view, ArrayList<String> list_record){

		// アイコンとタイプ表記を設定する
		setIconAndTypes(view, list_record);

		// 種族値，輝石，♂率，体重用のテキストを設定する
		setTextStats(view, list_record);

		// 特性ボタン3つを設定する
		setAbilityButton3(view, list_record);

		return true;
	}

	// タイプ表記を設定する
	private void setIconAndTypes(View view, ArrayList<String> list_record) {

		// TextViewを取得する
		TextView textView = (TextView)view.findViewById(R.id.race_view_types);

		// 画像IDを種族名から取得する（画像ファイルモード）
		String raceID = list_record.get(RECORD_ID_RACEID);

		// 画像ファイルのパス名を生成する
		String gifPath = imagePath.concat(raceID).concat(".gif");

		// ファイルが存在する時
		if(new File(gifPath).isFile()){

			// 画像を開く
			Bitmap bitmap = BitmapFactory.decodeFile(gifPath);

			// 画像のリサイズ
			float f = ICON_SIZE;
			Matrix matrix = new Matrix();
			float width = f / bitmap.getWidth();
			float height = f / bitmap.getHeight();
			matrix.postScale(width, height);
			Bitmap butmap_resized = Bitmap.createBitmap(
					bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix, true);

			textView.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(null, butmap_resized),null, null, null);
		}

		// タイプを取得する
		String type1 = list_record.get(RECORD_ID_TYPES);
		String type2 = list_record.get(RECORD_ID_TYPES + 1);

		textView.setText(type1.concat("\n").concat(type2));
	}

	// 種族値，輝石，♂率，体重用のテキストを設定する
	private void setTextStats(View view, ArrayList<String> list_record){

		// 桁合わせのルール
		String rule = "%03d";

		// H種族値を取得する
		String text = String.format(rule, Integer.valueOf(list_record.get(RECORD_ID_VALUE_R)));

		// 残りの種族値を追加する
		for(int i = 1; i < 6; i++){
			text = text.concat(", ").concat(String.format(rule, Integer.valueOf(list_record.get(i + RECORD_ID_VALUE_R))));
		}

		// ♂率を取得する（確率を8倍する）
		int maleRate = (int)(Float.valueOf(list_record.get(RECORD_ID_MALERATE)) * 8);

		// テキスト内容によって分岐する
		switch (maleRate) {
		default: // それ以外
			text = text.concat("\n").concat("性別不明\n");
			break;
		case 0: // ♀のみの場合
			text = text.concat("\n").concat("♀のみ\n");
			break;
		case 2: // ♂率 1/4の場合
			text = text.concat("\n").concat("♂:♀ = 1:3\n");
			break;
		case 4: // ♂率 1/2の場合
			text = text.concat("\n").concat("♂:♀ = 1:1\n");
			break;
		case 7: // ♂率 7/8の場合
			text = text.concat("\n").concat("♂:♀ = 7:1\n");
			break;
		case 8: // ♂のみの場合
			text = text.concat("\n").concat("♂のみ\n");
			break;
		}

		// 体重を取得する
		String weight = list_record.get(RECORD_ID_WEIGHT);

		// 体重を追加する
		text = text.concat(weight).concat("kg");
		
		// 体重ランクを取得する
		int weight_rank = new Race(context).getWeightRankByWeight(Float.valueOf(weight));

		// 体重技の倍率を追加する
		text = text.concat("（").concat(Integer.toString(weight_rank)).concat("倍）");

		// 未進化判定を取得する
		String evolution = list_record.get(RECORD_ID_EVOLUTION);

		if(evolution.equals("1"))text = text.concat("，輝石可");

		// テキストビューを取得する
		TextView textView = (TextView)view.findViewById(R.id.race_view_stats);

		// テキストビューで表示するテキストを設定する
		textView.setText(text);
	}

	// 特性ボタン3つを設定する
	private void setAbilityButton3(View view, ArrayList<String> list_record) {

		// 3つのボタンを作成する
		Button button[] = new Button[3];
		button[0] = (Button)view.findViewById(R.id.race_view_ability1);
		button[1] = (Button)view.findViewById(R.id.race_view_ability2);
		button[2] = (Button)view.findViewById(R.id.race_view_ability3);

		// それぞれのボタンを設定する
		for(int i = 0; i < 3; i++){
			setAbilityButton1(button[i], list_record.get(i + RECORD_ID_ABILITIES));
		}
	}

	// 特性ボタン1つを設定する
	private void setAbilityButton1(Button button, final String ability){

		// ここで不適正な技なら"エラー"と出力する押せないボタンにする
		// 特性名が空欄の時，ボタンを無効にして終了する
		if(ability.equals("")){

			// ボタンを無効にする
			button.setClickable(false);

			return;
		}

		// ボタンのテキストを設定する
		button.setText(ability);

		// クリック時の処理を設定する
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 技データをダイアログ表示する
				setAbilityInfo(ability);
			}
		});
	}

	/**
	 * 特性データをダイアログ表示する
	 * @param move : 技の正式名称
	 */
	private void setAbilityInfo(String ability){

		//FIXME 特性データが出来たら修正する

		// 技データのダイアログ表示を作成し表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(ability);
		builder.setPositiveButton("OK", null);
		builder.setMessage("ここに特性データ");
		builder.show();
	}
}
