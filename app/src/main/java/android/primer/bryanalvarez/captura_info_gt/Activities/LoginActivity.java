package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private EditText editTextUsuario;
    private EditText editTextPassword;
    private Switch switchRemember;
    private Button buttonLogin;
    private AlertDialog alertDialog;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Enviando datos...");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        bindUI();

        setCredentialsifExist();
        request = Volley.newRequestQueue(this);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()){
                    alertDialog.show();
                    cargarWebService();
                }else{
                    Toast.makeText(LoginActivity.this, "Debe llenar todos los campos", LENGTH_LONG).show();
                }
            }
        });
    }

    private void cargarWebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/getAutenticacionEmpleado.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("validacion").equals("true")){
                        alertDialog.dismiss();
                        Util.setId_usuario(jsonObject.getString("id").toString());
                        Util.setId_cargo(jsonObject.getString("cargo").toString());
                        goToMain();
                    }else{
                        alertDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this,"Acceso a internet muy debil. Revise su conexión", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String usuario = editTextUsuario.getText().toString().replace(" ","");
                String password = editTextPassword.getText().toString();

                Map<String,String> parametros = new HashMap<>();
                parametros.put("usuario",usuario);
                parametros.put("password",password);

                return parametros;
            }
        };
        request.add(stringRequest);
    }

    private void safeOnPreferences(String user, String password, String Id, String cargo){

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("User",user);
            editor.putString("Pass",password);
            editor.putString("Id",Id);
            editor.putString("Cargo",cargo);
            editor.commit();
            editor.apply();


    }

    private void bindUI(){

        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextPassword= (EditText) findViewById(R.id.editTextPassword);
        switchRemember = (Switch) findViewById(R.id.switchRemember);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
    }

    private void goToMain(){
        if(isOnline(this)){
            Intent intent = new Intent(this, MenuActivity.class);
            safeOnPreferences(editTextUsuario.getText().toString(),editTextPassword.getText().toString(), Util.getId_usuario(),Util.getId_cargo());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            Toast.makeText(this,"No tiene conección a internet",LENGTH_LONG).show();
        }

    }

    private void setCredentialsifExist(){
        String user = Util.getuserUserPrefs(prefs);
        String password = Util.getuserPasswordPrefs(prefs);
        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){
            editTextUsuario.setText(user);
            editTextPassword.setText(password);
            switchRemember.setChecked(true);
        }

    }

    private boolean check(){
        if(editTextPassword.getText().toString().equals("") || editTextUsuario.getText().toString().equals("") ){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}
