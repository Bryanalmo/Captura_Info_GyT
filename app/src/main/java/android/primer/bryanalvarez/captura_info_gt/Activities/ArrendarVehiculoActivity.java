package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Models.Arrendamiento_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Plazo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ArrendarVehiculoActivity extends AppCompatActivity {

    private EditText et_tiempo1_arrendamiento;
    private EditText et_tiempo2_arrendamiento;
    private EditText et_tiempo3_arrendamiento;
    private EditText et_precio1_arrendamiento;
    private EditText et_precio2_arrendamiento;
    private EditText et_precio3_arrendamiento;
    private EditText et_cantidad_arrendamiento;
    private ImageView iv_vehiculo_cotizar;
    private ImageView iv_marca;
    private Button bt_arrendar;

    private ArrayList<Plazo> plazos = new ArrayList<>();

    private AlertDialog alertDialog;
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;
    String editar_crear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrendar_vehiculo);

        alertDialog = new AlertDialog.Builder(ArrendarVehiculoActivity.this).create();
        alertDialog.setMessage("Enviando datos...");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog_cargando = new AlertDialog.Builder(ArrendarVehiculoActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        request = Volley.newRequestQueue(this);

        bindUI();

        editar_crear = getIntent().getExtras().getString("editar_crear");
        if(editar_crear.equals("editar")){
            plazos = Util.arrendamiento_vehiculo.getPlazos();
            et_cantidad_arrendamiento.setText(Util.arrendamiento_vehiculo.getCantidad());
            bindPlazos();
            alertDialog_cargando.dismiss();
        }else {
            traer_precios_arrendamiento_WebService(Util.getVehiculo().getId());
        }

        if (Util.getVehiculo().getMarca().equals("EZGO")){
            iv_marca.setImageResource(R.drawable.ezgo);
        }else if (Util.getVehiculo().getMarca().equals("CUSHMAN")){
            iv_marca.setImageResource(R.drawable.cushman);
        }

        Picasso.with(this).load(Util.getVehiculo().getImagen()).into(iv_vehiculo_cotizar);

        bt_arrendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarArrendamiento();
                if(editar_crear.equals("crear")){
                    AlertDialog alertDialogAgregar = new AlertDialog.Builder(ArrendarVehiculoActivity.this).create();
                    alertDialogAgregar.setMessage("¿Desea agregar más vehiculos a la cotización?");
                    alertDialogAgregar.setCancelable(false);
                    alertDialogAgregar.setCanceledOnTouchOutside(false);
                    alertDialogAgregar.setButton(DialogInterface.BUTTON_POSITIVE, "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ArrendarVehiculoActivity.this,ArrendamientoActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialogAgregar.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showAlertDatosCliente("Datos","Ingresa los datos del cliente");
                        }
                    });
                    alertDialogAgregar.show();

                }else{
                    Intent intent = new Intent(ArrendarVehiculoActivity.this,VerCotizacionArrendamientoActivity.class);
                    startActivity(intent);
                }

            }
        });
    }


    private void agregarArrendamiento() {
        plazos.clear();
        String plazo1 = et_tiempo1_arrendamiento.getText().toString();
        Long precio1 = Long.valueOf(et_precio1_arrendamiento.getText().toString());
        String plazo2 = et_tiempo2_arrendamiento.getText().toString();
        Long precio2 = Long.valueOf(et_precio2_arrendamiento.getText().toString());
        String plazo3 = et_tiempo3_arrendamiento.getText().toString();
        Long precio3 = Long.valueOf(et_precio3_arrendamiento.getText().toString());
        String cantidad = et_cantidad_arrendamiento.getText().toString();
        plazos.add(new Plazo(plazo1,precio1));
        plazos.add(new Plazo(plazo2,precio2));
        plazos.add(new Plazo(plazo3,precio3));

        Arrendamiento_Vehiculo arrendamiento_vehiculo = new Arrendamiento_Vehiculo(Util.getVehiculo(),cantidad,plazos);
        if (editar_crear.equals("editar")){
            for (int i = 0; i < Util.arrendamientos_vehiculos.size(); i++) {
                if (Util.arrendamientos_vehiculos.get(i).getId().equals(Util.arrendamiento_vehiculo.getId())){
                    Util.arrendamientos_vehiculos.remove(i);
                    Util.arrendamientos_vehiculos.add(i,arrendamiento_vehiculo);
                }
            }
        }else{
            Util.arrendamientos_vehiculos.add(arrendamiento_vehiculo);
        }



    }

    private void traer_precios_arrendamiento_WebService(final String id_vehiculo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_plazos_arrendamiento_vehiculo.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Plazo plazo= null;
                //Toast.makeText(ArrendarVehiculoActivity.this, response, Toast.LENGTH_SHORT).show();
                String Id;
                String Plazo;
                long Precio;

                if (response.equals("[]")){
                    bindPlazosDefault();
                }else{
                    try {
                        jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++ ){

                            Id = jsonArray.getJSONObject(i).getString("Id");
                            Precio = jsonArray.getJSONObject(i).getLong("Precio");
                            Plazo = jsonArray.getJSONObject(i).getString("Plazo");

                            plazo = new Plazo(Id,Plazo,Precio);
                            plazos.add(plazo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                alertDialog_cargando.dismiss();
                bindPlazos();

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("id_vehiculo", id_vehiculo);
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void bindPlazos() {
        et_tiempo1_arrendamiento.setText(plazos.get(0).getPlazo());
        et_tiempo2_arrendamiento.setText(plazos.get(1).getPlazo());
        et_tiempo3_arrendamiento.setText(plazos.get(2).getPlazo());
        et_precio1_arrendamiento.setText(plazos.get(0).getPrecio()+"");
        et_precio2_arrendamiento.setText(plazos.get(1).getPrecio()+"");
        et_precio3_arrendamiento.setText(plazos.get(2).getPrecio()+"");
    }

    private void bindPlazosDefault() {
        //Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
        plazos.add(new Plazo("6 meses", 0));
        plazos.add(new Plazo("12 meses", 0));
        plazos.add(new Plazo("24 meses", 0));
    }

    private void bindUI() {
        iv_marca = (ImageView) findViewById(R.id.iv_marca);
        iv_vehiculo_cotizar = (ImageView) findViewById(R.id.iv_vehiculo_cotizar);
        et_tiempo1_arrendamiento = (EditText) findViewById(R.id.et_tiempo1_arrendamiento);
        et_tiempo2_arrendamiento = (EditText) findViewById(R.id.et_tiempo2_arrendamiento);
        et_tiempo3_arrendamiento = (EditText) findViewById(R.id.et_tiempo3_arrendamiento);
        et_precio1_arrendamiento = (EditText) findViewById(R.id.et_precio1_arrendamiento);
        et_precio2_arrendamiento = (EditText) findViewById(R.id.et_precio2_arrendamiento);
        et_precio3_arrendamiento = (EditText) findViewById(R.id.et_precio3_arrendamiento);
        bt_arrendar = (Button) findViewById(R.id.bt_arrendar);
        et_cantidad_arrendamiento= (EditText) findViewById(R.id.et_cantidad_arrendamiento);
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
                String id_vehiculo = Util.getVehiculo().getId();
                String id_comercial = Util.getId_usuario();
                if(cb_tratamiento_datos.isChecked()){
                    if (correo.isEmpty()){
                        Toast.makeText(ArrendarVehiculoActivity.this,"Llene el campo Correo",Toast.LENGTH_SHORT).show();
                    }else{
                        if (!validarEmail(correo)){
                            Toast.makeText(ArrendarVehiculoActivity.this,"Email no valido",Toast.LENGTH_SHORT).show();
                            et_dialog_correo_cliente.setError("Email no válido");
                        }else {
                            dialog.dismiss();
                            showAlertDetallesCorreo(nombre, nit, celular, correo, ciudad, id_comercial, observaciones);
                        }
                    }
                }else{
                    Toast.makeText(ArrendarVehiculoActivity.this,"Por favor apruebe el tratamiento de datos",Toast.LENGTH_SHORT).show();
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
                    spinner.setAdapter(new ArrayAdapter<Cliente>(ArrendarVehiculoActivity.this, android.R.layout.simple_list_item_1, clientes));

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
                for (int i = 0; i < Util.arrendamientos_vehiculos.size(); i++) {

                    Toast.makeText(ArrendarVehiculoActivity.this, Util.arrendamientos_vehiculos.get(i).getVehiculo().getId() + Util.arrendamientos_vehiculos.get(i).getVehiculo().getModelo(), Toast.LENGTH_SHORT).show();
                }
                cotizar_WebService(nombre, nit, celular, correo, ciudad, id_comercial, observaciones, asunto, cuerpo);
                alertDialog = new AlertDialog.Builder(ArrendarVehiculoActivity.this).create();
                alertDialog.setMessage("Enviando datos");
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarAsuntoyCuerpoCorreo(EditText et_asunto_correo, EditText et_cuerpo_correo){

        String cuerpo = "De acuerdo a su amable solicitud y a las necesidades descritas, tenemos el agrado de someter a su consideración nuestra oferta técnico económica para el suministro de vehículos personales y utilitarios para distancias intermedias marca EZ-GO y CUSHMAN, de procedencia americana (U.S.A) y pertenecientes a la multinacional Textron Co."
                + "\n" + "\n" + "En Golf y Turf SAS, distribuidores autorizados para Colombia de estas marcas, agradecemos la oportunidad de presentarle esta oferta, esperando que se atractiva para ud y que sea una solución integral, de igual manera estaremos atentos a resolver cualquier duda que pueda surgir, nos puede contactar a través de los medios descritos a continuación.";
        et_cuerpo_correo.setText(cuerpo);
        String asunto = "Propuesta arrendamiento vehiculo EZ-GO";
        et_asunto_correo.setText(asunto);
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void cotizar_WebService(final String nombre, final String nit, final String celular, final String correo, final String ciudad, final String id_comercial, final String observaciones, final String asunto, final String cuerpo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion_arrendamiento.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Util.arrendamientos_vehiculos.clear();
                JSONObject jsonObject = null;
                alertDialog.dismiss();
                Toast.makeText(ArrendarVehiculoActivity.this, ""+response + "..... " + Util.arrendamientos_vehiculos.size(), Toast.LENGTH_SHORT).show();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(ArrendarVehiculoActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ArrendarVehiculoActivity.this,CotizacionesArrendamientosActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ArrendarVehiculoActivity.this, "El correo ingresado no es valido", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ArrendarVehiculoActivity.this, "je " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ArrendarVehiculoActivity.this, "e "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Util.cotizaciones_vehiculos.clear();
                    //Toast.makeText(ArrendarVehiculoActivity.this, "Creación exitosa, un email será enviado en breve.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ArrendarVehiculoActivity.this,CotizacionesArrendamientosActivity.class);
                    startActivity(intent);

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
                parametros.put("Numero_sub_cotizaciones", Util.arrendamientos_vehiculos.size()+"");
                for (int i = 0; i < Util.arrendamientos_vehiculos.size(); i++) {
                    parametros.put("Id_vehiculo"+i, Util.arrendamientos_vehiculos.get(i).getVehiculo().getId()+"");
                    parametros.put("Cantidad"+i, Util.arrendamientos_vehiculos.get(i).getCantidad());
                    Arrendamiento_Vehiculo arrendamiento = Util.arrendamientos_vehiculos.get(i);
                    for (int j = 0; j < arrendamiento.getPlazos().size() ; j++) {
                        parametros.put("plazo"+i+""+j, arrendamiento.getPlazos().get(j).getPlazo());
                        parametros.put("precio"+i+""+j, arrendamiento.getPlazos().get(j).getPrecio()+"");
                    }
                }
                return  parametros;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);

    }
}
