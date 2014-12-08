package pokazon.jp;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class suraid extends Activity implements View.OnTouchListener {

    private ViewFlipper viewFlipper;

    // 最大ページ数はとりあえず決め打ち
    private int _pageMax = -1;

	private float firstTouch;
    private boolean isFlip = false;
    // 1枚目と2枚目を交互にフリップしながら、隠れたImageViewに画像を入れ替えてゆく
    private ImageView _firstImageView; //  1枚目ImageView
    private ImageView _secondImageView; //  2枚目ImageView

    // 選択中の紙芝居番号、紙芝居ページ
    private int _kamiID = -1;
    private int _page = 1, _pageIDX=0;
    // 選択中の紙芝居の画像ファイルパスリスト
    List<String> _imagepathList = null;

    // 画像拡大縮小比率幅高さ
    private float _rsz_ratio_w = (float)0.5;
    private float _rsz_ratio_h = (float)0.5;

    //SQLiteデータベース空間を操作するインスタンス変数を宣言
    SQLiteDatabase sdb = null;
    //MySQLiteOpenHelperを操作するインスタンス変数を宣言
    MySQLiteOpenHelper helper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suraid);
    }

    @Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        _firstImageView = (ImageView)findViewById(R.id.imageview_first);
        _secondImageView = (ImageView)findViewById(R.id.imageview_second);
        findViewById(R.id.layout_first).setOnTouchListener(this);
        findViewById(R.id.layout_second).setOnTouchListener(this);

		// 前の画面から紙芝居番号を受け取り、それを元に画像ファイルパスリストを取得
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		_kamiID = bundle.getInt("kamiID");
		this._imagepathList = this.getImagePathList();
		this._pageMax = this._imagepathList.size(); // pageは0始まりなので

		// 1枚目の画像をセット
		if(this._pageMax > 0){
	        String filepath = this._imagepathList.get(this._pageIDX); // indexは0始まり
	        Bitmap bmp = this.getBitmapImage(filepath, this._rsz_ratio_w, this._rsz_ratio_h);
			this._firstImageView.setImageBitmap(bmp);
		}
		else{
			Log.e("error = ", " ImageFilePathList is 0. ");
			Log.e("kamiID = ", String.valueOf(_kamiID));
		}

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getRawX();
        switch(v.getId()) {
        case R.id.layout_first:
        case R.id.layout_second:
            switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstTouch = event.getRawX();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(!isFlip) {
                	// 右へフリップ
                    if( (x - firstTouch > 50) && ( this._page < this._pageMax ) ) {
                        isFlip = true;
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.move_in_left));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.move_out_right));
                        // 次の画像をセット
                        String filepath = this._imagepathList.get(++this._pageIDX);
                        this._page++;
                        Bitmap bmp = this.getBitmapImage(filepath, this._rsz_ratio_w, this._rsz_ratio_h);
                        _secondImageView.setImageBitmap(bmp);
                        viewFlipper.showNext();
                    }
                	// 左へフリップ
                    else if( (firstTouch - x > 50) && ( this._page > 1 )) {
                        isFlip = true;
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.move_in_right));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.move_out_left));
                        // １つ前の画像をセット
                        String filepath = this._imagepathList.get(--this._pageIDX);
                        this._page--;
                        Bitmap bmp = this.getBitmapImage(filepath, this._rsz_ratio_w, this._rsz_ratio_h);
                        _firstImageView.setImageBitmap(bmp);
                        viewFlipper.showPrevious();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFlip = false;
                break;
            }
        }

        return false;
    }

	/**
	 *
	 * @return 紙芝居番号に該当する画像ファイルパスリストを取得
	 */
    private List<String> getImagePathList(){

    	List<String> rtList = null;

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
		rtList = this.helper.getFilePathList(sdb,_kamiID);


    	return rtList;
    }

	/**
	 * 指定されたファイルパスのBitmapを取得
	 * @param filepath
	 * @param rsz_ratio_w
	 * @param rsz_ratio_h
	 * @return 拡大縮小済みのbitmap
	 */
    private Bitmap getBitmapImage(String filepath, float rsz_ratio_w, float rsz_ratio_h){

	 // 選択した画像のファイルパスを取得し、Bitmapを生成
		Bitmap bitmap = null;
		if(filepath != null){
			bitmap = BitmapFactory.decodeFile(filepath);
			bitmap = this.changeImageSize(bitmap, rsz_ratio_w, rsz_ratio_h);
		}

	   	return bitmap;
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