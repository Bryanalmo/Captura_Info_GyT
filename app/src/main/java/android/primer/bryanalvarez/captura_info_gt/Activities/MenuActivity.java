package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.primer.bryanalvarez.captura_info_gt.Models.Funcion;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MenuActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private LinearLayout ly_carros_golf;
    private LinearLayout ly_maquinaria;
    private LinearLayout ly_actualizacion;
    private LinearLayout ly_repuestos;
    private LinearLayout ly_arrendamiento;

    RequestQueue request;
    StringRequest stringRequest;

    private final int PERMISSION_READ_EXTERNAL_MEMORY = 1;
    private final int PERMISSION_WRITE_EXTERNAL_MEMORY = 2;
    private final int PERMISSION_CAMERA= 3;
    private AlertDialog alertDialog_cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Util.cotizaciones_maquinas.clear();

        checkForPermission();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        request = Volley.newRequestQueue(this);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Util.setId_usuario(Util.getuserIdPrefs(prefs));
        Util.setId_cargo(Util.getuserCargoPrefs(prefs));

        //Toast.makeText(this, Util.getId_usuario(), Toast.LENGTH_LONG).show();

        ly_carros_golf = (LinearLayout) findViewById(R.id.ly_carros_golf);
        ly_maquinaria = (LinearLayout) findViewById(R.id.ly_maquinaria);
        ly_actualizacion = (LinearLayout) findViewById(R.id.ly_actualizacion);
        ly_repuestos= (LinearLayout) findViewById(R.id.ly_repuestos);
        ly_arrendamiento= (LinearLayout) findViewById(R.id.ly_arrendamiento);

        ly_carros_golf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTRM_WebService("vehiculos");
                alertDialog_cargando = new AlertDialog.Builder(MenuActivity.this).create();
                alertDialog_cargando.setMessage("Recibiendo datos...");
                alertDialog_cargando.show();
            }
        });

        ly_maquinaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.getId_cargo().equals("12")){
                    Toast.makeText(MenuActivity.this, "No tiene permisos para acceder a esta funcion", Toast.LENGTH_SHORT).show();
                }else{
                    if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.CAMERA)){
                        getTRM_WebService("maquinaria");
                        alertDialog_cargando = new AlertDialog.Builder(MenuActivity.this).create();
                        alertDialog_cargando.setMessage("Recibiendo datos...");
                        alertDialog_cargando.show();
                    }
                }
            }
        });

        ly_actualizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Intent intent = new Intent(MenuActivity.this, ActualizarActivity.class);
                    startActivity(intent);
                }

            }
        });
        ly_repuestos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MenuActivity.this, "Esta opción estará disponible pronto", Toast.LENGTH_SHORT).show();
                if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Intent intent = new Intent(MenuActivity.this, CotizarRepuestosActivity.class);
                    intent.putExtra("editar_crear", "crear" );
                    startActivity(intent);
                }

            }
        });
        ly_arrendamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ArrendamientoActivity.class);
                startActivity(intent);
                //getTRM_WebService("arrendamiento");
            }
        });
    }

    private void checkForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,CAMERA},100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length==3 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this,"Permisos",Toast.LENGTH_SHORT).show();
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,CAMERA},100);
                }
            }
        }
    }

    private void getTRM_WebService(final String source) {

        String url = "https://silo.golfyturf.com/php/consultar_trm.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            response = response.replace("\"", "");
                            //response = response.replace(".", ",");
                            response = response.replace(" ", "");
                            Util.TRM = Double.parseDouble(response);
                            alertDialog_cargando.hide();
                            showSeleccionarMoneda(source);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MenuActivity.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        request.add(stringRequest);

    }

    private void showSeleccionarMoneda(final String source) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Seleccione la moneda con la que desea cotizar");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_seleccionar_moneda, null);
        builder.setView(viewInflated);

        final RadioButton radioButton_USD  = (RadioButton) viewInflated.findViewById(R.id.radioButton_USD);
        final RadioButton radioButton_COP  = (RadioButton) viewInflated.findViewById(R.id.radioButton_COP);
        radioButton_USD.setChecked(true);
        final TextView tv_TRM  = (TextView) viewInflated.findViewById(R.id.tv_TRM);
        tv_TRM.setText("$ "+Util.TRM);
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(radioButton_COP.isChecked()){
                    Util.monedaActual = Util.TRM;
                }else if (radioButton_USD.isChecked()){
                    Util.monedaActual = 1;
                }
                Intent intent;
                if(source.equals("maquinaria")){
                    intent = new Intent(MenuActivity.this, MaquinasActivity.class);
                }else if(source.equals("vehiculos")){
                    intent = new Intent(MenuActivity.this, VehiculosActivity.class);
                }else {
                    intent = new Intent(MenuActivity.this, ArrendamientoActivity.class);
                }
                startActivity(intent);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean hasPermission(String permissionToCheck) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permissionToCheck);
        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_logout:
                removeSharedPreferences();
                logOut();
                return true;
            case R.id.menu_forget_logout:
                removeSharedPreferences();
                logOut();
                return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    private void logOut(){
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void removeSharedPreferences(){
        Util.deleteUserandPass(prefs);
    }
}
