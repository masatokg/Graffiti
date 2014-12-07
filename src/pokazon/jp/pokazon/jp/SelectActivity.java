package pokazon.jp;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;


/**
 * 注意：未使用
 * @author Hokazono,Tada
 *
 */

public class SelectActivity extends Activity implements View.OnClickListener{

	private Bitmap bitmap;
	private Canvas canvas;
	private int viewWidth, viewHeight;
	static final int REQUEST_CAPTURE_IMAGE = 100;
	static final int REQUEST_IMAGE_SELECT = 200;

	ImageButton imageButton1;
	ImageButton imageButton2;
	ImageButton imageButton3;
	ImageButton imageButton4;
	ImageView imageView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("SelectActivity","onCreate");

		// ウィンドウマネージャのインスタンス取得
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		// ディスプレイのインスタンスを生成
		Display display = manager.getDefaultDisplay();
		Point point = new Point();
		// 画像サイズの取得
		display.getSize(point);
		viewWidth = point.x;
		viewHeight = point.y;
	}

	// 起動先アクティビティからデータを返してもらう
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 画像を選択した時のみ実行
		if (REQUEST_IMAGE_SELECT == requestCode && data!=null) {
			Uri uri = data.getData();
			try {
				bitmap = loadImage(uri, viewWidth, viewHeight);
			} catch (Exception e) {
				e.printStackTrace();
			}
			imageView1.setImageBitmap(bitmap);
		}
		// カメラ撮影からの戻り時
		if(REQUEST_CAPTURE_IMAGE == requestCode
				&& resultCode == Activity.RESULT_OK ){
				Bitmap capturedImage =
					(Bitmap) data.getExtras().get("data");
				Bitmap sacaledImage =
				Bitmap.createScaledBitmap( capturedImage, 670,480, false);
				imageView1.setImageBitmap(sacaledImage);
		}
	}

	// 取得したURIを用いて画像を読み込む
	private Bitmap loadImage(Uri uri, int viewWidth, int viewHeight){
	    // Uriから画像を読み込みBitmapを作成
	    Bitmap originalBitmap = null;
	    try {
	        originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // MediaStoreから回転情報を取得
	    final int orientation;
	    Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), uri, new String[] {
	        MediaStore.Images.ImageColumns.ORIENTATION
	    });
	    if (cursor != null) {
	        cursor.moveToFirst();
	        orientation = cursor.getInt(0);
	    } else {
	        orientation = 0;
	    }

	    int originalWidth = originalBitmap.getWidth();
	    int originalHeight = originalBitmap.getHeight();

	    // 縮小割合を計算
	    final float scale;
	    if (orientation == 90 || orientation == 270) {
	    	// 縦向きの画像は半分のサイズに変更
	        scale = Math.min(((float)viewWidth / originalHeight)/2, ((float)viewHeight / originalWidth)/2);
	    } else {
	    	// 横向きの画像
	        scale = Math.min((float)viewWidth / originalWidth, (float)viewHeight / originalHeight);
	        String st = String.valueOf(scale);
	        //Log.d("AAA",st);
	    }

	    // 変換行列の作成
	    final Matrix matrix = new Matrix();
	    if (orientation != 0) {
	    	//画像を回転させる
	        matrix.postRotate(orientation);
	    }
	    if (scale < 1.0f) {
	    	// Bitmapを拡大縮小する
	        matrix.postScale(scale, scale);
	    }

	    // 行列によって変換されたBitmapを返す
	    return Bitmap.createBitmap(originalBitmap, 0, 0, originalWidth, originalHeight, matrix,
	            true);
	}


	protected void findViews(){
		imageButton1 = (ImageButton)findViewById(R.id.imageButton1);
		imageButton2 = (ImageButton)findViewById(R.id.imageButton2);
		imageButton3 = (ImageButton)findViewById(R.id.imageButton3);
		imageButton4 = (ImageButton)findViewById(R.id.imageButton4);
		imageView1 = (ImageView)findViewById(R.id.pageView);

	}

	protected void setListeners(){
		imageButton1.setOnClickListener(this);
		imageButton2.setOnClickListener(this);
		imageButton3.setOnClickListener(this);
		imageButton4.setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		findViews();
		setListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
			switch(v.getId()){
				case R.id.imageButton1:
					// 画像選択へ移動
					// 画像が保存されてるフォルダにアクセス
					Intent intent1 = new Intent();
					intent1.setType("image/*");
					intent1.setAction(Intent.ACTION_PICK);
					// 起動先アクティビティからデータを返してもらいたい場合
					startActivityForResult(intent1, REQUEST_IMAGE_SELECT);
				break;
				case R.id.imageButton2:
					// 完了画面へ移動
					//Intent intent2 = new Intent(this,Kanryou1Activity.class);
					//startActivity(intent2);
				break;
				case R.id.imageButton3:
					// ものがたりをつくる

				break;
				case R.id.imageButton4:
					// カメラ起動
					Intent intent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent4, REQUEST_CAPTURE_IMAGE);
				break;
				default:

				break;
			}

	}
}
