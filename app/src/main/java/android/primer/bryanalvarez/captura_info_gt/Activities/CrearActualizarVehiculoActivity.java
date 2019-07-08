package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Accesorio_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Accesorio_Vehiculo_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.Marca;
import android.primer.bryanalvarez.captura_info_gt.Models.Tipo_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.provider.MediaStore;
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

public class CrearActualizarVehiculoActivity extends AppCompatActivity {

    private ImageView iv_imagen_vehiculo_act_crear;
    private EditText et_nombre_vehiculo_act_crear;
    private EditText et_precio_sin_iva_vehiculo_act_crear;
    private EditText et_iva_vehiculo_act_crear;
    private EditText et_motor_vehiculo_act_crear;
    private EditText et_chasis_vehiculo_act_crear;
    private EditText et_frenos_vehiculo_act_crear;
    private EditText et_velocidad_vehiculo_act_crear;
    private EditText et_capacidad_vehiculo_act_crear;
    private EditText et_capacidad_carga_vehiculo_act_crear;
    private EditText et_peso_vehiculo_act_crear;
    private EditText et_ancho_carga_vehiculo_act_crear;
    private EditText et_largo_vehiculo_act_crear;
    private EditText et_incluye_vehiculo_act_crear;
    private EditText et_distancia_suelo_vehiculo_act_crear2;
    private EditText et_colores_vehiculo_act_crear;
    private Spinner spinner_marca_vehiculo;
    private Spinner spinner_tipo_vehiculo;
    private TextView tv_valor_final_act_crear_vehiculo;
    private TextView tv_titulo_act_crear_vehiculo;
    private Button btn_act_crear_vehiculo;
    private RecyclerView recycler_view_accesorios_vehiculo_act_crear;
    private Accesorio_Vehiculo_Adapter adapter_accesorio;
    private RecyclerView.LayoutManager mLayoutManager_componentes;
    private DividerItemDecoration itemDecoration_componentes;
    private ArrayList<Accesorio> accesorios = new ArrayList<>();
    private ArrayList<Accesorio> accesorios_vehiculo = new ArrayList<>();
    private AlertDialog alertDialog_cargando;

    private String editar_crear;
    private Vehiculo vehiculo;

    private boolean marcasCargadas = false;
    private boolean tiposCargados = false;
    private boolean accesoriosCargados = false;
    private boolean imagenActualizada = false;

    private String path;
    private final int VALOR_RETORNO = 21;
    private final int COD_CAMARA = 1;
    private File fileImagen;
    private Bitmap bitmap;
    private final String CARPETA_PRINCIPAL = "GolfyTurf/";
    private final String RUTA_IMAGEN = CARPETA_PRINCIPAL + "Imagenes";

