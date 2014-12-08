package pokazon.jp;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLiteOpenHelper extends SQLiteOpenHelper {


	/**
	 *
	 * @param context 呼び出しコンテキスト
	 * @param name 利用DB名
	 * @param factory カーソルファクトリー
	 * @param version DBバージョン
	 *
	 */

	public MySQLiteOpenHelper(Context context){
		super(context, "kamishibaidb.sqlite3", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
		Log.d("Helper:onCreate","紙芝居テーブル作成");
		db.execSQL("CREATE TABLE IF NOT EXISTS Kamishibai(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, kname TEXT)");
		Log.d("Helper:onCreate","写真テーブル作成");
		db.execSQL("CREATE TABLE IF NOT EXISTS Photo1(_id INTEGER PRIMARY KEY AUTOINCREMENT, page INTEGER NOT NULL, pass TEXT, time INTEGER, yomi TEXT, no INTEGER NOT NULL)" );
		Log.d("Helper:onCreate","テーブル作成完了");
		db.execSQL("CREATE TABLE IF NOT EXISTS"+
		" Serifu( _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,phrase TEXT )");
		Log.d("Helper:onCreate","Serifuテーブル作成完了");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
		db.execSQL("DROP TABLE Kamishibai");
		db.execSQL("DROP TABLE Photo1");
		db.execSQL("drop table Serifu;");
		onCreate(db);

	}


	/**
	 *
	 * 紙芝居のマスタテーブルを作る
	 *
	 * makeKamishibai
	 *
	 * @param SQLiteDatabase インサート先のインスタンス
	 * @param title 紙芝居のタイトル
	 * @return int 紙芝居ID 初期値 -1
	 */

	// 紙芝居のデータを作る
	// 紙芝居idを返す
	public int makeKamishibai(SQLiteDatabase db, String title){

		int kamiID = -1;

		Log.d("makeKamishibai","紙芝居テーブルのinsert文");
		String sqlstr = "INSERT INTO Kamishibai (kname) VALUES('" + title + "');";
		Log.d("SQLHelper",sqlstr);

		try{
			//トランザクション開始
			db.beginTransaction();
			db.execSQL(sqlstr);
			//トランザクション成功
			db.setTransactionSuccessful();

		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			//db.endTransaction();
		}

		// 写真テーブル作成の準備
		Log.d("makeKami","Photo作成");
		String sqlstr2 = "SELECT _id FROM Kamishibai WHERE kname = '"+ title +"';";

		// 写真テーブル作成
		try{
			//トランザクション開始
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr2, null);
			if(cursor.getCount() != 0){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
				kamiID = cursor.getInt(0);
			}
			//カーソルをリターンするのでcloseしない
			//cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			db.endTransaction();
		}
		Log.d("makeKami","makePhotoへ");


		return kamiID;
	}


	/**
	 *  紙芝居写真の領域作成と初期化
	 *  Photo1テーブルに k_idのデータがないときにレコードを10個作る
	 * @param db
	 * @param _id 紙芝居ID
	 */
	public void makePhoto(SQLiteDatabase db, int _id){

		// kamishibaiテーブルの_idは、Photo1テーブルのnoと紐づける
		int kno = _id; // 紙芝居番号
		/*
		for(int i = 0; i < 10; i++){
			String sqlstr = "INSERT INTO Photo1 (page, pass, time, no) VALUES("+ i + ",'',5,"+ kno +");";

			Log.d("makePhoto",sqlstr);
			try{
				//トランザクション開始
				db.beginTransaction();
				db.execSQL(sqlstr);
				//トランザクション成功
				db.setTransactionSuccessful();

			} catch(SQLException e) {
				Log.e("ERROR", e.toString());
			}finally{
				db.endTransaction();
			}
		}
		*/
		// まとめて INSERTする
//		String sqlstr = "INSERT INTO Photo1 SELECT 0 AS page, 'foo' AS pass, 0 AS time, 0 AS no " +
//				"UNION ALL SELECT 0, '', 5, "+ kno +
//				" UNION ALL SELECT 1, '', 5, "+ kno +
//				" UNION ALL SELECT 2, '', 5, "+ kno +
//				" UNION ALL SELECT 3, '', 5, "+ kno +
//				" UNION ALL SELECT 4, '', 5, "+ kno +
//				" UNION ALL SELECT 5, '', 5, "+ kno +
//				" UNION ALL SELECT 6, '', 5, "+ kno +
//				" UNION ALL SELECT 7, '', 5, "+ kno +
//				" UNION ALL SELECT 8, '', 5, "+ kno +
//				" UNION ALL SELECT 9, '', 5, "+ kno+";";


		String sqlstr = "INSERT INTO Photo1(page, time, no) "+
				"VALUES(1,5,"+ kno +"),(2,5,"+ kno +"),(3,5,"+ kno +"),(4,5,"+ kno +"),(5,5,"+ kno +")," +
						"(6,5,"+ kno +"),(7,5,"+ kno +"),(8,5,"+ kno +"),(9,5,"+ kno +"),(10,5,"+ kno +");";
		Log.d("makePhoto",sqlstr);
		try{
			//トランザクション開始
			db.beginTransaction();
			db.execSQL(sqlstr);
			//トランザクション成功
			db.setTransactionSuccessful();

		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			db.endTransaction();
		}

		return;
	}

	/**
	 * 紙芝居一覧を検索
	 * @param db
	 * @return List<String> 紙芝居
	 */
	public List<String> selectKamishibailist(SQLiteDatabase db){

		List<String> rtList = new ArrayList<String>();
		String sqlstr = "SELECT kname FROM Kamishibai ORDER BY _id;";
		Log.d("selectKamishibai",sqlstr);
		try{
			//トランザクション開始
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount() != 0){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
				while(cursor.moveToNext()){
					String kname = cursor.getString(0);
					rtList.add(kname);
				}
			}
			//カーソルをリターンするのでcloseしない → する
			cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			//
		}
		return rtList;
	}

	/**
	 *  紙芝居名で検索して紙芝居番号を受け取る
	 * @param db
	 * @param kname
	 * @return int 紙芝居番号 初期値 -1
	 */
	public int selectKamishibai(SQLiteDatabase db, String kname){

		int rtInt = -1;
		String sqlstr = "SELECT _id FROM Kamishibai where kname = '" + kname + "' ORDER BY _id" ;
		Log.d("selectKamishibai",sqlstr);
		try{
			//トランザクション開始
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount() != 0){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
				rtInt = cursor.getInt(0);
			}
			//カーソルをリターンするのでcloseしない → する
			cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			//
		}
		return rtInt;
	}

	/**
	 * 紙芝居番号をもとにPhoto1テーブルを検索
	 * @param db
	 * @param no 紙芝居番号
	 * @return SQLiteCursor
	 */
	public SQLiteCursor selectPhoto1(SQLiteDatabase db,int no) {

		SQLiteCursor cursor = null;

		String sqlstr = "SELECT _id, page, pass, time, yomi, no  FROM Photo1 WHERE no = "+ no +" ORDER BY page;";
		Log.d("selectPhoto",sqlstr);
		try{
			//トランザクション開始
			cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount() != 0 ){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
			}
			//cursorは呼び出しもとへ返すからここではcloseしない
			// cursor.close();

		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{

		}
		return cursor;
	}

	/**
	 * 写真テーブルに画像ファイルパスを更新
	 * @param db
	 * @param page
	 * @param kamiID
	 * @param path
	 */
	public void UpdatePhoto1(SQLiteDatabase db, int page, int kamiID, String path){

		String sqlstr = "UPDATE Photo1 SET pass = '" + path + "' WHERE no = "+ kamiID +" AND page = " + page + " ;";
		Log.d("UpdatePhoto1",sqlstr);
		try{
			//トランザクション開始
			db.beginTransaction();
			db.execSQL(sqlstr);
			//トランザクション成功
			db.setTransactionSuccessful();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			db.endTransaction();
		}

	}

	/**
	 * 写真テーブルを紙芝居番号で検索し、ページ番号順にファイルパスのリストを取得
	 * @param db
	 * @param kamiID
	 * @return ファイルパスリスト
	 */
	public List<String> getFilePathList(SQLiteDatabase db, int kamiID){
		List<String> rtList = new ArrayList<String>();

		String sqlstr = "SELECT pass FROM Photo1 where no = " + kamiID  + " ORDER BY page;" ;
		Log.d("getFilePathList",sqlstr);
		String filepath = null;
		try{
			//トランザクション開始
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount() != 0){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
				filepath = cursor.getString(0);
				if(filepath!=null){
					rtList.add(filepath);
				}
				while(cursor.moveToNext()){
					filepath = cursor.getString(0);
					if(filepath!=null){
						rtList.add(filepath);
					}
				}
			}
			//カーソルをリターンするのでcloseしない → する
			cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			//
		}
		return rtList;
	}


	/**
	 * 写真テーブルを紙芝居番号、ページ番号で検索し、画像ファイルパスを取得
	 * @param db
	 * @param kamiID
	 * @return ファイルパス
	 */
	public String getFilePath(SQLiteDatabase db, int kamiID, int page){
		String rtString = null;

		String sqlstr = "SELECT pass FROM Photo1 where no = " + kamiID  + " AND page = " + page + ";" ;
		Log.d("getFilePath",sqlstr);
		try{
			//トランザクション開始
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount() != 0){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
				rtString = cursor.getString(0);
			}
			//カーソルをリターンするのでcloseしない → する
			cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			//
		}
		return rtString;
	}

	public void insertSerifu (SQLiteDatabase db,String inputMsg){
	String sqlstr = "insert into Serifu (phrase)values(' "+ inputMsg +" ');";
	try{
		db.beginTransaction();
		db.execSQL(sqlstr);

		db.setTransactionSuccessful();
	}catch (SQLException e){
		Log.e("ERROR",e.toString());
		}finally{
			db.endTransaction();
		}
	return;

	}

	public SQLiteCursor selectSerifuList(SQLiteDatabase db){
		SQLiteCursor cursor = null;

		String sqlstr = "SELECT _id,phrase FROM Serifu ORDER BY _id;";
		try{
			cursor = (SQLiteCursor)db.rawQuery(sqlstr,null);
			if(cursor.getCount()!=0){

				cursor.moveToFirst();
			}
		} catch (SQLException e){
			Log.e("ERROR",e.toString());
		} finally{

		}
		return cursor;
	}
	public void deleteSerifu(SQLiteDatabase db,int id){

		String sqlstr = "DELETE FROM Serifu where _id = " + id +";";
		try{
			db.beginTransaction();
			db.execSQL(sqlstr);

			db.setTransactionSuccessful();
		}catch (SQLException e){
			Log.e("ERROR",e.toString());
		}finally {
			db.endTransaction();

		}
	}

}
