package pokazon.jp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 紙芝居をつくるページ一覧画面
 * @author
 *
 */
public class MainActivity extends Activity implements View.OnClickListener ,OnItemClickListener{

	//

    private static final int REQUEST_GALLERY = 0;
	private static final int REQUEST_SELECT_IMAGE = 200;
    private ImageView imgViewKuroneko;

    // 選択中の紙芝居番号
    private int _kamiID = -1;

    //SQLiteデータベース空間を操作するインスタンス変数を宣言
    SQLiteDatabase sdb = null;
    //MySQLiteOpenHelperを操作するインスタンス変数を宣言
    MySQLiteOpenHelper helper = null;

    int selectedID = -1;

    int lastPosition = -1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }

    @Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		//ギャラリーボタン
//		Button btnLib = (Button)findViewById(R.id.btn_library);
//		btnLib.setOnClickListener(this);
		//リスト追加ボタン
//		Button btnList = (Button)findViewById(R.id.btnList);
//		btnList.setOnClickListener(this);
		//カメラボタン
//		Button btnCamera = (Button)findViewById(R.id.btn_camera);
//		btnCamera.setOnClickListener(this);
		//リスト
		ListView lstKami = (ListView)findViewById(R.id.listKami);
		lstKami.setOnItemClickListener(this);

		// 前の画面から紙芝居番号を受け取る
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		_kamiID = bundle.getInt("kamiID");

		//ListViewにDBの値をセット
	    this.setDBValueList(lstKami);



        imgViewKuroneko = (ImageView)findViewById(R.id.imageView);

        // クラスのフィールド変数が NULL なら、データベース空間をオープン
        if(sdb == null) {
        	helper = new MySQLiteOpenHelper(getApplicationContext());
        }
        try{
        	sdb = helper.getWritableDatabase();
        } catch(SQLiteException e) {
        	// 異常終了
        	return;
        }

	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE  && resultCode == RESULT_OK){
        	// 画像選択画面からの戻り
             try {
                 // InputStream in = getContentResolver().openInputStream(data.getData());
                 // Bitmap img = BitmapFactory.decodeStream(in);
                 // in.close();
            	 Bundle bundle = data.getExtras();

            	 // 紙芝居番号、ページ番号を取得
            	 int _kamiID = bundle.getInt("kamiID");
            	 int page = bundle.getInt("page");
/*
            	 // 選択した画像のファイルパスを取得し、Bitmapを生成
            	 String filepath = bundle.getString("imagefilePath");
            	 Bitmap bmp = null;
            	 if(filepath != null){
            		 bmp = BitmapFactory.decodeFile(filepath);
            		 bmp = this.changeImageSize(bmp, (float)0.2, (float)0.2);
            	 }
*/
            	 // 選択した画像のファイルパスをSQLiteに保存
            	 String filepath = bundle.getString("imagefilePath");
            	 this.helper.UpdatePhoto1(sdb, page, _kamiID, filepath);


         		//リスト
         		ListView lstKami = (ListView)findViewById(R.id.listKami);
         		lstKami.setOnItemClickListener(this);
         		//ListViewにDBの値をセット
         	    this.setDBValueList(lstKami);

/*
            	 byte[] bytes = bundle.getByteArray("dispImage");

            	 Bitmap bmp = null;
            	 if (bytes != null) {
            		 bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            	 }
            	 //選択した画像を表示
                 imgView.setImageBitmap(bmp);
*/


             } catch(Exception e) {

             }
        }

        // ファイルパス取得のコード
        /*
        if(requestCode == REQUEST_CODE) {
        	switch (resultCode) {
        	case Activity.RESULT_OK:
        		String[] projection = {MediaStore.MediaColumns.DATA};
        		String selection = null;
        		String[] selectionArgs = null;
        		String sortOrder = null;
        		Cursor cursor = getActivity().gtContentResolver().query(data.getData(), projection, selection, selectionArgs, sortOrder);
        		if(cursor.getCount() == 1) {
        			cursor.moveToNext();
        			String filePath = cursor.getString(0);
        			Toast.makeText(getActivity(),filePath,Toast.LENGTH_SHORT).show();
        		}
        	}
        }
        */
    }

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
        // ギャラリー呼び出し
		Intent intent = new Intent();
//		switch(v.getId()){
//		case R.id.btn_camera:
//			break;
//		case R.id.btn_library:
//	        intent.setType("image/*");
//	        intent.setAction(Intent.ACTION_GET_CONTENT);
//	        startActivityForResult(intent, REQUEST_GALLERY);
//	        break;
//		case R.id.btnList:
//			Log.d("MainActivity","リスト作成");

