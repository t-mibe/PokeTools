package com.tim.dialog;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tim.data.Alias;
import com.tim.data.Member;
import com.tim.data.Race;
import com.tim.other.MyParse;
import com.tim.other.Orientation;
import com.tim.poketools.R;

/**
 * 個体データの閲覧ダイアログを生成するクラス
 * 編集ダイアログは別に用意する
 * @author mibe
 *
 */
public class MemberViewDialog extends ViewDialog {

	//////////////////
	// ここから定数 //
	//////////////////

	// レコードの要素数
	public static final int RECORD_SIZE = Member.RECORD_SIZE;

	////////////////////////////
	// ここからグローバル変数 //
	////////////////////////////

	// エイリアス変換クラス
	private Alias alias = null;

	// 種族表示ダイアログ生成クラス
	private RaceViewDialog rDialog = null;

	////////////////////////
	// ここから初期化処理 //
	////////////////////////

	public MemberViewDialog(Context context) {
		super(context, new Member(context));

		alias = new Alias(context);
		rDialog = new RaceViewDialog(context);
	}
	
	//////////////////////////////////////
	// ここからダイアログのタイトル処理 //
	//////////////////////////////////////
	
	/**
	 * レコード文からタイトル文を作成する
	 * @param record : 個体データのレコード文
	 * @return       : 生成したタイトル文
	 */
	@Override
	public String getDialogTitleByRecord(String record) {

		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードサイズが不正ならエラー文を返す
		if(list_record.size() < RECORD_SIZE)return "不正なデータです";

		// 必要な値を取得する
		String name = (String) list_record.get(Member.RECORD_ID_NAME);
		String race = (String) list_record.get(Member.RECORD_ID_RACE);
		String gender = (String) list_record.get(Member.RECORD_ID_GENDER);
		String nature = (String) list_record.get(Member.RECORD_ID_NATURE);

		// 性別以外のレコード文を作成する
		String text = name.concat("（").concat(race).concat("）").concat(nature);
		
		// 性別が有効な時のみ性別も追加する
		if(gender.equals("♂") || gender.equals("♀")){
			text = text.concat(gender);
		}
		// タイトル文を返す
		return text;
	}

	//////////////////////////////////////
	// ここからダイアログ内部の表示処理 //
	//////////////////////////////////////

	/**
	 * レコード文からダイアログを生成する
	 */
	@Override
	public View getDialogViewByRecord(String record) {

		// レコードを分割する
		ArrayList<String> list_record = MyParse.splitRecord(record);

		// レコードの要素数が不正ならnullを返す
		if(list_record.size() < RECORD_SIZE)return null;

		// XMLリソースからViewを作成する
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_member_view, null);

		// 画面が縦向きなら，レイアウトを変更する
		if(Orientation.isPortrait((Activity)context)) setViewPortrait(view);

		// 個体閲覧ダイアログのViewを設定する
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

		layout = (LinearLayout)view.findViewById(R.id.member_view);
		layout.setOrientation(LinearLayout.VERTICAL);