    RequestQueue request;
    StringRequest stringRequest;
    String idMarca;
    String idTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actualizar_vehiculo);

        request = Volley.newRequestQueue(this);

        alertDialog_cargando = new AlertDialog.Builder(CrearActualizarVehiculoActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        editar_crear = getIntent().getExtras().getString("editar_crear");
        
        bindUI();

        if (editar_crear.equals("editar")){
            vehiculo = Util.getVehiculo();
            traer_accesorios_vehiculo__WebService();
        }else{
            traer_accesorios_WebService();
            et_precio_sin_iva_vehiculo_act_crear.setText("0");
            et_iva_vehiculo_act_crear.setText("0");
        }

        traer_marcas_WebService(spinner_marca_vehiculo);
        traer_tipos_WebService(spinner_tipo_vehiculo);

        comprobar_editar_crear();

        et_precio_sin_iva_vehiculo_act_crear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && Integer.parseInt(s.toString()) != 0) {
                    long precio_nuevo_sin_iva = Integer.parseInt(s.toString()) ;
                    long precio_nuevo_iva = 0;
                    double iva_maquina = 0;
                    if(et_iva_vehiculo_act_crear.getText().toString().isEmpty()){
                        iva_maquina = 0;
                    }else{
                        iva_maquina = Double.parseDouble(et_iva_vehiculo_act_crear.getText().toString());
                    }
                    precio_nuevo_iva = (long) (precio_nuevo_sin_iva + (precio_nuevo_sin_iva*iva_maquina));
                    String format_valor_nuevo= NumberFormat.getInstance().format(precio_nuevo_iva);
                    tv_valor_final_act_crear_vehiculo.setText("$ "+format_valor_nuevo);
                }else{
                    if (editar_crear.equals("editar")){
                        String format_valor_nuevo= NumberFormat.getInstance().format(vehiculo.getValor_IVA());
                        tv_valor_final_act_crear_vehiculo.setText("$ "+format_valor_nuevo);
                    }else{
                        String format_valor_nuevo= NumberFormat.getInstance().format(0);
                        tv_valor_final_act_crear_vehiculo.setText("$ "+format_valor_nuevo);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_iva_vehiculo_act_crear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && Double.parseDouble(s.toString()) != 0) {
                    long precio_nuevo_sin_iva =0;
                    if(et_precio_sin_iva_vehiculo_act_crear.getText().toString().isEmpty()){
                        precio_nuevo_sin_iva = 0;
                    }else{
                        precio_nuevo_sin_iva = Integer.parseInt(et_precio_sin_iva_vehiculo_act_crear.getText().toString());
                    }
                    long precio_nuevo_iva = 0;
                    double iva_maquina = Double.parseDouble(s.toString()) ;
                    precio_nuevo_iva = (long) (precio_nuevo_sin_iva + (precio_nuevo_sin_iva*iva_maquina));
                    String format_valor_nuevo= NumberFormat.getInstance().format(precio_nuevo_iva);
                    tv_valor_final_act_crear_vehiculo.setText("$ "+format_valor_nuevo);
                }else{
                    if (editar_crear.equals("editar")){
                        String format_valor_nuevo= NumberFormat.getInstance().format(vehiculo.getValor_IVA());
                        tv_valor_final_act_crear_vehiculo.setText("$ "+format_valor_nuevo);
                    }else{
                        long precio_nuevo_sin_iva =0;
                        if(et_precio_sin_iva_vehiculo_act_crear.getText().toString().isEmpty()){
                            precio_nuevo_sin_iva = 0;
                        }else{
                            precio_nuevo_sin_iva = Integer.parseInt(et_precio_sin_iva_vehiculo_act_crear.getText().toString());
                        }
                        String format_valor_nuevo= NumberFormat.getInstance().format(precio_nuevo_sin_iva);
                        tv_valor_final_act_crear_vehiculo.setText("$ "+format_valor_nuevo);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_imagen_vehiculo_act_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });

        spinner_marca_vehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Marca marcaSelected = (Marca) spinner_marca_vehiculo.getSelectedItem();
                idMarca = marcaSelected.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_tipo_vehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tipo_Vehiculo tipoSelected = (Tipo_Vehiculo) spinner_tipo_vehiculo.getSelectedItem();
                idTipo = tipoSelected.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_act_crear_vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_cargando = new AlertDialog.Builder(CrearActualizarVehiculoActivity.this).create();
                alertDialog_cargando.setMessage("Cargando...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
                if (editar_crear.equals("crear")){
                    if (imagenActualizada){
                        actualizar_vehculo_WebService();
                    }else{
                        Toast.makeText(CrearActualizarVehiculoActivity.this,"Debe selccionar una imagen",Toast.LENGTH_SHORT).show();
                        alertDialog_cargando.dismiss();
                    }
                }else{
                    actualizar_vehculo_WebService();
                }
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case VALOR_RETORNO:
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        // iv_imagen_maquina_act_crear.setImageBitmap(bitmap);
                        Picasso.with(this).load(data.getData()).fit().into(iv_imagen_vehiculo_act_crear);
                        imagenActualizada = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case COD_CAMARA:

                    Uri imageUri = Uri.parse(path);
                    File file = new File(imageUri.getPath());
                    bitmap = BitmapFactory.decodeFile(path);
                    Picasso.with(this).load(file).fit().into(iv_imagen_vehiculo_act_crear);
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
            bitmap = redimensionarImagen(bitmap, 600,600);
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
        //Toast.makeText(this, imagenString, Toast.LENGTH_SHORT).show();
        return imagenString;
    }

    private void comprobar_editar_crear() {
        if (editar_crear.equals("editar")){
            Picasso.with(this).load(vehiculo.getImagen()).into(iv_imagen_vehiculo_act_crear);
            et_nombre_vehiculo_act_crear.setText(vehiculo.getModelo());
            et_precio_sin_iva_vehiculo_act_crear.setText(vehiculo.getValor()+"");
            et_iva_vehiculo_act_crear.setText(vehiculo.getIVA()+"");
            et_motor_vehiculo_act_crear.setText(vehiculo.getMotor());
            et_chasis_vehiculo_act_crear.setText(vehiculo.getChasis());
            et_frenos_vehiculo_act_crear.setText(vehiculo.getFrenos());
            et_velocidad_vehiculo_act_crear.setText(vehiculo.getVelocidad());
            et_capacidad_vehiculo_act_crear.setText(vehiculo.getCapacidad());
            et_capacidad_carga_vehiculo_act_crear.setText(vehiculo.getCapacidad_carga());
            et_peso_vehiculo_act_crear.setText(vehiculo.getPeso());
            et_ancho_carga_vehiculo_act_crear.setText(vehiculo.getAncho_vehiculo());
            et_largo_vehiculo_act_crear.setText(vehiculo.getLargo_vehiculo());
            et_incluye_vehiculo_act_crear.setText(vehiculo.getDatos_incluye());
            et_colores_vehiculo_act_crear.setText(vehiculo.getColores());
            et_distancia_suelo_vehiculo_act_crear2.setText(vehiculo.getDistancia_al_suelo());
            String format_valor= NumberFormat.getInstance().format(vehiculo.getValor_IVA());
            tv_valor_final_act_crear_vehiculo.setText("$ "+ format_valor);
            tv_titulo_act_crear_vehiculo.setText("Editar Vehículo");
            btn_act_crear_vehiculo.setText("Actualizar");
        }
    }

    private void traer_tipos_WebService(final Spinner spinner_tipo_vehiculo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_tipos_vehiculo.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                ArrayList<Tipo_Vehiculo> tipos_vehiculo = new ArrayList<>();
                String id;
                String tipo;
                Tipo_Vehiculo tipo_vehiculo;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        tipo = jsonArray.getJSONObject(i).getString("Tipo");

                        tipo_vehiculo = new Tipo_Vehiculo(id,tipo);
                        tipos_vehiculo.add(tipo_vehiculo);

                    }
                    spinner_tipo_vehiculo.setAdapter(new ArrayAdapter<Tipo_Vehiculo>(CrearActualizarVehiculoActivity.this, android.R.layout.simple_list_item_1, tipos_vehiculo));
                    if (editar_crear.equals("editar")){
                        for (int i = 0; i < tipos_vehiculo.size(); i++) {
                            if (tipos_vehiculo.get(i).getId().equals(vehiculo.getTipo())){
                                spinner_tipo_vehiculo.setSelection(i);
                            }
                        }
                    }
                    tiposCargados = true;
                    hideAlertDialog();

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

    private void traer_marcas_WebService(final Spinner spinner_marca_vehiculo) {

            String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_marcas.php";
            url = url.replace(" ", "%20");

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    JSONArray jsonArray = null;
                    ArrayList<Marca> marcas = new ArrayList<>();
                    String id;
                    String des_marca;
                    Marca marca;
                    try {
                        jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++ ){

                            id = jsonArray.getJSONObject(i).getString("Id");
                            des_marca = jsonArray.getJSONObject(i).getString("Marca");

                            marca = new Marca(id,des_marca);
                            marcas.add(marca);

                        }
                        spinner_marca_vehiculo.setAdapter(new ArrayAdapter<Marca>(CrearActualizarVehiculoActivity.this, android.R.layout.simple_list_item_1, marcas));
                        if (editar_crear.equals("editar")){
                            for (int i = 0; i < marcas.size(); i++) {
                                if (marcas.get(i).getMarca().equals(vehiculo.getMarca())){
                                    spinner_marca_vehiculo.setSelection(i);
                                }
                            }
                        }
                        marcasCargadas = true;
                        hideAlertDialog();

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
                    parametros.put("filtro", "1");
                    return  parametros;
                }
            };
            request.add(stringRequest);
    }

    private void hideAlertDialog() {
        if (marcasCargadas && tiposCargados && accesoriosCargados){
            alertDialog_cargando.dismiss();
        }

    }

    private void traer_accesorios_WebService() {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_accesorios.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Accesorio accesorio= null;

                String Id;
                String Referencia;
                int Precio;
                double IVA;
                long Precio_IVA;
                long Aumento_IVA;


                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Precio = jsonArray.getJSONObject(i).getInt("Precio");
                        Referencia = jsonArray.getJSONObject(i).getString("Accesorio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");

                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

                        accesorio = new Accesorio();

                        accesorio.setId(Id);
                        accesorio.setReferencia(Referencia);
                        accesorio.setValor(Precio);
                        accesorio.setAumento_IVA(Aumento_IVA);
                        accesorio.setPrecio_IVA(Precio_IVA);
                        accesorio.setCheck(false);

                        accesorios.add(accesorio);
                    }

                    adapter_accesorio = new Accesorio_Vehiculo_Adapter(CrearActualizarVehiculoActivity.this, accesorios, R.layout.list_view_item_accesorio_vehiculo, new Accesorio_Vehiculo_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Accesorio accesorio, int position) {

                        }
                    }, new Accesorio_Vehiculo_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Accesorio accesorio, int position, boolean isChecked) {
                            if(isChecked){
                                accesorio.setCheck(true);
                            }else{
                                accesorio.setCheck(false);
                                accesorios_vehiculo.remove(accesorios.get(position));
                            }
                            comprobarAccesoriosAgregados();
                        }
                    });
                    recycler_view_accesorios_vehiculo_act_crear.setAdapter(adapter_accesorio);
                    accesoriosCargados = true;
                    hideAlertDialog();
                    accesorios = comprobarAccesorioVehiculo(accesorios);
                    adapter_accesorio.notifyDataSetChanged();

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
                parametros.put("id_vehiculo", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void comprobarAccesoriosAgregados() {
        accesorios_vehiculo = new ArrayList<>();
        for (int i = 0; i < accesorios.size() ; i++) {
            if(accesorios.get(i).isCheck()){
                accesorios_vehiculo.add(accesorios.get(i));
            }
        }
    }

    private ArrayList<Accesorio> comprobarAccesorioVehiculo(ArrayList<Accesorio> accesorios) {
        for (int i = 0; i < accesorios.size() ; i++) {
            Accesorio accesorio= accesorios.get(i);
            for (int j = 0; j <accesorios_vehiculo.size() ; j++) {
                Accesorio accesorio_vehiculo= accesorios_vehiculo.get(j);
                if (accesorio.getId().equals(accesorio_vehiculo.getId())){
                    accesorios.get(i).setCheck(true);
                }
            }
        }
        return accesorios;
    }

    private void traer_accesorios_vehiculo__WebService() {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_accesorios.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                traer_accesorios_WebService();
                JSONArray jsonArray = null;
                Accesorio accesorio= null;
                String Id;
                String Referencia;
                int Precio;
                double IVA;
                long Precio_IVA;
                long Aumento_IVA;


                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Precio = jsonArray.getJSONObject(i).getInt("Precio");
                        Referencia = jsonArray.getJSONObject(i).getString("Accesorio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");

                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

                        accesorio = new Accesorio();

                        accesorio.setId(Id);
                        accesorio.setReferencia(Referencia);
                        accesorio.setValor(Precio);
                        accesorio.setAumento_IVA(Aumento_IVA);
                        accesorio.setPrecio_IVA(Precio_IVA);
                        accesorio.setCheck(false);

                        accesorios_vehiculo.add(accesorio);
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
                parametros.put("id_vehiculo", vehiculo.getId());
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void bindUI() {
        iv_imagen_vehiculo_act_crear = (ImageView) findViewById(R.id.iv_imagen_vehiculo_act_crear);
        et_nombre_vehiculo_act_crear = (EditText) findViewById(R.id.et_nombre_vehiculo_act_crear);
        et_precio_sin_iva_vehiculo_act_crear = (EditText) findViewById(R.id.et_precio_sin_iva_vehiculo_act_crear);
        et_iva_vehiculo_act_crear = (EditText) findViewById(R.id.et_iva_vehiculo_act_crear);
        et_motor_vehiculo_act_crear = (EditText) findViewById(R.id.et_motor_vehiculo_act_crear);
        et_chasis_vehiculo_act_crear = (EditText) findViewById(R.id.et_chasis_vehiculo_act_crear);
        et_frenos_vehiculo_act_crear = (EditText) findViewById(R.id.et_frenos_vehiculo_act_crear);
        et_velocidad_vehiculo_act_crear = (EditText) findViewById(R.id.et_velocidad_vehiculo_act_crear);
        et_capacidad_vehiculo_act_crear = (EditText) findViewById(R.id.et_capacidad_vehiculo_act_crear);
        et_capacidad_carga_vehiculo_act_crear = (EditText) findViewById(R.id.et_capacidad_carga_vehiculo_act_crear);
        et_peso_vehiculo_act_crear = (EditText) findViewById(R.id.et_peso_vehiculo_act_crear);
        et_ancho_carga_vehiculo_act_crear = (EditText) findViewById(R.id.et_ancho_carga_vehiculo_act_crear);
        et_largo_vehiculo_act_crear = (EditText) findViewById(R.id.et_largo_vehiculo_act_crear);
        et_incluye_vehiculo_act_crear = (EditText) findViewById(R.id.et_incluye_vehiculo_act_crear);
        et_colores_vehiculo_act_crear = (EditText) findViewById(R.id.et_colores_vehiculo_act_crear);
        et_distancia_suelo_vehiculo_act_crear2 = (EditText) findViewById(R.id.et_distancia_suelo_vehiculo_act_crear2);
        spinner_marca_vehiculo = (Spinner) findViewById(R.id.spinner_marca_vehiculo);
        spinner_tipo_vehiculo = (Spinner) findViewById(R.id.spinner_tipo_vehiculo);
        tv_valor_final_act_crear_vehiculo = (TextView) findViewById(R.id.tv_valor_final_act_crear_vehiculo);
        tv_titulo_act_crear_vehiculo = (TextView) findViewById(R.id.tv_titulo_act_crear_vehiculo);
        btn_act_crear_vehiculo = (Button) findViewById(R.id.btn_act_crear_vehiculo);
        recycler_view_accesorios_vehiculo_act_crear = (RecyclerView) findViewById(R.id.recycler_view_accesorios_vehiculo_act_crear);
        recycler_view_accesorios_vehiculo_act_crear.setHasFixedSize(true);
        recycler_view_accesorios_vehiculo_act_crear.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager_componentes = new LinearLayoutManager(this);
        itemDecoration_componentes = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_view_accesorios_vehiculo_act_crear.setLayoutManager(mLayoutManager_componentes);
        recycler_view_accesorios_vehiculo_act_crear.addItemDecoration(itemDecoration_componentes);
    }

    private void actualizar_vehculo_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/editar_crear_vehiculo.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                Toast.makeText(CrearActualizarVehiculoActivity.this, response, Toast.LENGTH_LONG).show();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(CrearActualizarVehiculoActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CrearActualizarVehiculoActivity.this,VehiculosActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(CrearActualizarVehiculoActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(CrearActualizarVehiculoActivity.this, "j error"+e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(CrearActualizarVehiculoActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CrearActualizarVehiculoActivity.this,VehiculosActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(CrearActualizarVehiculoActivity.this, " v error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();

                String modelo = et_nombre_vehiculo_act_crear.getText().toString();
                String valor = et_precio_sin_iva_vehiculo_act_crear.getText().toString();
                String IVA = et_iva_vehiculo_act_crear.getText().toString();
                String motor = et_motor_vehiculo_act_crear.getText().toString();
                String chasis = et_chasis_vehiculo_act_crear.getText().toString();
                String frenos = et_frenos_vehiculo_act_crear.getText().toString();
                String velocidad = et_velocidad_vehiculo_act_crear.getText().toString();
                String capacidad = et_capacidad_vehiculo_act_crear.getText().toString();
                String capacidad_carga = et_capacidad_carga_vehiculo_act_crear.getText().toString();
                String peso = et_peso_vehiculo_act_crear.getText().toString();
                String ancho_vehiculo = et_ancho_carga_vehiculo_act_crear.getText().toString();
                String largo_vehiculo = et_largo_vehiculo_act_crear.getText().toString();
                String datos_incluye = et_incluye_vehiculo_act_crear.getText().toString();
                String colores = et_colores_vehiculo_act_crear.getText().toString();
                String distancia_suelo = et_distancia_suelo_vehiculo_act_crear2.getText().toString();

                String imagen_maquina ="";
                if(imagenActualizada){
                    imagen_maquina = convertirImagenString(bitmap);
                }else{
                    imagen_maquina = "";
                }
                if (editar_crear.equals("crear")){
                    parametros.put("Id", "");
                }else{
                    parametros.put("Id", vehiculo.getId());
                }
                parametros.put("tipo", idTipo);
                parametros.put("marca", idMarca);
                parametros.put("modelo", modelo);
                parametros.put("motor",motor);
                parametros.put("chasis", chasis);
                parametros.put("velocidad", velocidad);
                parametros.put("capacidad", capacidad);
                parametros.put("capacidad_carga",capacidad_carga);
                parametros.put("frenos",frenos);
                parametros.put("ancho_vehiculo",ancho_vehiculo);
                parametros.put("largo_vehiculo",largo_vehiculo);
                parametros.put("peso",peso);
                parametros.put("valor",valor);
                parametros.put("IVA",IVA);
                parametros.put("imagen", imagen_maquina);
                parametros.put("datos_incluye",datos_incluye);
                parametros.put("colores",colores);
                parametros.put("distancia_suelo",distancia_suelo);
                parametros.put("Numero_accesorios",accesorios_vehiculo.size()+"");
                parametros.put("Editar_crear",editar_crear);
                for (int i=0; i<accesorios_vehiculo.size(); i++){
                    Accesorio accesorio_vehiculo = accesorios_vehiculo.get(i);
                    parametros.put("Id_accesorio"+i, accesorio_vehiculo.getId() );
                }

                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);
    }
}
