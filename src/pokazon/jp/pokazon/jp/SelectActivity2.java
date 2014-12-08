package pokazon.jp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 「カメラまたは画像」を選択する画面
 * @author XXXX
 *
 */
public class SelectActivity2 extends Activity implements View.OnClickListener{

	private static final int REQUEST_CODE = 0;
	private static final int REQUEST_GALLERY = 0;
	private static final int REQUEST_CAPTURE_IMAGE = 100;
	private static final int REQUEST_SELECT_IMAGE = 200;
	private static final int REQUEST_MAKE_STRY = 300;

	// 画像縦横反転時のIntent消失退避処理用キー定数
	private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";

    // 選択中の紙芝居番号、ページ番号
    private int _kamiID = -1;
    private int _page = -1;


	private ImageView _imageView;
	private TextView _text;
	// カメラ撮影または、画像選択からの画像イメージを保持する変数
	private Bitmap dispImage;
	// カメラ画像またはギャラリー選択画像のファイルパスを保持するインスタンス変数
	Uri _imageUri;
	String _imagefilePath = "_imagefilePath初期値";

	//SQLiteデータベース空間を操作するインスタンス変数を宣言
    SQLiteDatabase sdb = null;
    //MySQLiteOpenHelperを操作するインスタンス変数を宣言
    MySQLiteOpenHelper helper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		Log.d("SelectAct2","onCreate");
		setContentView(R.layout.gazou_select);



	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		Intent intent = getIntent();
		this._page = intent.getIntExtra("page", -1);
		this._kamiID = intent.getIntExtra("kamiID", -1);

		// しゃしんをえらぶ
		ImageButton photoSelect = (ImageButton)findViewById(R.id.imageButton1);
		photoSelect.setOnClickListener(this);
		// かめら
		ImageButton camera = (ImageButton)findViewById(R.id.imageButton4);
		camera.setOnClickListener(this);

		// ものがたりをつくる
		ImageButton imageButton3 = (ImageButton)findViewById(R.id.imageButton3);
		imageButton3.setOnClickListener(this);

		// けってい
		ImageButton kettei = (ImageButton)findViewById(R.id.imageButtonKettei);
		kettei.setOnClickListener(this);

		_text = (TextView)findViewById(R.id.pageIdx);
		_imageView = (ImageView)findViewById(R.id.pageView);

		_text.setText(this._page + " まいめ");

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
		String filepath  = this.helper.getFilePath(sdb,_kamiID,_page);