		layout = (LinearLayout)view.findViewById(R.id.member_view_move12);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		layout = (LinearLayout)view.findViewById(R.id.member_view_move34);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		((TextView)view.findViewById(R.id.member_view_text_memo)).setTextSize(24f);
		((TextView)view.findViewById(R.id.member_view_text_stats)).setTextSize(21f);
	}

	/**
	 * 個体閲覧ダイアログのビューを設定する
	 * @param view			: 編集するレイアウト
	 * @param list_record	: 個体データのレコードを分割した配列
	 * @return				: 成功したらtrue
	 */
	private boolean setDialogView(View view, ArrayList<String> list_record) {

		// アイコンボタンを設定する
		setIconButton(view, list_record.get(Member.RECORD_ID_RACE));

		// 特性ボタンを設定する
		setAbirilyButton(view, list_record.get(Member.RECORD_ID_ABILITY));

		// メモ用テキストを設定する
		setViewMemoText(view, list_record);

		// ステータスの表示テキストを生成する
		String textStats = createTextStats(list_record);

		// ステータスの表示テキスト生成に失敗した時，falseを返す
		if(textStats == null)return false;

		// ステータスの表示テキストを表示する
		TextView stateView = (TextView) view.findViewById(R.id.member_view_text_stats);
		stateView.setText(textStats);

		// ビュー内のボタン4つに技を割り当てる
		setMoveButton4(view, list_record);

		// 成功を示すtrue
		return true;
	}

	/**
	 * アイコンボタンの設定をする
	 * @param view : アイコンボタンがあるView
	 * @param race : 種族名（略称）
	 */
	private void setIconButton(View view, final String race){

		// 種族名をエイリアスチェックする
		final String race_long = alias.checkAlias(race);

		// アイコンボタンを取得する
		ImageButton button = (ImageButton) view.findViewById(R.id.member_view_iconButton);

		// 画像を変更する，失敗したらボタン設定中止
		if(!setIconGIF(button, race_long))return;

		// ボタンを有効にする
		button.setClickable(true);

		// ボタンが押された時の動作を設定する
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewRaceData(race_long); // 種族データをダイアログ表示する
			}
		});
	}

	/**
	 * アイコンボタンの画像を変更する
	 * @param button	: 変更するImageButton
	 * @param race		: 対象となる種族名（正式名称）
	 */
	private boolean setIconGIF(ImageButton button, String race_long) {

		// ボタン内の余白を無効にする
		button.setBackgroundResource(R.drawable.imagebutton);

		// 画像IDを種族名から取得する（画像ファイルモード）
		String raceID = rDialog.getRaceID(race_long);

		// 画像IDが取得できたかチェック
		if(raceID == null || raceID.equals(""))return false;

		// 画像ファイルのパス名
		String pathName = imagePath.concat(raceID).concat(".gif");

		// ファイルの有無をチェックする，ないならfalseを返す
		if(!new File(pathName).isFile())return false;

		// 画像を開く
		Bitmap bitmap = BitmapFactory.decodeFile(pathName);

		// 画像のリサイズ
		float f = ICON_SIZE;
		Matrix matrix = new Matrix();
		float width = f / bitmap.getWidth();
		float height = f / bitmap.getHeight();
		matrix.postScale(width, height);
		Bitmap butmap_resized = Bitmap.createBitmap(
				bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix, true);

		button.setImageBitmap(butmap_resized);

		// 画像変更が成功したとしてTrueを返す
		return true;
	}

	/**
	 * 種族データをダイアログ表示する
	 * @param race: 種族名（正式名称）
	 */
	private void viewRaceData(String race_long){

		// ダイアログを作成し表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(race_long);
		builder.setPositiveButton("OK", null);
		builder.setView(rDialog.getDialogViewByName(race_long));
		builder.show();
	}

	/**
	 * 特性ボタンの設定をする
	 * @param view
	 * @param ability
	 */
	private void setAbirilyButton(View view, final String ability) {

		// 特性ボタンを取得する
		Button button =  (Button)view.findViewById(R.id.member_view_abilityButton);

		// 特性をボタンに貼り付ける
		button.setText(ability);

		// ボタンが押された時の動作を設定する
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewAbilityData(ability); // 特性データをダイアログ表示する
			}
		});
	}

	/**
	 * 特性データをダイアログ表示する
	 * @param ability
	 */
	private void viewAbilityData(String ability) {

		//FIXME 特性データを実装したら更新する

		// ダイアログを作成し表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(ability);
		builder.setPositiveButton("OK", null);
		builder.setMessage("ここに特性データ");
		builder.show();
	}

	/**
	 * メモ用テキストエリアの設定をする
	 * @param view: 編集するビュー
	 * @param data: 個体データの配列
	 */
	private void setViewMemoText(View view, ArrayList<String> list_record){

		// メモを取得する
		String memo = list_record.get(Member.RECORD_ID_MEMO);

		// テキストビューを取得する
		TextView nameView = (TextView) view.findViewById(R.id.member_view_text_memo);

		// 生成したテキストをビューに反映させる
		nameView.setText(memo);
	}

	/**
	 * ステータスの表示テキストを生成する
	 * @param list_record	: 個体データを分割した配列
	 * @return				: 生成した文字列
	 */
	private String createTextStats(ArrayList<String> list_record){

		// レコードからステータスの実値を計算する

		// 種族名から種族値を取得する
		//int rValues[] = getRaceValues(list_record.get(RECORD_ID_RACE));
		//int rValues[] = rDialog.getRaceValue(list_record.get(Member.RECORD_ID_RACE));
		// 種族名の正式名称を取得する
		int rValues[] = new Race(context).getRaceValue(list_record.get(Member.RECORD_ID_RACE));

		// 種族値の取得に失敗した時，nullを返す
		if(rValues == null) return null;

		// 個体値と努力値を取得する
		int iValues[] = new int[6];
		int eValues[] = new int[6];
		for(int i = 0; i < 6; i++){
			iValues[i] = Integer.parseInt("0".concat(list_record.get(i + Member.RECORD_ID_VALUE_I)));
			eValues[i] = Integer.parseInt("0".concat(list_record.get(i + Member.RECORD_ID_VALUE_E)));
		}

		// 性格から性格補正倍率を取得する
		float nValues[] = new Member(context).getNatureValue(list_record.get(Member.RECORD_ID_NATURE));

		// 実値
		int values[] = new Member(context).getStatValue(rValues, iValues, eValues, nValues, 50);

		// 文字埋めのルール
		String rule = "%03d";

		// 数値パラメータをテキストに連結する
		String rText = String.format("種: ".concat(rule), rValues[0]);
		String iText = String.format("個: ".concat(rule), iValues[0]);
		String eText = String.format("努: ".concat(rule), eValues[0]);
		String vText = String.format("実: ".concat(rule), values[0]);
		for(int i = 1; i < 6; i++){
			rText = rText.concat(String.format(", ".concat(rule), rValues[i]));
			iText = iText.concat(String.format(", ".concat(rule), iValues[i]));
			eText = eText.concat(String.format(", ".concat(rule), eValues[i]));
			vText = vText.concat(String.format(", ".concat(rule), values[i]));
		}

		return rText.concat("\n").concat(iText).concat("\n")

				.concat(eText).concat("\n").concat(vText);
	}

	/**
	 * ビュー内のボタン4つに技を割り当てる
	 * 別データの技を読み取ってダイアログorトースト表示でいいかと
	 */
	private void setMoveButton4(View view, ArrayList<String> list_record){

		// 4つのボタンを作成する
		Button button[] = new Button[4];
		button[0] = (Button)view.findViewById(R.id.member_view_move1);
		button[1] = (Button)view.findViewById(R.id.member_view_move2);
		button[2] = (Button)view.findViewById(R.id.member_view_move3);
		button[3] = (Button)view.findViewById(R.id.member_view_move4);

		// それぞれのボタンを設定する
		for(int i = 0; i < 4; i++){
			setMoveButton1(button[i], list_record.get(i + Member.RECORD_ID_MOVE));
		}
	}

	/**
	 * 1つの技ボタンの設定を行う
	 * @param button	: 設定するボタン
	 * @param move_long	: 設定する技の正式名称
	 */
	private void setMoveButton1(Button button, final String move){

		// ここで不適正な技なら"エラー"と出力する押せないボタンにする
		// 技名が空欄の時，ボタンを無効にして終了する
		if(move.equals("")){

			// ボタンを無効にする
			button.setClickable(false);

			return;
		}

		// ボタンのテキストを設定する
		button.setText(move);

		// 技の正式名称を取得する
		final String move_long = alias.checkAlias(move);

		// クリック時の処理を設定する
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 技データをダイアログ表示する
				setMoveInfo(move_long);
			}
		});
	}

	/**
	 * 技データをダイアログ表示する
	 * @param move : 技の正式名称
	 */
	private void setMoveInfo(String move){

		//FIXME 技データが出来たら修正する

		// 技データのダイアログ表示を作成し表示する
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(move);
		builder.setPositiveButton("OK", null);
		builder.setMessage("ここに技データ");
		builder.show();

	}
}
