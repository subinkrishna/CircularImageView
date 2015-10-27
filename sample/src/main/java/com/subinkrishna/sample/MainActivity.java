package com.subinkrishna.sample;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.subinkrishna.widget.CircularImageView;

public class MainActivity extends AppCompatActivity {

    /** Log tag */
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset to default theme once the Activity is "cold started"
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        Typeface mono = Typeface.createFromAsset(getAssets(), "fonts/roboto/Mono-Regular.ttf");
        title.setTypeface(mono);
        */

        setToolbar();
        setImageContainerBackground();

        findViewById(R.id.civ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CircularImageView)v).toggle();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setImageContainerBackground() {
        Resources res = getResources();
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.grid);
        int cellH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, res.getDisplayMetrics());
        b = Bitmap.createScaledBitmap(b, cellH, cellH, true);

        BitmapDrawable bd = new BitmapDrawable(res, b);
        bd.setTileModeX(Shader.TileMode.REPEAT);
        bd.setTileModeY(Shader.TileMode.REPEAT);
        findViewById(R.id.image_container).setBackgroundDrawable(bd);
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
        }
    }

}
