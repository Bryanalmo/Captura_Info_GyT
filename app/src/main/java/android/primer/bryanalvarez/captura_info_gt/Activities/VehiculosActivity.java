package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Vehiculos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class VehiculosActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private FrameLayout frame_layout_num_vehiculos;
    private FloatingActionButton floatingActionButtonNumVehiculos;
    private TextView tv_num_vehiculos;
    private FloatingActionButton floatingActionButtonCotizaciones;
    private ListView listViewVehiculos;
    private Vehiculos_Adapter adapter_vehiculos;
    private ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        alertDialog_cargando = new AlertDialog.Builder(VehiculosActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        request = Volley.newRequestQueue(this);

        bindUI();
        floatingActionButtonNumVehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_cargando = new AlertDialog.Builder(VehiculosActivity.this).create();
                alertDialog_cargando.setMessage("¿Desea enviar la cotización ahora?");
                alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAlertDatosCliente("Datos","Ingresa los datos del cliente");

                    }
                });
                alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog_cargando.show();
            }
        });

        listViewVehiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VehiculosActivity.this,CotizarActivity.class);
                intent.putExtra("editar_crear","crear");
                intent.putExtra("id_vehiculo",vehiculos.get(position).getId());
                startActivity(intent);
                Util.setVehiculo(vehiculos.get(position));
            }
        });

        floatingActionButtonCotizaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehiculosActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        traer_vehiculos_WebService();

        registerForContextMenu(listViewVehiculos);
        hideShowNumMaquinas();
    }

    private void bindUI() {
        floatingActionButtonNumVehiculos = (FloatingActionButton) findViewById(R.id.floatingActionButtonNumVehiculos);
        frame_layout_num_vehiculos = (FrameLayout) findViewById(R.id.frame_layout_num_vehiculos);
        tv_num_vehiculos = (TextView) findViewById(R.id.tv_num_vehiculos);
        floatingActionButtonCotizaciones = (FloatingActionButton) findViewById(R.id.floatingActionButtonCotizaciones);
        listViewVehiculos = (ListView) findViewById(R.id.listViewVehiculos);
    }

    private void hideShowNumMaquinas() {
        if (Util.cotizaciones_vehiculos.size() == 0){
            frame_layout_num_vehiculos.setVisibility(View.GONE);
        }else{
            frame_layout_num_vehiculos.setVisibility(View.VISIBLE);
            tv_num_vehiculos.setText(Util.cotizaciones_vehiculos.size()+"");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones_vehiculos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_vehiculos_crear:
                Intent intent = new Intent(VehiculosActivity.this,CrearActualizarVehiculoActivity.class);
                intent.putExtra("editar_crear","crear");
                startActivity(intent);
                return true;
            case R.id.menu_vehiculos_crear_accesorio:
                showDialogCrearAccesorio();
                return true;
            case R.id.menu_vehiculos_modificar_filtro:
                Intent intent2 = new Intent(VehiculosActivity.this,ModificarFiltroVehiculosActivity.class);
                startActivity(intent2);
                return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    private void showDialogCrearAccesorio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Crear accesorio");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_accesorio_vehiculo, null);
        builder.setView(viewInflated);

        final EditText et_dialog_nombre_accesorio_vehiculo = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_accesorio_vehiculo);
        final EditText et_dialog_precio_accesorio_vehiculo = (EditText) viewInflated.findViewById(R.id.et_dialog_precio_accesorio_vehiculo);
        final EditText et_dialog_IVA_accesorio_vehiculo = (EditText) viewInflated.findViewById(R.id.et_dialog_IVA_accesorio_vehiculo);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = et_dialog_nombre_accesorio_vehiculo.getText().toString();
                String precio = et_dialog_precio_accesorio_vehiculo.getText().toString();
                String IVA = et_dialog_IVA_accesorio_vehiculo.getText().toString();

                alertDialog_cargando = new AlertDialog.Builder(VehiculosActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();

                crear_accesorio_vehiculo_WebService(nombre,precio,IVA);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void crear_accesorio_vehiculo_WebService(final String nombre, final String precio, final String iva) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_accesorio_vehiculo.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(VehiculosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(VehiculosActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(VehiculosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("Nombre", nombre);
                parametros.put("Precio", precio);
                parametros.put("IVA", iva);

                return parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        inflater.inflate(R.menu.context_menu_vehiculos, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Obtener info en el context menu del objeto que se pinche
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.editar_vehiculo:
                Intent intent = new Intent(VehiculosActivity.this,CrearActualizarVehiculoActivity.class);
                Util.setVehiculo(vehiculos.get(info.position));
                intent.putExtra("editar_crear","editar");
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void traer_vehiculos_WebService() {

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_vehiculos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Vehiculo vehiculo = null;
                ArrayList<Vehiculo> vehiculos_WS = new ArrayList<>();

                String Id;
                int Precio;
                String Tipo;
                String Marca;
                String Modelo;
                String Motor;
                String Chasis;
                String Velocidad;
                String Capacidad;
                String Capacidad_carga;
                String Frenos;
                String Ancho;
                String Largo;
                String Peso;
                String Imagen_vehiculo;
                String Datos_incluye;
                String Color;
                String Distancia_suelo;
                double IVA;
                long Precio_IVA;
                long Aumento_IVA;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Precio = jsonArray.getJSONObject(i).getInt("Precio");
                        Tipo = jsonArray.getJSONObject(i).getString("Tipo");
                        Marca = jsonArray.getJSONObject(i).getString("Marca");
                        Modelo = jsonArray.getJSONObject(i).getString("Nombre_vehiculo");
                        Motor = jsonArray.getJSONObject(i).getString("Motor");
                        Chasis = jsonArray.getJSONObject(i).getString("Chasis");
                        Velocidad = jsonArray.getJSONObject(i).getString("Velocidad");
                        Capacidad = jsonArray.getJSONObject(i).getString("Capacidad");
                        Capacidad_carga = jsonArray.getJSONObject(i).getString("Capacidad_carga");
                        Frenos = jsonArray.getJSONObject(i).getString("Frenos");
                        Ancho = jsonArray.getJSONObject(i).getString("Ancho");
                        Largo = jsonArray.getJSONObject(i).getString("Largo");
                        Peso = jsonArray.getJSONObject(i).getString("Peso");
                        Imagen_vehiculo = jsonArray.getJSONObject(i).getString("Imagen_vehiculo");
                        Datos_incluye = jsonArray.getJSONObject(i).getString("Info_incluye");
                        Color = jsonArray.getJSONObject(i).getString("Color");
                        Distancia_suelo = jsonArray.getJSONObject(i).getString("Distancia_suelo");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Precio = (int) (Precio * Util.monedaActual);
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

                        if (Util.monedaActual != 1){
                            Precio_IVA = redondearPrecio(Precio_IVA);
                            Precio = redondearPrecioSinIVa(Precio_IVA, IVA);
                            Aumento_IVA = recalcularAumentoIVA(Precio, IVA);
                        }

                        vehiculo = new Vehiculo();

                        vehiculo.setId(Id);
                        vehiculo.setTipo(Tipo);
                        vehiculo.setMarca(Marca);
                        vehiculo.setModelo(Modelo);
                        vehiculo.setMotor(Motor);
                        vehiculo.setChasis(Chasis);
                        vehiculo.setVelocidad(Velocidad);
                        vehiculo.setCapacidad(Capacidad);
                        vehiculo.setCapacidad_carga(Capacidad_carga);
                        vehiculo.setFrenos(Frenos);
                        vehiculo.setAncho_vehiculo(Ancho);
                        vehiculo.setLargo_vehiculo(Largo);
                        vehiculo.setPeso(Peso);
                        vehiculo.setValor(Precio);
                        vehiculo.setValor_IVA(Precio_IVA);
                        vehiculo.setAumento_IVA(Aumento_IVA);
                        vehiculo.setImagen(Imagen_vehiculo);
                        vehiculo.setIVA(IVA);
                        vehiculo.setDatos_incluye(Datos_incluye);
                        vehiculo.setColores(Color);
                        vehiculo.setDistancia_al_suelo(Distancia_suelo);
                        vehiculo.setAgregado(false);

                        vehiculos_WS.add(vehiculo);
                    }
                    adapter_vehiculos = new Vehiculos_Adapter(VehiculosActivity.this, vehiculos_WS, R.layout.list_view_item_vehiculo);
                    listViewVehiculos.setAdapter(adapter_vehiculos);
                    vehiculos=vehiculos_WS;
                    alertDialog_cargando.dismiss();
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
                parametros.put("En_cotizador", "1");
                return  parametros;
            }
        };;
        request.add(stringRequest);
    }

    private long recalcularAumentoIVA(int precio, double iva) {
        return (long) (precio * iva);
    }

    private int redondearPrecioSinIVa(long precio_iva, double iva) {
        return (int) (precio_iva / (1+iva));
    }

    private long redondearPrecio(long precio) {

        double precio_objetivo = 0;
        double precio_round = precio/100000;
        for (int i = 1000000; i < 100000000 ; i+=1000000) {
            if(precio>=i && precio<(i+1000000)){
                int factor = 1000;
                while (precio_round != precio_objetivo){

                    long precio_div = precio/(factor);
                    double precio_div_round = Math.ceil(precio_div);
                    precio = (long) (precio_div_round*(factor));

                    precio_objetivo = i/100000;
                    precio_round = precio/100000;

                    factor = factor*10;

                }
                break;
            }
        }

        return precio - 100000;
    }

    private void showAlertDatosCliente(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_datos_cliente, null);
        builder.setView(viewInflated);

        final EditText et_dialog_nombre_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_cliente);
        final EditText et_dialog_nit_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_nit_cliente);
        final EditText et_dialog_celular_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_celular_cliente);
        final EditText et_dialog_correo_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_correo_cliente);
        final EditText et_dialog_ciudad_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_ciudad_cliente);
        final EditText et_dialog_ciudad_observaciones = (EditText) viewInflated.findViewById(R.id.et_dialog_ciudad_observaciones);
        final ImageButton ib_dialog_cargar_cliente = (ImageButton) viewInflated.findViewById(R.id.ib_dialog_cargar_cliente);
        final Button bt_dialog_enviar_cotizacion = (Button) viewInflated.findViewById(R.id.bt_dialog_enviar_cotizacion);
        final CheckBox cb_tratamiento_datos= (CheckBox) viewInflated.findViewById(R.id.cb_tratamiento_datos);

        final AlertDialog dialog = builder.create();
        dialog.show();

        bt_dialog_enviar_cotizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_dialog_nombre_cliente.getText().toString();
                String nit = et_dialog_nit_cliente.getText().toString();
                String celular = et_dialog_celular_cliente.getText().toString();
                String correo = et_dialog_correo_cliente.getText().toString().replace(" ","");
                String ciudad = et_dialog_ciudad_cliente.getText().toString();
                String observaciones = et_dialog_ciudad_observaciones.getText().toString();
                String id_comercial = Util.getId_usuario();
                if(cb_tratamiento_datos.isChecked()){
                    if (correo.isEmpty()){
                        Toast.makeText(VehiculosActivity.this,"Llene el campo Correo",Toast.LENGTH_SHORT).show();
                    }else{
                        if (!validarEmail(correo)){
                            Toast.makeText(VehiculosActivity.this,"Email no valido",Toast.LENGTH_SHORT).show();
                            et_dialog_correo_cliente.setError("Email no válido");
                        }else {
                            dialog.dismiss();
                            showAlertDetallesCorreo(nombre, nit, celular, correo, ciudad, id_comercial, observaciones);
                        }
                    }
                }else{
                    Toast.makeText(VehiculosActivity.this,"Por favor apruebe el tratamiento de datos",Toast.LENGTH_SHORT).show();
                }

            }
        });

        ib_dialog_cargar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertCargarCliente(et_dialog_nombre_cliente, et_dialog_nit_cliente, et_dialog_celular_cliente, et_dialog_correo_cliente, et_dialog_ciudad_cliente);
            }
        });


    }

    private void showAlertCargarCliente(final EditText et_dialog_nombre_cliente, final EditText et_dialog_nit_cliente, final EditText et_dialog_celular_cliente, final EditText et_dialog_correo_cliente, final EditText et_dialog_ciudad_cliente) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clientes añadidos");


        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_clientes_agregados, null);
        builder.setView(viewInflated);

        final Spinner spinner_clientes_agregados = (Spinner) viewInflated.findViewById(R.id.spinner_clientes_agregados);
        traer_datos_clientes_WebService(spinner_clientes_agregados);


        builder.setPositiveButton("Cargar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cliente cliente_selec = (Cliente) spinner_clientes_agregados.getSelectedItem();
                et_dialog_nombre_cliente.setText(cliente_selec.getNombre());
                et_dialog_nit_cliente.setText(cliente_selec.getCedula_nit());
                et_dialog_celular_cliente.setText(cliente_selec.getCelular());
                et_dialog_correo_cliente.setText(cliente_selec.getCorreo());
                et_dialog_ciudad_cliente.setText(cliente_selec.getCiudad());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void traer_datos_clientes_WebService(final Spinner spinner){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_clientes.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Cliente cliente = null;
                ArrayList<Cliente> clientes = new ArrayList<>();

                String id;
                String nombre;
                String cedula_nit;
                String celular;
                String correo;
                String ciudad;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        nombre = jsonArray.getJSONObject(i).getString("Nombre");
                        cedula_nit = jsonArray.getJSONObject(i).getString("Cedula/NIT");
                        celular = jsonArray.getJSONObject(i).getString("Celular");
                        correo = jsonArray.getJSONObject(i).getString("Correo");
                        ciudad = jsonArray.getJSONObject(i).getString("Ciudad");

                        cliente = new Cliente(id,nombre,cedula_nit,celular,correo,ciudad);
                        clientes.add(cliente);

                    }
                    spinner.setAdapter(new ArrayAdapter<Cliente>(VehiculosActivity.this, android.R.layout.simple_list_item_1, clientes));

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

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void showAlertDetallesCorreo(final String nombre, final String nit, final String celular, final String correo, final String ciudad, final String id_comercial, final String observaciones) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Detalles de correo electronico");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_detalles_correo, null);
        builder.setView(viewInflated);

        final RadioGroup radioGroup  = (RadioGroup) viewInflated.findViewById(R.id.radioGroup);
        final TextView textView  = (TextView) viewInflated.findViewById(R.id.textView59);
        final EditText et_asunto_correo = (EditText) viewInflated.findViewById(R.id.et_asunto_correo);
        final EditText et_cuerpo_correo = (EditText) viewInflated.findViewById(R.id.et_cuerpo_correo);

        radioGroup.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        cargarAsuntoyCuerpoCorreo(et_asunto_correo,et_cuerpo_correo);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String asunto = et_asunto_correo.getText().toString();
                String cuerpo = et_cuerpo_correo.getText().toString();
                //Toast.makeText(CotizarRepuestosActivity.this, formaPago, Toast.LENGTH_SHORT).show();
                cotizar_WebService(nombre, nit, celular, correo, ciudad, id_comercial, observaciones, asunto,cuerpo);
                alertDialog_cargando = new AlertDialog.Builder(VehiculosActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarAsuntoyCuerpoCorreo(EditText et_asunto_correo, EditText et_cuerpo_correo){

        String cuerpo = "De acuerdo a su amable solicitud y a las necesidades descritas, tenemos el agrado de someter a su consideración nuestra oferta técnico económica para el suministro de vehículos personales y utilitarios para distancias intermedias marca EZ-GO y CUSHMAN, de procedencia americana (U.S.A) y pertenecientes a la multinacional Textron Co."
                + "\n" + "\n" +  "En Golf y Turf SAS, distribuidores autorizados para Colombia de estas marcas, agradecemos la oportunidad de presentarle esta oferta, esperando que se atractiva para ud y que sea una solución integral, de igual manera estaremos atentos a resolver cualquier duda que pueda surgir, nos puede contactar a través de los medios descritos a continuación.";
        et_cuerpo_correo.setText(cuerpo);
        String asunto = "Propuesta vehículo EZ-GO";
        et_asunto_correo.setText(asunto);
    }

    private void cotizar_WebService(final String nombre, final String nit, final String celular, final String correo, final String ciudad, final String id_comercial, final String observaciones, final String asunto, final String cuerpo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                //Toast.makeText(VehiculosActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_vehiculos.clear();
                    if(jsonObject.getString("Success").equals("true")){

                        Toast.makeText(VehiculosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VehiculosActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(VehiculosActivity.this, "El correo ingresado no es valido", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Util.cotizaciones_vehiculos.clear();
                    Log.w("ERROR_J",e.getMessage());
                    Toast.makeText(VehiculosActivity.this, "Error J" + e.getMessage() + " -- " + response , Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Intent intent = new Intent(VehiculosActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Util.cotizaciones_vehiculos.clear();
                    Toast.makeText(VehiculosActivity.this, "Creación exitosa, un email será enviado en breve.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VehiculosActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Util.cotizaciones_vehiculos.clear();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Nombre_cliente", nombre);
                parametros.put("Nit_cliente", nit);
                parametros.put("Celular_cliente", celular);
                parametros.put("Correo_cliente", correo);
                parametros.put("Ciudad_cliente", ciudad);
                parametros.put("Id_comercial", id_comercial);
                parametros.put("Observaciones", observaciones);
                parametros.put("Asunto", asunto );
                parametros.put("Cuerpo", cuerpo );
                parametros.put("Numero_sub_cotizaciones", Util.cotizaciones_vehiculos.size()+"");
                parametros.put("Moneda", Util.monedaActual+"");
                long valor_total = 0;
                for (int j = 0; j < Util.cotizaciones_vehiculos.size(); j++) {
                    parametros.put("Id_vehiculo"+j, Util.cotizaciones_vehiculos.get(j).getVehiculo().getId());
                    parametros.put("Valor_sin_IVA_Vehiculo"+j, Util.cotizaciones_vehiculos.get(j).getVehiculo().getValor()+"");
                    parametros.put("Valor_IVA_Vehiculo"+j, Util.cotizaciones_vehiculos.get(j).getVehiculo().getAumento_IVA()+"");
                    parametros.put("Valor_Vehiculo"+j, Util.cotizaciones_vehiculos.get(j).getVehiculo().getValor_IVA()+"");
                    parametros.put("Valor_sin_IVA"+j, Util.cotizaciones_vehiculos.get(j).getValor_sin_iva()+"");
                    parametros.put("Valor_IVA"+j, Util.cotizaciones_vehiculos.get(j).getValor_iva()+"");
                    parametros.put("Valor"+j, Util.cotizaciones_vehiculos.get(j).getValor()+"");
                    valor_total += Util.cotizaciones_vehiculos.get(j).getValor();
                    ArrayList<Accesorio> accesorios = Util.cotizaciones_vehiculos.get(j).getAccesorios_adicionados();
                    parametros.put("Numero_acc"+j, accesorios.size()+"");
                    for (int k = 0; k < accesorios.size() ; k++) {
                        parametros.put("Accesorio"+j+""+k, accesorios.get(k).getId());
                    }
                }
                parametros.put("Valor_total", valor_total+"");
                return  parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VehiculosActivity.this,MenuActivity.class);
        Util.cotizaciones_vehiculos.clear();
        startActivity(intent);
    }
}
