package pokazon.jp;


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
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
		db.execSQL("DROP TABLE Kamishibai");
		db.execSQL("DROP TABLE Photo1");
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
	 * @return 
	 */

	// 紙芝居のデータを作る
	// 紙芝居idを返す
	public String makeKamishibai(SQLiteDatabase db, String title){

		String mk = null;

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
				mk = cursor.getString(0);
			}
			//カーソルをリターンするのでcloseしない
			//cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			db.endTransaction();
		}
		Log.d("makeKami","makePhotoへ");


		return mk;
	}


	// 紙芝居写真の領域作成と初期化
	// Photo1テーブルに k_idのデータがないときに作る
	public void makePhoto(SQLiteDatabase db, String _id){
		String kno = _id; // 紙芝居の id
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

	public String selectKamishibai(SQLiteDatabase db){

		String rtString = null;
		String sqlstr = "SELECT kname FROM Kamishibai ORDER BY _id;";
		Log.d("selectKamishibai",sqlstr);
		try{
			//トランザクション開始
			SQLiteCursor cursor = (SQLiteCursor)db.rawQuery(sqlstr, null);
			if(cursor.getCount() != 0){
				//カーソル開始位置を先頭にする
				cursor.moveToFirst();
				rtString = cursor.getString(0);
			}
			//カーソルをリターンするのでcloseしない
			//cursor.close();
		} catch(SQLException e) {
			Log.e("ERROR", e.toString());
		}finally{
			//
		}
		return rtString;
	}


	public SQLiteCursor selectPhoto1(SQLiteDatabase db,String no) {

		SQLiteCursor cursor = null;
		
		String sqlstr = "SELECT _id,page FROM Photo1 WHERE no = "+ no +" ORDER BY page;";
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
	
	public void UpdatePhoto1(){
		
	}














}
