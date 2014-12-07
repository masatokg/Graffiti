package pokazon.jp;

import android.app.Activity;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class KousinActivity extends Activity implements
View.OnClickListener,AdapterView.OnItemClickListener{

		SQLiteDatabase sdb = null;

		MySQLiteOpenHelper helper = null;


		int selectedID = -1;

		int lastPosition = -1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kousin);
	}


	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();



		Button button1 = (Button)findViewById(R.id.button1);
		Button button2 = (Button)findViewById(R.id.button2);
		ListView listView1 = (ListView)findViewById(R.id.listView1);


		button1.setOnClickListener(this);
		button2.setOnClickListener(this);


		listView1.setOnItemClickListener(this);

		this.setDBValuetoList(listView1);
	}

		@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ
		if(this.selectedID!=-1){
			parent.getChildAt(this.lastPosition).setBackgroundColor(0);
		}

		view.setBackgroundColor(android.graphics.Color.LTGRAY);

		SQLiteCursor cursor = (SQLiteCursor)parent.getItemAtPosition(position);

		this.selectedID = cursor.getInt(cursor.getColumnIndex("_id"));

		this.lastPosition = position;

	}


		private void setDBValuetoList(ListView listView1){

			SQLiteCursor cursor = null;
			helper = new MySQLiteOpenHelper(getApplicationContext());

			if(sdb == null){
				helper = new MySQLiteOpenHelper(getApplicationContext());
			}
			try{
				sdb = helper.getWritableDatabase();
			}catch(SQLiteException e){
				Log.e("ERROR",e.toString());
			}

			cursor = this.helper.selectSerifuList(sdb);

			int db_layout = android.R.layout.simple_list_item_activated_1;

			String[]from = {"phrase"};

			int[] to = new int[]{android.R.id.text1};


			SimpleCursorAdapter adapter =
					new SimpleCursorAdapter(this,db_layout,cursor,from,to,0);

			listView1.setAdapter(adapter);
			}


		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ


			switch(v.getId()){
			case R.id.button1:


				if(this.selectedID != -1){
					helper.deleteSerifu(sdb, this.selectedID);
					ListView listView1 = (ListView)findViewById(R.id.listView1);

					this.setDBValuetoList(listView1);

					this.selectedID = -1;
					this.lastPosition = -1;
				}
				else{

					Toast.makeText(KousinActivity.this,"削除する行を選んでください",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.button2:

				finish();
				break;



			}
		}
	}