//			break;
//			}


	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO 自動生成されたメソッド・スタブ

		// 前に選択中の行があれば、背景を透明にする
		if(this.selectedID != -1) {
			parent.getChildAt(this.lastPosition).setBackgroundColor(0);
		}

		// 選択中の行の背景をグレーにする
		view.setBackgroundColor(android.graphics.Color.LTGRAY);
		// カーソルを取得
		SQLiteCursor cursor = (SQLiteCursor)parent.getItemAtPosition(position);
		// カーソルのレコードから、「_id」の値を取得
		this.selectedID = cursor.getInt(cursor.getColumnIndex("page"));
		// 何行目を選択したか記憶
		this.lastPosition = position;

		// ページ番号を紙芝居番号を次の画面に送る
		Intent onICIntent = new Intent(this,SelectActivity2.class);
		onICIntent.putExtra("kamiID",this._kamiID);
		onICIntent.putExtra("page",selectedID);

		int requestCode = REQUEST_SELECT_IMAGE;
		startActivityForResult(onICIntent, requestCode);
//		startActivity(onICIntent);

	}


	private void setDBValueList(ListView lstKami) {

		SQLiteCursor cursor = null;

		// クラスのフィールド変数がNULLなら、データベース空間をオープンン
		if(sdb == null) {
			helper = new MySQLiteOpenHelper(getApplicationContext());
		}
		try{
			sdb = helper.getWritableDatabase();
		} catch(SQLiteException e){
			// 異常終了
			Log.e("ERROR", e.toString());
		}
		// MySQLiteOpenHelperにSELECT文を実行させて結果のカーソルを受け取る
		cursor = this.helper.selectPhoto1(sdb,_kamiID);

		//dblayout: ListViewにさらにレイアウトを指定するもの
//		int db_layout = android.R.layout.simple_list_item_activated_1;
		int db_layout = R.layout.kamilist_item;


		//from: カーソルからListviewに指定するカラムの値を指定するもの
		String[] from = {"page", "pass"};
		//to: Lisetviewの中に指定したdb_layoutに配置する、各行のview部品のid
		int[] to = new int[]{ R.id.text_sample, R.id.icon_sample };

		//ListViewにセットするアダプターを生成
		//カーソルをもとに、fromの列から、toのViewへ値のマッピングがおこなわれる。
		CustomListAdapter  adapter = new CustomListAdapter (this,db_layout,cursor,from,to,0);

/*
		// 画像リストのアダプターを作る
		List<ListItem> list = new ArrayList<ListItem>();

		for (int i = 1; i < 6; i++) {
			ListItem item = new ListItem();
			item.setText("アイテム" + i);
			item.setImageId(R.drawable.ic_launcher);
			list.add(item);
        }

		// adapterのインスタンスを作成
		ImageArrayAdapter imgArrAdapter =
				new ImageArrayAdapter(this, R.layout.list_view_image_item, list);
*/

		// アダプターを指定する
		lstKami.setAdapter(adapter);
//		lstKami.setAdapter(imgArrAdapter);

		/*  リストビューカスタマイズ
		// リソースに準備した画像ファイルからBitmapを作成しておく
	    Bitmap image;
	    image = BitmapFactory.decodeResource(getResources(), R.drawable.tb);

	    // データの作成
	    List<CustomData> objects = new ArrayList<CustomData>();
	    CustomData item1 = new CustomData();
	    item1.setImagaData(image);
	    item1.setTextData({"page"});

	    CustomData item2 = new CustomData();
	    item2.setImagaData(image);
	    item2.setTextData("The second");

	    CustomData item3 = new CustomData();
	    item3.setImagaData(image);
	    item3.setTextData("Il terzo");

	    objects.add(item1);
	    objects.add(item2);
	    objects.add(item3);

	    CustomAdapter customAdapater = new CustomAdapter(this, 0, objects);

	    ListView listView = (ListView)findViewById(R.id.list);
	    listView.setAdapter(customAdapater);
		*/
	}

    /**
     * bitmapを拡大縮小する
     * @param bmpSrc
     * @return
     */
    private Bitmap changeImageSize (Bitmap bmpSrc, float rsz_ratio_w, float rsz_ratio_h ){
        // 画像の大きさを最適化する
    	Bitmap bmpRsz;
    	Matrix matrix = new Matrix();

    	// 拡大比率
//    	float rsz_ratio_w = (float) 0.5;
//    	float rsz_ratio_h = (float) 0.5;
    	// 比率をMatrixに設定
    	matrix.postScale( rsz_ratio_w, rsz_ratio_h );
    	// リサイズ画像
    	bmpRsz = Bitmap.createBitmap(bmpSrc, 0, 0, bmpSrc.getWidth(),bmpSrc.getHeight(), matrix,true);
    	return bmpRsz;
    }


}
