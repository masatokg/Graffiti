package pokazon.jp;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TextToSpeech3  extends Activity implements View.OnClickListener,
	TextToSpeech.OnInitListener {

	SQLiteDatabase sdb = null;
	MySQLiteOpenHelper helper = null;

    // 選択中の紙芝居番号、ページ番号
    private int _kamiID = -1;
    private int _page = -1;
    private String _imagefilePath = null;

	private TextToSpeech mTextToSpeech;
	private EditText mEditText;
	private static int MY_DATA_CHECK_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		// レイアウトを設定する
		 setContentView(R.layout.texttospeech3);

	    Intent intent = getIntent();
//		this._page = intent.getIntExtra("page", -1);
//		this._kamiID = intent.getIntExtra("kamiID", -1);

		Bundle bundle = intent.getExtras();

		// 紙芝居番号、ページ番号、画像パスを取得
		this._kamiID = bundle.getInt("kamiID", -1);
		this._page = bundle.getInt("page", -1);
		this._imagefilePath = bundle.getString("imagefilePath");

		TextView kamishibai = (TextView) findViewById(R.id.tvSpeachKamishibaiName);
		TextView kamishibaiPage = (TextView) findViewById(R.id.tvSpeachKamishibaiPage);

		 // 読み上げるメッセージの入力ボックスを取得する
		 mEditText = (EditText) findViewById(R.id.editText);

		 // ボタン押したら入力されたテキストを読み上げるリスナー
		 Button btnSpeach = (Button) findViewById(R.id.btnSpeach);
		 btnSpeach.setOnClickListener(this);

		 Button btnSpeachEdit = (Button)findViewById(R.id.btnSpeachUpdate);
		 btnSpeachEdit.setOnClickListener(this);


		 Button btnSpeachSave = (Button)findViewById(R.id.btnSpeachBack);
		 btnSpeachSave.setOnClickListener(this);

		 // 該当するページの画像を表示する
		 ImageView pageView = (ImageView)findViewById(R.id.pageViewTextSpeach);

		if(sdb == null){
			helper = new MySQLiteOpenHelper(getApplicationContext());
			sdb = helper.getWritableDatabase();
		}
		try{
			if(this._imagefilePath!=null){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Config.ARGB_4444;
				options.inPurgeable = true;
				options.inSampleSize = 3;
				Bitmap dispImage = BitmapFactory.decodeFile(_imagefilePath, options);
				pageView.setImageBitmap(dispImage);

			}
			else{
				this._imagefilePath = helper.getFilePath(sdb, _kamiID, _page);
				String kaminame = helper.selectKamishibaiNmae(sdb, _kamiID);
				kamishibai.setText("かみしばい： " + kaminame);
			}

			if(this._imagefilePath!=null){

		 		BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Config.ARGB_4444;
				options.inPurgeable = true;
				options.inSampleSize = 2;
		   		Bitmap bmp = BitmapFactory.decodeFile(this._imagefilePath, options);
		   		bmp = this.changeImageSize(bmp, (float)1.2, (float)1.2);
		   		pageView.setImageBitmap(bmp);
			}
			kamishibaiPage.setText(_page + " まいめ");
		}catch(SQLiteException e){

			return;
		}

	}

	@Override
	protected void onDestroy() {
	 super.onDestroy();
	// TextToSpeech を解放する
	 if (mTextToSpeech != null) {
		 mTextToSpeech.shutdown();
	   }
	 }

	@Override
	public void onClick(View v) {

		Intent intent = null;
		// ボタン別処理
		switch(v. getId()){
			// よみあげ
			case R.id.btnSpeach: {
				if (mTextToSpeech == null) {
				 	// 初回はテキスト読み上げ可能かチェックする
				 	Intent checkIntent = new Intent();
				 	checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				 	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			 	}else {
			 		// テキストを読み上げる
			 		speech();
				}
				break;
			}
			// もどる
			case R.id.btnSpeachBack:{

				// 返すデータ(Intent&Bundle)の作成
				intent = new Intent(this, SelectActivity2.class);
				Bundle bundle = new Bundle();

				// 紙芝居番号、ページ番号もセット
				bundle.putInt("kamiID", this._kamiID);
				bundle.putInt("page", this._page);

				intent.putExtras(bundle);
				finish();
//				intent = new Intent(TextToSpeech3.this,KousinActivity.class);
//				startActivity(intent);

				break;
			}
			// かきかえ
			case R.id.btnSpeachUpdate:{
				EditText etv = (EditText)findViewById(R.id.editText);
				String inputMsg = etv.getText().toString();
				Toast.makeText(TextToSpeech3.this,"かきかえました",Toast.LENGTH_SHORT).show();

				if(inputMsg!=null && !inputMsg.isEmpty())
				{
					// 読み上げテキストを保存
					// helper.insertSerifu(sdb,inputMsg);
					helper.UpdatePhoto1Yomi(sdb, _page, _kamiID, inputMsg);
				}
				// etv.setText("");
				break;
			}
		}
	 }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// テキスト読み上げ可能チェックから戻った場合
		 if (requestCode == MY_DATA_CHECK_CODE) {
			 if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				  // 音声リソースが見つかったので TextToSpeech を開始する (-> onInit)
				 mTextToSpeech = new TextToSpeech(this, this);
			 } else {
			// 音声リソースがなければダウンロードする
			 Intent installIntent = new Intent();
			 installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			 startActivity(installIntent);
		 	}
		 }
	 }
	@Override
	public void onInit(int status) {
		 if (status == TextToSpeech.SUCCESS) {
			 // 日本語に対応していれば日本語に設定する （無くても良い）
			 Locale locale = Locale.JAPANESE;
			 if (mTextToSpeech.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
				 mTextToSpeech.setLanguage(locale);
			 } else {
				 Toast.makeText(this, "It does not support the Japanese.", Toast.LENGTH_SHORT).show();
			 }
			 // テキストを読み上げる
			 speech();
		 } else {
			 Toast.makeText(this, "TextToSpeech is not supported.", Toast.LENGTH_SHORT).show();
		 }
	}
	private void speech() {
	 // テキスト読み上げ中であれば停止する
	 if (mTextToSpeech.isSpeaking()) {
		 mTextToSpeech.stop();
	 }
	 // テキストを読み上げる
	 String message = mEditText.getText().toString();
	 mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
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

