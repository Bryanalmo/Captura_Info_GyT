package android.primer.bryanalvarez.captura_info_gt.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.primer.bryanalvarez.captura_info_gt.Activities.LoginActivity;
import android.primer.bryanalvarez.captura_info_gt.Activities.MainActivity;
import android.primer.bryanalvarez.captura_info_gt.Activities.MenuActivity;
import android.primer.bryanalvarez.captura_info_gt.Activities.VehiculosActivity;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.text.TextUtils;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        Intent intentLogin = new Intent(this, LoginActivity.class);
        Intent intentMain = new Intent(this, MenuActivity.class);

        if(!TextUtils.isEmpty(Util.getuserUserPrefs(prefs)) && !TextUtils.isEmpty(Util.getuserPasswordPrefs(prefs)) ){

            startActivity(intentMain);
        }else{
            startActivity(intentLogin);
        }
        finish();

    }
}