		// uri文字列 から画像を取得するメソッド
		dispImage = BitmapFactory.decodeFile(_imagefilePath);
		_imageView.setImageBitmap(dispImage);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// パスを取るためだけのコード
		if(requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {

			// C# コード基準で打ち込んだコード
			_imageUri = data.getData();
//			_imageView.setImageURI(_imageUri);
			_imagefilePath = GetPathToImage(_imageUri);
//			Toast.makeText(this, _imagefilePath, Toast.LENGTH_SHORT).show();
			Log.v("C#", "path=" + _imagefilePath);

			// uri文字列 から画像を取得するメソッド
			dispImage = BitmapFactory.decodeFile(_imagefilePath);
			_imageView.setImageBitmap(dispImage);

            Log.d("-SelectActivity2:ギャラリー- _imagefilePath = ", _imagefilePath);
			}
		// カメラ撮影からの戻り時
		else if(REQUEST_CAPTURE_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
//			Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
//			dispImage = Bitmap.createScaledBitmap(capturedImage, 670, 480, false);
//			_imageView.setImageBitmap(dispImage);

			// uri文字列 から画像を取得するメソッド
//			_imageView.setImageURI(_imageUri);
//			_imagefilePath = GetPathToImage(_imageUri);
			_imagefilePath = _imageUri.getPath();
            Log.d("-SelectActivity2:Camera- _imagefilePath = ", _imagefilePath);
			dispImage = BitmapFactory.decodeFile(_imagefilePath);

			// 画像の大きさ変更
			dispImage = this.changeImageSize(dispImage);
			_imageView.setImageBitmap(dispImage);
			// カメラ画像のファイルパスを取得（_imageUriを保持しているので使わないかも）
			_imagefilePath = _imageUri.getPath();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		//ギャラリー呼び出し

		Intent intent = new Intent();
		switch(v.getId()) {
		case R.id.imageButton1: // しゃしんをえらぶ
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_GALLERY);
			break;

		case R.id.imageButton2: // おわり
			break;
		case R.id.imageButtonKettei: // 決定ボタン

			// 返すデータ(Intent&Bundle)の作成
			intent = new Intent(this, MainActivity.class);
			Bundle bundle = new Bundle();

			// 画像のフルパスStringもセット
			bundle.putString("imagefilePath", _imagefilePath);

			// 紙芝居番号、ページ番号もセット
			bundle.putInt("kamiID", this._kamiID);
			bundle.putInt("page", this._page);

			intent.putExtras(bundle);
            Log.d("-SelectActivity2:Kettei- _imagefilePath = ", _imagefilePath);

//			intent.putExtra("dispImge", dispImage);
			setResult(RESULT_OK, intent);

			// 呼び出しもとに戻る
			finish();
			break;

		case R.id.imageButton3: // ものがたりをつくる
			// ものがたりをつくる
			Intent intent3 = new Intent(SelectActivity2.this,TextToSpeech3.class);
			Bundle bundle3 = new Bundle();
			// 画像のフルパスStringもセット
			bundle3.putString("imagefilePath", _imagefilePath);

			// 紙芝居番号、ページ番号もセット
			bundle3.putInt("kamiID", this._kamiID);
			bundle3.putInt("page", this._page);

			intent3.putExtras(bundle3);
			startActivityForResult(intent3, REQUEST_MAKE_STRY);
			break;

		case R.id.imageButton4: // カメラ
			// カメラ画像用ファイル準備
//			String filename = "/mnt/sdcard/xxx.jpg";
//			_imageUri = Uri.fromFile(new File(filename));
			this._imageUri = this.makeCameraFileName();
			// カメラ起動
			Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);
			startActivityForResult(intentCamera, REQUEST_CAPTURE_IMAGE);
			break;
		}

	}

	/**
	 * 画像への絶対パスを取得するメソッド
	 * @param uri
	 * @return
	 */
	private String GetPathToImage(Uri uri) {
		String path = null;
		ContentResolver cp = getContentResolver();
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cs = cp.query(uri, projection, null, null, null);
		if(cs != null) {
			int columnIndex = cs.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cs.moveToFirst();
			path = cs.getString(columnIndex);
		}

		return path;

	}

	/**
	 * カメラ保存用のUrlを生成するメソッド
	 * @return
	 */
	private Uri makeCameraFileName(){
		final Date date = new Date(System.currentTimeMillis());
		final SimpleDateFormat dataFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		final String filename = dataFormat.format(date) + ".jpg";
        Log.d("-SelectActivity2:makeCameraFileName- filename = ", filename);
		Uri saveUri =
		        Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()
		                + "/DCIM/Camera", filename));

		return saveUri;
	}

    /**
     * 状態を保持する
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_IMAGE_URI, this._imageUri);
    }

    /**
     * 保持した状態を元に戻す
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this._imageUri = (Uri) savedInstanceState.get(KEY_IMAGE_URI);
        // setImageView();
    }

    /**
     * bitmapを拡大縮小する
     * @param bmpSrc
     * @return
     */
    private Bitmap changeImageSize (Bitmap bmpSrc ){
        // 画像の大きさを最適化する
    	Resources r = getResources();
    	Bitmap bmpRsz;
    	Matrix matrix = new Matrix();

    	// 拡大比率
    	float rsz_ratio_w = (float) 0.5;
    	float rsz_ratio_h = (float) 0.5;
    	// 比率をMatrixに設定
    	matrix.postScale( rsz_ratio_w, rsz_ratio_h );
    	// リサイズ画像
    	bmpRsz = Bitmap.createBitmap(bmpSrc, 0, 0, bmpSrc.getWidth(),bmpSrc.getHeight(), matrix,true);
    	return bmpRsz;
    }

}
