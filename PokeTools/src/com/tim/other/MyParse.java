package com.tim.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyParse {

	/////////////////////////////////
	// CSVのパース                 //
	// 改変元: http://goo.gl/A4G6c //
	/////////////////////////////////

	private static final int STRING_BUFFER_SIZE = 1024;

	/**
	 * BufferedReaderから1レコード分のテキストを取り出す。
	 * セル内での改行をチェックし，必要なら複数行を結合して返す
	 * 改行は\nに変換される
	 * @param reader	: 行データを取り出すBufferedReader。
	 * @return			: 1レコード分のテキスト。 ※ 1行とは限らない もしくはnull
	 * @throws			: IOException 入出力エラー
	 */
	public String buildRecord (BufferedReader reader)throws IOException{

		// 1行読み込む
		String result = reader.readLine();

		// ダブルクォーテーションの位置
		int pos;

		// テキストが存在し，ダブルクォーテーションがあった時
		if (result != null && 0 < result.length() && 0 <= (pos = result.indexOf("\""))){

			// セル内かセル外かの状態確認，Trueでセル内
			boolean inString = true;

			// 最後に読み込んだ1行
			String rawline = result;

			// 追加で読み込んだ新しい1行
			String newline = null;

			// 全体保存用のバッファー
			StringBuffer buff = new StringBuffer(STRING_BUFFER_SIZE);

			// ループ処理
			while (true) {

				// 次のダブルクォーテーションを探し，posを更新する
				// 1. 最後に見つけたposの次からダブルクォーテーションを探す
				// 2. クオーテーションを発見する度，セル内とセル外を変更する
				// 3. ダブルクォーテーションがなくなるまでループ
				while (0 <= (pos = rawline.indexOf("\"", ++pos))) {
					inString = !inString;
				}

				// この時点でposは必ず-1
				// inStringの値によって状態が決まる
				// T: セル内で改行したので，次の行も同じレコードの一部
				// F: セル外で改行したので，ここでレコードは終了

				// セル内で改行されたとき，もう1行読み込む
				if (inString && (newline = reader.readLine()) != null) {

					// 読込に成功したとき

					// 全体のバッファーに古い行を追加する
					buff.append(rawline);

					// 古い1行の最後に改行を追加する（表示用だしこれでいい）
					buff.append("\n");

					// posを初期化する
					pos = -1;

					// 新しい行を探索対象とする
					rawline = newline;

					// 新しい行に対して，セル内での改行を検証する
					continue;
				} else {

					// セル外で改行されたとき
					// もしくは新しい行が読み込めなかった時

					// セル内なのに新しい行が読み込めなかった
					// もしくはバッファーが空欄の時
					if (inString || 0 < buff.length()) {

						// 最後に読み込んだ行をバッファーに追加する
						buff.append(rawline);

						// セル内で改行された時（本来フォーマット違反）
						if (inString) {
							// バッファーの最後にダブルクォーテーションを追加する
							buff.append("\"");
						}

						// バッファーの内容を結果とする
						result = buff.toString();
					}
					break;
				}
			}
		}

		// 最初に読み込んだ1行
		// もしくは結合した複数行を結果として戻す
		return result;
	}
	
	/**
	 * 1レコード分のテキストを分割してフィールドの配列にする。
	 * @param src	: 1レコード分のテキストデータ。
	 * return		: フィールドの配列。
	 */
	public static ArrayList<String> splitRecord (String src) {
		
		ArrayList<String> reuslt = new ArrayList<String>();
		
		// レコードの最終文字をチェックする
		
		// レコードを半角カンマで分割する
		String[] columns = src.split(",", -1);
		
		// 配列の最大長として列数を入れる
		int maxlen = columns.length;
		
		int startPos, endPos, columnlen;
		
		// 
		StringBuffer buff = new StringBuffer(STRING_BUFFER_SIZE);
		String column;
		boolean isInString, isEscaped;
		
		for (int index = 0; index < maxlen; index++) {
			
			// 1カラムを取得する
			column = columns[index];
			
			// カラム内にダブルクォーテーションがなかった時
			if ((endPos = column.indexOf("\"")) < 0) {
				
				// 配列の末尾にカラムの内容を追加する
				reuslt.add(column);
			} else {
				
				// カラム内にダブルクォーテーションがあった時
				
				// 発見したのが先頭なら，セル内フラグをTrueにする
				isInString = (endPos == 0);
				
				// エスケープフラグをFalseにする
				isEscaped = false;
				
				// カラムの文字サイズを取得する
				columnlen = column.length();
				
				// バッファーをリセットする
				buff.setLength(0);
				
				// セル内フラグに対応した探索開始位置を設定する
				// セル内: 1 ※セル開始位置なので，0番目はあって当然
				// セル外: 0
				startPos = (isInString)? 1: 0;
				
				// startPosがカラム長を超えるまでループ
				while (startPos < columnlen) {
					
					// 次のダブルクォーテーションを探す
					if (0 <= (endPos = column.indexOf("\"", startPos))) {
						
						// ダブルクォーテーションがあった時
						
						// 想定ケース
						// 1. カラム末尾
						// 2. セル内でダブルクォーテーション表記のためのエスケープ
						// 3. エスケープした後のダブルクォーテーション
						
						// 条件分岐してバッファーに追加する
						// 1. 2文字目以降で発見されたとき，その間を追加
						// 2. 1文字目で発見されたとき
						// 2-1. エスケープ状態ならダブルクォーテーションを文字として追加
						// 2-2. エスケープ状態でないなら何も追加しない（多分カラムの末尾）
						buff.append(
								(startPos < endPos)?
										column.substring(startPos, endPos):
										isEscaped? 
												"\"": 
												""
								);
						
						// エスケープフラグを反転する
						isEscaped = !isEscaped;
						
						// セル内フラグを反転する
						isInString = !isInString;
						
						// 探索開始位置を更新する
						startPos = ++endPos;
					} else {
						
						// ダブルクォーテーションが無かった時
						
						// カラムの内容をバッファーに追加する
						buff.append(column.substring(startPos));
						
						// セル内かつ最終カラムでない時
						if (isInString && index < maxlen - 1) {
							
							// 次のカラムを読み込む
							column = columns[++index];
							
							// カラム長を取得する
							columnlen = column.length();
							
							// バッファーの最後にカンマを追加する
							buff.append(",");
							
							// 次のカラムの探索開始位置を設定する
							startPos = 0;
						} else {
							
							// セル外もしくは最終カラムの時
							break;
						}
					}
				}
				
				// バッファーの内容を配列の末尾に追加する
				reuslt.add(buff.toString());
			}
		}
		
		return reuslt;
	}
}


