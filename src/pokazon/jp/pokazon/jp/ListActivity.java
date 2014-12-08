package pokazon.jp;

import android.app.Activity;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

	//SQLiteデータベース空間を操作するインスタンス変数を宣言
		SQLiteDatabase sdb = null;

		//MySQLiteOpenHelperを操作するインスタンス変数を宣言
		//MySQLiteOpenHelper helper = null;

		// リストにて選択したHitokotoテーブルのレコードの「_id」カラム値を保持する変数を宣言
		int selectedID = -1;

		//リストにて選択した行番号を保持する変数の宣言（行の色を変えたり消したりするため）
		int lastPosition = -1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		ListView listkami = (ListView)findViewById(R.id.listKami);
		listkami.setOnItemClickListener(this);

	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ

		// 前に選択中の行があれば、背景色を透明にする
		if(this.selectedID != -1){
			parent.getChildAt(this.lastPosition).setBackgroundColor(0);
		}
		//選択中の行の背景色をグレーにする
		view.setBackgroundColor(android.graphics.Color.LTGRAY);

		//選択中のレコードを指し示すカーソルを取得
		SQLiteCursor cursor = (SQLiteCursor)parent.getItemAtPosition(position);
		// カーソルのレコードから、「_id」の値を取得して記憶
		this.selectedID = cursor.getInt(cursor.getColumnIndex("_id"));
		// 何行目を選択したか記憶
		this.lastPosition = position;


/*---------------自分で削除を書いた分--------------------------
		ListView listView = (ListView) parent;
		SQLiteCursor cursor = (SQLiteCursor)listView.getItemAtPosition(position);
		String Id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
		//Toast.makeText(getApplicationContext(), Id, Toast.LENGTH_LONG).show();

		selectedID = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)); // トーストにindexを表示
*/

	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
