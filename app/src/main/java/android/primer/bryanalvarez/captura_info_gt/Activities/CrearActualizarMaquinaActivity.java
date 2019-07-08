package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Accesorio_Maquina_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Componente_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.Models.Funcion;
import android.primer.bryanalvarez.captura_info_gt.Models.Maquina;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class CrearActualizarMaquinaActivity extends AppCompatActivity {

    private ImageView iv_imagen_maquina_act_crear;
    private EditText et_nombre_maquina_act_crear;
    private EditText et_precio_sin_iva_maquina_act_crear;
    private EditText et_ref_maquina_act_crear;
    private EditText et_descripcion_maquina_act_crear;
    private Spinner spinner_funcion_maquina;
    private EditText et_iva_maquina_act_crear;
    private TextView et_valor_final_act_crear;
    private TextView tv_titulo_act_crear;
    private RecyclerView recycler_view_componentes_act_crear;
    private RecyclerView recycler_view_accesorios_act_crear;
    private RecyclerView.LayoutManager mLayoutManager_componentes;
    private RecyclerView.LayoutManager mLayoutManager_accesorios;
    private DividerItemDecoration itemDecoration_componentes;
    private DividerItemDecoration itemDecoration_accesorios;
    private Componente_Adapter componente_adapter;
    private ArrayList<Componente> componentes = new ArrayList<>();
    private ArrayList<Componente> componentes_maquina = new ArrayList<>();
    private Accesorio_Maquina_Adapter accesorio_maquina_adapter;
    private ArrayList<Accesorio_Maquina> accesorios = new ArrayList<>();
    private ArrayList<Accesorio_Maquina> accesorios_maquina = new ArrayList<>();
    private Button btn_act_crear_maquina;
    private AlertDialog alertDialog_cargando;

    private String editar_crear;
    private Maquina maquina;

    private boolean componentesCargados = false;
    private boolean accesoriosCargados = false;
    private boolean imagenActualizada = false;

    private String path;
    private final int VALOR_RETORNO = 21;
    private final int COD_CAMARA = 1;
    private File fileImagen;
    private Bitmap bitmap;
    private final String CARPETA_PRINCIPAL = "GolfyTurf/";
    private final String RUTA_IMAGEN = CARPETA_PRINCIPAL + "Imagenes";

    String funcion = "";

    RequestQueue request;
    StringRequest stringRequest;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actualizar_maquina);

        request = Volley.newRequestQueue(this);

        alertDialog_cargando = new AlertDialog.Builder(CrearActualizarMaquinaActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        editar_crear = getIntent().getExtras().getString("editar_crear");

        bindUI();

        if (editar_crear.equals("editar")){
            traer_componentes_maquina_WebService();
            traer_accesorios_maquina_WebService();
            maquina = Util.getMaquina();
        }else{
            traer_componentes_WebService();
            traer_accesorios_WebService();
            et_precio_sin_iva_maquina_act_crear.setText("0");
            et_iva_maquina_act_crear.setText("0");
        }

        traer_funciones_WebService(spinner_funcion_maquina);

        comprobar_editar_crear();
        et_precio_sin_iva_maquina_act_crear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && Integer.parseInt(s.toString()) != 0) {
                    long precio_nuevo_sin_iva = Integer.parseInt(s.toString()) ;
                    long precio_nuevo_iva = 0;
                    double iva_maquina = 0;
                    if(et_iva_maquina_act_crear.getText().toString().isEmpty()){
                        iva_maquina = 0;
                    }else{
                        iva_maquina = Double.parseDouble(et_iva_maquina_act_crear.getText().toString());
                    }
                    precio_nuevo_iva = (long) (precio_nuevo_sin_iva + (precio_nuevo_sin_iva*iva_maquina));
                    String format_valor_nuevo= NumberFormat.getInstance().format(precio_nuevo_iva);
                    et_valor_final_act_crear.setText("$ "+format_valor_nuevo);
                }else{
                    if (editar_crear.equals("editar")){
                        String format_valor_nuevo= NumberFormat.getInstance().format(maquina.getPrecio());
                        et_valor_final_act_crear.setText("$ "+format_valor_nuevo);
                    }else{
                        String format_valor_nuevo= NumberFormat.getInstance().format(0);
                        et_valor_final_act_crear.setText("$ "+format_valor_nuevo);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_iva_maquina_act_crear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && Double.parseDouble(s.toString()) != 0) {
                    long precio_nuevo_sin_iva =0;
                    if(et_precio_sin_iva_maquina_act_crear.getText().toString().isEmpty()){
                        precio_nuevo_sin_iva = 0;
                    }else{
                        precio_nuevo_sin_iva = Integer.parseInt(et_precio_sin_iva_maquina_act_crear.getText().toString());
                    }
                    long precio_nuevo_iva = 0;
                    double iva_maquina = Double.parseDouble(s.toString()) ;
                    precio_nuevo_iva = (long) (precio_nuevo_sin_iva + (precio_nuevo_sin_iva*iva_maquina));
                    String format_valor_nuevo= NumberFormat.getInstance().format(precio_nuevo_iva);
                    et_valor_final_act_crear.setText("$ "+format_valor_nuevo);
                }else{
                    if (editar_crear.equals("editar")){
                        String format_valor_nuevo= NumberFormat.getInstance().format(maquina.getPrecio());
                        et_valor_final_act_crear.setText("$ "+format_valor_nuevo);
                    }else{
                        long precio_nuevo_sin_iva =0;
                        if(et_precio_sin_iva_maquina_act_crear.getText().toString().isEmpty()){
                            precio_nuevo_sin_iva = 0;
                        }else{
                            precio_nuevo_sin_iva = Integer.parseInt(et_precio_sin_iva_maquina_act_crear.getText().toString());
                        }
                        String format_valor_nuevo= NumberFormat.getInstance().format(precio_nuevo_sin_iva);
                        et_valor_final_act_crear.setText("$ "+format_valor_nuevo);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_imagen_maquina_act_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }

        activarBoton();

        spinner_funcion_maquina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Funcion funcionSelected = (Funcion) spinner_funcion_maquina.getSelectedItem();
                funcion = funcionSelected.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            activarBoton();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }

    private void activarBoton(){
        btn_act_crear_maquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_cargando = new AlertDialog.Builder(CrearActualizarMaquinaActivity.this).create();
                alertDialog_cargando.setMessage("Cargando...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
                if (editar_crear.equals("crear")){
                    if (imagenActualizada){
                        actualizar_maquina_WebService();
                    }else{
                        Toast.makeText(CrearActualizarMaquinaActivity.this,"Debe selccionar una imagen",Toast.LENGTH_SHORT).show();
                        alertDialog_cargando.dismiss();
                    }
                }else{
                    actualizar_maquina_WebService();
                }
                /*new MaterialFilePicker()
                        .withActivity(CrearActualizarMaquinaActivity.this)
                        .withRequestCode(10)
                        .start();*/

            }
        });

    }



    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    private void comprobar_editar_crear() {
        if (editar_crear.equals("editar")){
            Picasso.with(this).load(maquina.getImagen_equipo()).into(iv_imagen_maquina_act_crear);
            et_nombre_maquina_act_crear.setText(maquina.getModelo_maquina());
            et_ref_maquina_act_crear.setText(maquina.getReferencia());
            et_descripcion_maquina_act_crear.setText(maquina.getDescripcion());
            et_precio_sin_iva_maquina_act_crear.setText(maquina.getPrecio()+"");
            et_iva_maquina_act_crear.setText(maquina.getIVA()+"");
            String format_valor= NumberFormat.getInstance().format(maquina.getPrecio_IVA());
            et_valor_final_act_crear.setText("$ "+format_valor);
            tv_titulo_act_crear.setText("Actualizar Máquina");
            btn_act_crear_maquina.setText("Actualizar");
        }
    }

    private void bindUI() {
        iv_imagen_maquina_act_crear = (ImageView) findViewById(R.id.iv_imagen_vehiculo_act_crear);
        et_nombre_maquina_act_crear = (EditText) findViewById(R.id.et_nombre_vehiculo_act_crear);
        et_ref_maquina_act_crear = (EditText) findViewById(R.id.et_ref_maquina_act_crear);
        et_descripcion_maquina_act_crear = (EditText) findViewById(R.id.et_descripcion_maquina_act_crear);
        spinner_funcion_maquina = (Spinner) findViewById(R.id.spinner_funcion_maquina);
        et_precio_sin_iva_maquina_act_crear = (EditText) findViewById(R.id.et_precio_sin_iva_vehiculo_act_crear);
        et_iva_maquina_act_crear = (EditText) findViewById(R.id.et_iva_maquina_act_crear);
        et_valor_final_act_crear = (TextView) findViewById(R.id.et_valor_final_act_crear);
        tv_titulo_act_crear = (TextView) findViewById(R.id.tv_titulo_act_crear_vehiculo);
        recycler_view_componentes_act_crear = (RecyclerView) findViewById(R.id.recycler_view_accesorios_vehiculo_act_crear);
        recycler_view_accesorios_act_crear = (RecyclerView) findViewById(R.id.recycler_view_accesorios_act_crear);
        btn_act_crear_maquina = (Button) findViewById(R.id.btn_act_crear_maquina);
        recycler_view_componentes_act_crear.setHasFixedSize(true);
        recycler_view_componentes_act_crear.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager_componentes = new LinearLayoutManager(this);
        itemDecoration_componentes = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_view_componentes_act_crear.setLayoutManager(mLayoutManager_componentes);
        recycler_view_componentes_act_crear.addItemDecoration(itemDecoration_componentes);
        recycler_view_accesorios_act_crear.setHasFixedSize(true);
        recycler_view_accesorios_act_crear.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager_accesorios = new LinearLayoutManager(this);
        itemDecoration_accesorios = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_view_accesorios_act_crear.setLayoutManager(mLayoutManager_accesorios);
        recycler_view_accesorios_act_crear.addItemDecoration(itemDecoration_accesorios);
    }

    private void hideAlertDialog() {
        if (componentesCargados && accesoriosCargados){
            alertDialog_cargando.dismiss();
        }

    }

    private void traer_funciones_WebService(final Spinner spinner){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_funciones.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Funcion funcion = null;
                ArrayList<Funcion> funciones = new ArrayList<>();
                String id;
                String desc_funcion;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        desc_funcion = jsonArray.getJSONObject(i).getString("Funcion");

                        funcion = new Funcion(id,desc_funcion);
                        funciones.add(funcion);

                    }
                    spinner.setAdapter(new ArrayAdapter<Funcion>(CrearActualizarMaquinaActivity.this, android.R.layout.simple_list_item_1, funciones));
                    if (editar_crear.equals("editar")){
                        for (int i = 0; i < funciones.size(); i++) {
                            if (funciones.get(i).getId().equals(maquina.getFuncion())){
                                spinner.setSelection(i);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(stringRequest);
    }

    private void traer_componentes_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_componentes.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Componente componente= null;
                String Id;
                String Nombre;
                String Modelo_maquina;
                long Precio;
                double IVA;
                long valor_IVA;
                long precio_IVA;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Nombre = jsonArray.getJSONObject(i).getString("Nombre");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        valor_IVA = (long) (Precio*IVA);
                        precio_IVA = Precio + valor_IVA;
                        componente = new Componente(Id,Nombre,Precio,IVA,valor_IVA,precio_IVA);
                        componente.setAgregado(false);
                        componentes.add(componente);

                    }

                    componente_adapter = new Componente_Adapter(CrearActualizarMaquinaActivity.this, componentes, R.layout.list_view_componente, new Componente_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Componente componente, int position) {

                        }
                    }, new Componente_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Componente componente, int position,boolean isChecked) {
                            if(isChecked){
                                componente.setAgregado(true);
                            }else{
                                componente.setAgregado(false);
                                componentes_maquina.remove(componentes.get(position));
                            }
                            comprobarComponentesAgregados();
                        }
                    });
                    recycler_view_componentes_act_crear.setAdapter(componente_adapter);
                    //alertDialog_cargando.dismiss();
                    componentesCargados = true;
                    hideAlertDialog();
                    componentes = comprobarComponentesMaquina(componentes);
                    componente_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Id_maquina", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void comprobarComponentesAgregados() {
        componentes_maquina = new ArrayList<>();
        for (int i = 0; i < componentes.size() ; i++) {
            if(componentes.get(i).isAgregado()){
                componentes_maquina.add(componentes.get(i));
            }
        }
        Toast.makeText(CrearActualizarMaquinaActivity.this,"Agregados "+componentes_maquina.size(),Toast.LENGTH_SHORT).show();
    }

    private void traer_accesorios_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_accesorios_maquinaria.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Accesorio_Maquina accesorio_maquina= null;
                String Id;
                String Nombre;
                long Precio;
                double IVA;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Nombre = jsonArray.getJSONObject(i).getString("Descripcion");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");

                        accesorio_maquina= new Accesorio_Maquina(Id,Nombre,Precio,IVA);
                        accesorio_maquina.setAgregado(false);
                        accesorios.add(accesorio_maquina);

                    }

                    accesorio_maquina_adapter = new Accesorio_Maquina_Adapter(CrearActualizarMaquinaActivity.this, accesorios, R.layout.list_view_item_accesorio_maquina, new Accesorio_Maquina_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Accesorio_Maquina accesorio_maquina, int position) {

                        }
                    }, new Accesorio_Maquina_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Accesorio_Maquina accesorio_maquina, int position, boolean isChecked) {
                            if(isChecked){
                                accesorio_maquina.setAgregado(true);
                            }else{
                                accesorio_maquina.setAgregado(false);
                                accesorios_maquina.remove(accesorios.get(position));
                            }
                            comprobarAccesoriosAgregados();
                        }
                    });
                    recycler_view_accesorios_act_crear.setAdapter(accesorio_maquina_adapter);
                    //alertDialog_cargando.dismiss();
                    accesoriosCargados = true;
                    hideAlertDialog();
                    accesorios = comprobarAccesoriosMaquina(accesorios);
                    accesorio_maquina_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Id_maquina", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void comprobarAccesoriosAgregados() {
        accesorios_maquina = new ArrayList<>();
        for (int i = 0; i < accesorios.size() ; i++) {
            if(accesorios.get(i).isAgregado()){
                accesorios_maquina.add(accesorios.get(i));
            }
        }
        Toast.makeText(CrearActualizarMaquinaActivity.this,"Agregados "+accesorios_maquina.size(),Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Accesorio_Maquina> comprobarAccesoriosMaquina(ArrayList<Accesorio_Maquina> accesorios) {
        for (int i = 0; i < accesorios.size() ; i++) {
            Accesorio_Maquina accesorio= accesorios.get(i);
            for (int j = 0; j <accesorios_maquina.size() ; j++) {
                Accesorio_Maquina accesorio_maquina= accesorios_maquina.get(j);
                if (accesorio.getId().equals(accesorio_maquina.getId())){
                    accesorios.get(i).setAgregado(true);
                }
            }
        }
        return accesorios;
    }

    private ArrayList<Componente> comprobarComponentesMaquina(ArrayList<Componente> componentes) {
        for (int i = 0; i < componentes.size() ; i++) {
            Componente componente = componentes.get(i);
            for (int j = 0; j <componentes_maquina.size() ; j++) {
                Componente componente_maquina = componentes_maquina.get(j);
                if (componente.getId().equals(componente_maquina.getId())){
                    componentes.get(i).setAgregado(true);
                }
            }
        }
        return componentes;
    }

    private void traer_componentes_maquina_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_componentes.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                traer_componentes_WebService();
                JSONArray jsonArray = null;
                Componente componente= null;
                String Id;
                String Nombre;
                long Precio;
                double IVA;
                long valor_IVA;
                long precio_IVA;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Nombre = jsonArray.getJSONObject(i).getString("Nombre");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        valor_IVA = (long) (Precio*IVA);
                        precio_IVA = Precio + valor_IVA;
                        componente = new Componente(Id,Nombre,Precio,IVA,valor_IVA,precio_IVA);
                        componente.setAgregado(false);
                        componentes_maquina.add(componente);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Id_maquina", maquina.getId());
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void traer_accesorios_maquina_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_accesorios_maquinaria.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                traer_accesorios_WebService();
                JSONArray jsonArray = null;
                Accesorio_Maquina accesorio_maquina= null;
                String Id;
                String Nombre;
                long Precio;
                double IVA;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Nombre = jsonArray.getJSONObject(i).getString("Descripcion");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");

                        accesorio_maquina= new Accesorio_Maquina(Id,Nombre,Precio,IVA);
                        accesorio_maquina.setAgregado(false);
                        accesorios_maquina.add(accesorio_maquina);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Id_maquina", maquina.getId());
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void cargarImagen() {
        final CharSequence[] opciones = {"Tomar foto", "Cargar imagen", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(this);
        alertOpciones.setTitle("Seleccione una opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(opciones[i].equals("Tomar foto")){
                    abrirCamara();
                }else if(opciones[i].equals("Cargar imagen")){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
                }else{
                    dialog.dismiss();
                }
            }
        });

        alertOpciones.show();
    }

    private void abrirCamara() {
        File myFile = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreated = myFile.exists();
        String nombre = "";
        if(isCreated==false){
            isCreated = myFile.mkdirs();
        }
        if(isCreated == true){
            Long consecutivo = System.currentTimeMillis()/1000;
            nombre = consecutivo.toString()+".jpg";
        }
        path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN
                + File.separator + nombre;
        File imagen = new File(path);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(this,
                android.primer.bryanalvarez.captura_info_gt.BuildConfig.APPLICATION_ID + ".provider",
                imagen);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, COD_CAMARA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case VALOR_RETORNO:
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        // iv_imagen_maquina_act_crear.setImageBitmap(bitmap);
                        Picasso.with(this).load(data.getData()).fit().into(iv_imagen_maquina_act_crear);
                        imagenActualizada = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case COD_CAMARA:

                    Uri imageUri = Uri.parse(path);
                    File file = new File(imageUri.getPath());
                    bitmap = BitmapFactory.decodeFile(path);
                    Picasso.with(this).load(file).fit().into(iv_imagen_maquina_act_crear);
                    imagenActualizada = true;
                    //iv_imagen_maquina_act_crear.setImageBitmap(bitmap);
                    MediaScannerConnection.scanFile(this,
                            new String[]{imageUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });
                    break;
            }
            bitmap = redimensionarImagen(bitmap, 600,800);
        }


        if(requestCode == 10 && resultCode == RESULT_OK){

            progress = new ProgressDialog(CrearActualizarMaquinaActivity.this);
            progress.setTitle("Subiendo archivos");
            progress.setMessage("Por favor espere...");
            progress.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    File f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    String content_type  = getMimeType(f.getPath());

                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .build();

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://golfyturf.com/feria_automovil/AppWebServices/recibir_archivos.php")
                            .post(request_body)
                            .build();

                    try {
                        okhttp3.Response response = client.newCall(request).execute();

                        if(!response.isSuccessful()){
                            throw new IOException("Error : "+response);
                        }

                        progress.dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });

            t.start();




        }

    }



    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {
        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if(ancho > anchoNuevo || alto > altoNuevo){
            float escalaAncho = anchoNuevo/ancho;
            float escalaAlto = altoNuevo/alto;
            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);

            return Bitmap.createBitmap(bitmap, 0,0, ancho,alto,matrix,false);
        }else{

            return bitmap;
        }
    }

    private String convertirImagenString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imageByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imageByte,Base64.DEFAULT);

        return imagenString;
    }

    private void actualizar_maquina_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/editar_crear_maquina.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                Toast.makeText(CrearActualizarMaquinaActivity.this, response, Toast.LENGTH_LONG).show();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(CrearActualizarMaquinaActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CrearActualizarMaquinaActivity.this,MaquinasActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(CrearActualizarMaquinaActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(CrearActualizarMaquinaActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CrearActualizarMaquinaActivity.this,MaquinasActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();

                String Nombre_maquina = et_nombre_maquina_act_crear.getText().toString();
                String Referencia = et_ref_maquina_act_crear.getText().toString();
                String Descripcion_maquina = et_descripcion_maquina_act_crear.getText().toString();
                String Precio = et_precio_sin_iva_maquina_act_crear.getText().toString();
                String IVA = et_iva_maquina_act_crear.getText().toString();
                String Imagen_maquina ="";
                if(imagenActualizada){
                    Imagen_maquina = convertirImagenString(bitmap);
                }else{
                    Imagen_maquina = "";
                }
                parametros.put("Editar_crear", editar_crear );
                parametros.put("Referencia", Referencia );
                if (editar_crear.equals("crear")){
                    parametros.put("Id_maquina", "" );
                    parametros.put("Referencia_antigua", "" );
                }else{
                    parametros.put("Id_maquina", maquina.getId() );
                    parametros.put("Referencia_antigua", maquina.getReferencia() );
                }
                parametros.put("Nombre_maquina", Nombre_maquina);
                parametros.put("Descripcion", Descripcion_maquina);
                parametros.put("Precio", Precio );
                parametros.put("IVA", IVA );
                parametros.put("Imagen_maquina", Imagen_maquina );
                parametros.put("Numero_componentes", componentes_maquina.size()+"" );
                parametros.put("Numero_accesorios", accesorios_maquina.size() + "" );
                parametros.put("Funcion", funcion );
                for (int i=0; i<componentes_maquina.size(); i++){
                    Componente componente = componentes_maquina.get(i);
                    parametros.put("Id_componente"+i, componente.getId() );
                }
                for (int i=0; i<accesorios_maquina.size(); i++){
                    Accesorio_Maquina accesorio_maquina = accesorios_maquina.get(i);
                    parametros.put("Id_accesorio"+i, accesorio_maquina.getId() );
                }

                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);
    }
}
