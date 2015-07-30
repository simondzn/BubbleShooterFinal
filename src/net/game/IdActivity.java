package net.game;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.io.File;

/**
 * Created by Simon on 22/07/2015.
 */
public class IdActivity extends Activity {
    public static String num;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_activity);
    }
    public void onStartButtonClicked(View view){
        EditText editText = (EditText)findViewById(R.id.numSamp);
        num = editText.getText().toString();
        Intent i = new Intent(getApplicationContext(),BubbleShooterActivity.class);
        i.putExtra("num",num);
        File dir = new File("/storage/emulated/0/Download/User-" + num);
        dir.mkdirs();
        startActivity(i);


    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private boolean isStringEmpty(Editable string) {
        if (string != null) {
            if (!string.toString().isEmpty()) {
                return false;
            }
        }
        return true;

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}