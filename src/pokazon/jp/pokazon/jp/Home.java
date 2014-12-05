package pokazon.jp;

import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Home extends Activity implements View.OnClickListener {

	SQLiteDatabase sdb = null;
	MySQLiteOpenHelper helper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		//ImageView img = (ImageView) findViewById(R.id.imageView2);
		//アニメーション定義ファイルをロード
		//Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);
		//ImageViewにアニメーションを適用&開始
		//img.startAnimation(anim);


		ImageButton btnMiru = (ImageButton)findViewById(R.id.imgselect);
		btnMiru.setOnClickListener(this);
		ImageButton btnTsukuru = (ImageButton)findViewById(R.id.imageButton2);
		btnTsukuru.setOnClickListener(this);

		ImageView view1 = (ImageView) findViewById(R.id.imgset);
		view1.setBackgroundResource(R.drawable.anim);
		AnimationDrawable anim = (AnimationDrawable)view1.getBackground();
		anim.start();

		ImageView view2 = (ImageView) findViewById(R.id.imageView2);
		view2.setBackgroundResource(R.drawable.move);
		AnimationDrawable move = (AnimationDrawable)view2.getBackground();
		move.start();

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class GifImageView extends ImageView
	{
	    private Movie mMovie;

	    private long mMoviestart;

	    public GifImageView(Context context, InputStream stream) {
	        super(context);

	        mMovie = Movie.decodeStream(stream);
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	        canvas.drawColor(Color.TRANSPARENT);
	        super.onDraw(canvas);
	        final long now = SystemClock.uptimeMillis();

	        if (mMoviestart == 0) {
	            mMoviestart = now;
	        }

	        final int relTime = (int)((now - mMoviestart) % mMovie.duration());
	        mMovie.setTime(relTime);
	        mMovie.draw(canvas, 10, 10);
	        this.invalidate();
	    }
	    // Frameアニメーションを XML から読み込む
	    void frameAnimationFromXMLTest( View v ){

	        // リソースからアニメーションを読み込み、ビューに設定
	        v.setBackgroundResource( R.drawable.yumesibai );

	        // ビューからアニメーションを取り出し
	        AnimationDrawable anim = (AnimationDrawable)v.getBackground();

	        // アニメーション開始
	        anim.start();

	    }
	    void frameAnimationFromXMLTest2( View v ){

	        // リソースからアニメーションを読み込み、ビューに設定
	        v.setBackgroundResource( R.drawable.yumechan_1 );

	        // ビューからアニメーションを取り出し
	        AnimationDrawable move = (AnimationDrawable)v.getBackground();

	        // アニメーション開始
	        move.start();


	}

	}

	//@SuppressWarnings("null")
	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		final Intent intent = new Intent(this,MainActivity.class);
		switch(v.getId()){
		case R.id.imageButton2: //作るボタン
			// カスタムビューを設定
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.dialog, (ViewGroup)findViewById(R.id.layout_root));

			//アラートダイアログを生成
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("かみしばいのなまえをいれてね");
			builder.setView(layout);
			builder.setPositiveButton("つくる", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton){
					// つくる ボタンクリック取得
					EditText name = (EditText)layout.findViewById(R.id.customDlg_title);
					String strname = name.getText().toString();
					Log.d("MainActivity","トースト");
	                //入力した文字をトースト出力する
	                Toast.makeText(Home.this,
	                        strname,
	                        Toast.LENGTH_LONG).show();
	                String mk = helper.makeKamishibai(sdb, strname);
	                Log.d("-Home-",mk);
	                helper.makePhoto(sdb,mk);
	                intent.putExtra("title", mk);
	       			startActivity(intent);
				}
			});
//			builder.setNegativeButton("もどる", new OnClickListener() {
//	            public void onClick(DialogInterface dialog, int which) {
//					// もどる ボタンクリック
//				}
//			})；
			builder.create().show();
			break;
		case R.id.imgselect: // 見るボタン
			Intent intent1 = new Intent(Home.this,suraid.class);
			startActivity(intent1);
			break;
			//テキスト入力を受け付けるビューを作成します。
			/*
		    final EditText editView = new EditText(this);
		    new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_info)
		        .setTitle("かみしばいのなまえ")
		        //setViewにてビューを設定します。
		        .setView(editView)
		        .setPositiveButton("つくる", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	Log.d("MainActivity","トースト");
		                //入力した文字をトースト出力する
		                Toast.makeText(Home.this,
		                        editView.getText().toString(),
		                        Toast.LENGTH_LONG).show();
		                helper.makeKamishibai(sdb, editView.getText().toString());
		                Log.d("-Home-",editView.getText().toString());
		                intent.putExtra("title", editView.getText().toString());
		       			startActivity(intent);
		            }
		        })
		        .setNegativeButton("もどる", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            }
		        })
		        .show();
		    //helper.makeKamishibai(sdb, editView.getText().toString());
            // 紙芝居セレクト
   			//intent.putExtra("title", editView.getText().toString());//紙芝居タイトルを飛ばす
   			//startActivity(intent);
   			 * */


		}
	}
}