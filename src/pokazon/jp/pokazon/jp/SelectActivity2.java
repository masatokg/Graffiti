package pokazon.jp;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectActivity2 extends Activity implements View.OnClickListener{

	private static final int REQUEST_CODE = 0;
	private static final int REQUEST_GALLERY = 0;
	private static final int REQUEST_CAPTURE_IMAGE = 100;
	private static final int REQUEST_SELECT_IMAGE = 200;

	private ImageView _imageView;
	private TextView _text;
	// カメラ撮影または、画像選択からの画像イメージを保持する変数
	private Bitmap dispImage;

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
		Intent i = getIntent();
		Integer no = i.getIntExtra("kpage", -1);

		// しゃしんをえらぶ
		ImageButton photoSelect = (ImageButton)findViewById(R.id.imageButton1);
		photoSelect.setOnClickListener(this);
		// かめら
		ImageButton camera = (ImageButton)findViewById(R.id.imageButton4);
		camera.setOnClickListener(this);

		// しゃしんをえらぶ
		ImageButton kettei = (ImageButton)findViewById(R.id.imageButtonKettei);
		kettei.setOnClickListener(this);

		_text = (TextView)findViewById(R.id.pageIdx);
		_imageView = (ImageView)findViewById(R.id.imageView1);

		_text.setText(no + " まいめ");

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// パスを取るためだけのコード
		if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			// ContentResolver経由でファイルパスを取得
			ContentResolver cr = getContentResolver();
			String[] columns = {MediaStore.Images.Media.DATA};
			Cursor c = cr.query(data.getData(), columns, null, null, null);
			int column_index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			c.moveToFirst();
			String path = c.getString(column_index);
			Log.v("test", "path=" + path);


			// C# コード基準で打ち込んだコード
			Uri uri = data.getData();
			_imageView.setImageURI(uri);
			String path2 = GetPathToImage(uri);
			Toast.makeText(this, path2, Toast.LENGTH_SHORT).show();
			Log.v("C#", "path=" + path2);

			// uri から画像を取得するメソッド
			dispImage = BitmapFactory.decodeFile(path2);
			_imageView.setImageBitmap(dispImage);

			}
		// カメラ撮影からの戻り時
		if(REQUEST_CAPTURE_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
			Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
			dispImage = Bitmap.createScaledBitmap(capturedImage, 670, 480, false);
			_imageView.setImageBitmap(dispImage);
		}
	/*	super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE) {
			switch( resultCode) {
			case Activity.RESULT_OK:
				String[] projection = {MediaStore.MediaColumns.DATA};
				String selection = null;
				String[] selectionArgs = null;
				String sortOrder = null;
				Cursor cursor = getActivity().getContentResolver().query(data.getData(), projection, selection, selectionArgs, sortOrder);
						if(cursor.getCount() == 1) {
							cursor.moveToNext();
							String filePath = cursor.getString(0);
							Toast.makeText(getActivity(), filePath, Toast.LENGTH_SHORT).show();
						}
				break;
			}
		}
		*/
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
		case R.id.imageButtonKettei:

			// 返すデータ(Intent&Bundle)の作成
			intent = new Intent(this, MainActivity.class);
			Bundle bundle = new Bundle();
//			bundle.putParcelable("dispImge", dispImage);
//			intent.putExtras(b);

			// 画像のbitmapをbyte配列にしてIntentにセットする
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			dispImage.compress(CompressFormat.PNG, 100, baos);
			byte[] bytes = baos.toByteArray();
			bundle = new Bundle();
			bundle.putByteArray("dispImage", bytes );
			intent.putExtras(bundle);

//			intent.putExtra("dispImge", dispImage);
			setResult(RESULT_OK, intent);

			// 呼び出しもとに戻る
			finish();
			break;

		case R.id.imageButton3: // ものがたりをつくる
			break;
		case R.id.imageButton4: // カメラ
			// カメラ起動
			Intent intent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent4, REQUEST_CAPTURE_IMAGE);
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




}
