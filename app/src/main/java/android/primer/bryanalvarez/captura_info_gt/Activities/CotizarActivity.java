package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Accesorio_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Accesorio_Vehiculo_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CotizarActivity extends AppCompatActivity {

    private RecyclerView listViewAccesorios;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;
    private Accesorio_Adapter adapter_accesorio;
    private Accesorio_Vehiculo_Adapter accesorio_vehiculo_adapter;
    private ArrayList<Accesorio> accesorios = new ArrayList<>();
    private ArrayList<Accesorio> accesorios_seleccionados = new ArrayList<>();
    private TextView tv_total_cotizar_sin_iva;
    private TextView tv_total_cotizar_iva;
    private TextView tv_total_cotizar;
    private ImageView iv_vehiculo_cotizar;
    private ImageView iv_marca;
    private Button bt_cotizar;
    private Cotizacion subCotizacion;

    private AlertDialog alertDialog;
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;
    long total = 0;
    long total_sin_iva = 0;
    long total_iva = 0;
    String format_total;
    String format_total_sin_iva;
    String format_total_iva;
    String editar_crear;
    String id_vehiculo = "";
    String cliente;
    String enviar_guardar = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizar);

        alertDialog = new AlertDialog.Builder(CotizarActivity.this).create();
        alertDialog.setMessage("Enviando datos...");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog_cargando = new AlertDialog.Builder(CotizarActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        editar_crear = getIntent().getStringExtra("editar_crear");
        id_vehiculo = getIntent().getStringExtra("id_vehiculo");

        request = Volley.newRequestQueue(this);

        bindUI();

        if (Util.getVehiculo().getMarca().equals("EZGO")){
            iv_marca.setImageResource(R.drawable.ezgo);
        }else if (Util.getVehiculo().getMarca().equals("CUSHMAN")){
            iv_marca.setImageResource(R.drawable.cushman);
        }

        listViewAccesorios.setHasFixedSize(true);
        listViewAccesorios.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listViewAccesorios.setLayoutManager(mLayoutManager);
        listViewAccesorios.addItemDecoration(itemDecoration);

        Picasso.with(this).load(Util.getVehiculo().getImagen()).into(iv_vehiculo_cotizar);
        traer_accesorios_WebService(id_vehiculo);

        registerForContextMenu(listViewAccesorios);

        if (editar_crear.equals("crear")){
            bt_cotizar.setText("CONTINUAR");
        }else if (editar_crear.equals("editar")){
            bt_cotizar.setText("ACTUALIZAR");
            subCotizacion = Util.getCotizacion();
        }


        bt_cotizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editar_crear.equals("crear")){
                    alertDialog_cargando = new AlertDialog.Builder(CotizarActivity.this).create();
                    alertDialog_cargando.setMessage("¿Desea agregar más vehiculos a la cotización?");
                    alertDialog_cargando.setCancelable(false);
                    alertDialog_cargando.setCanceledOnTouchOutside(false);
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CotizarActivity.this,VehiculosActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showAlertDatosCliente("Datos","Ingresa los datos del cliente");
                        }
                    });
                    alertDialog_cargando.show();
                    agregarSubCotizacion();
                }else if (editar_crear.equals("editar")){
                    alertDialog_cargando = new AlertDialog.Builder(CotizarActivity.this).create();
                    alertDialog_cargando.setMessage("¿Desea actualizar la cotización?");
                    alertDialog_cargando.setCancelable(false);
                    alertDialog_cargando.setCanceledOnTouchOutside(false);
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            agregarSubCotizacion();
                            Intent intent = new Intent(CotizarActivity.this,VerCotizacionVehiculosActivity.class);
                            startActivity(intent);
                        }
                    });
                    alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog_cargando.show();
                }
            }
        });

        total = Util.getVehiculo().getValor_IVA();
        format_total = NumberFormat.getInstance().format(total);
        tv_total_cotizar.setText("$ "+format_total);

        total_sin_iva = Util.getVehiculo().getValor();
        format_total_sin_iva = NumberFormat.getInstance().format(total_sin_iva);
        tv_total_cotizar_sin_iva.setText("$ "+format_total_sin_iva);

        total_iva = Util.getVehiculo().getAumento_IVA();
        format_total_iva = NumberFormat.getInstance().format(total_iva);
        tv_total_cotizar_iva.setText("$ "+format_total_iva);

    }

    private void agregarSubCotizacion() {
        Cotizacion subCotizacionAgregada;
        if (editar_crear.equals("crear")){
            subCotizacionAgregada = new Cotizacion((int) total_sin_iva,(int)total_iva, (int)total,Util.getVehiculo(),accesorios_seleccionados);
            Util.cotizaciones_vehiculos.add(subCotizacionAgregada);
        }else if (editar_crear.equals("editar")){
            subCotizacionAgregada = new Cotizacion(subCotizacion.getId(), (int) total_sin_iva,(int)total_iva, (int)total,Util.getVehiculo(),accesorios_seleccionados);
            Util.getCotizacion_general_vehiculo().getSubCotizaciones().remove(subCotizacion);
            Util.getCotizacion_general_vehiculo().getSubCotizaciones().add(subCotizacionAgregada);
        }
    }


    private ArrayList<Accesorio> bindAccesoriosSelect(ArrayList<Accesorio> accesorios) {
        ArrayList<Accesorio> accesoriosUP = accesorios;
        ArrayList<Accesorio> accesorios_cot = Util.getCotizacion().getAccesorios_adicionados();
        int ocunt = 0;
        for (int i=0; i<accesoriosUP.size(); i++){
            for (int j=0; j<accesorios_cot.size(); j++){
                if (accesorios.get(i).getId().equals(accesorios_cot.get(j).getId())){
                    accesoriosUP.get(i).setCheck(true);
                    ocunt++;
                    sumarRestarTotalAccesorios(true,accesoriosUP.get(i));
                }
            }
        }
        //Toast.makeText(this, "count: " + ocunt, Toast.LENGTH_SHORT).show();

        return accesoriosUP;
    }

    private void bindUI() {

        iv_vehiculo_cotizar = (ImageView) findViewById(R.id.iv_vehiculo_cotizar);
        iv_marca = (ImageView) findViewById(R.id.iv_marca);
        tv_total_cotizar = (TextView) findViewById(R.id.tv_total_cotizar);
        tv_total_cotizar_sin_iva = (TextView) findViewById(R.id.tv_total_cotizar_sin_iva);
        tv_total_cotizar_iva = (TextView) findViewById(R.id.tv_total_cotizar_iva);
        listViewAccesorios = (RecyclerView) findViewById(R.id.listViewAccesorios);
        bt_cotizar = (Button) findViewById(R.id.bt_cotizar);
    }

    private void traer_accesorios_WebService(final String id_vehiculo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_accesorios.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Accesorio accesorio= null;
                ArrayList<Accesorio> accesorios_WS = new ArrayList<>();

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

                        Precio = (int) (Precio * Util.monedaActual);
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

                        accesorio = new Accesorio();

                        accesorio.setId(Id);
                        accesorio.setReferencia(Referencia);
                        accesorio.setValor(Precio);
                        accesorio.setAumento_IVA(Aumento_IVA);
                        accesorio.setPrecio_IVA(Precio_IVA);
                        accesorio.setCheck(false);

                        accesorios_WS.add(accesorio);
                    }

                    accesorio_vehiculo_adapter = new Accesorio_Vehiculo_Adapter(CotizarActivity.this, accesorios_WS, R.layout.list_view_item_accesorio_vehiculo, new Accesorio_Vehiculo_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Accesorio accesorio, int position) {

                        }
                    }, new Accesorio_Vehiculo_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Accesorio accesorio, int position, boolean isChecked) {
                            if(isChecked){
                                sumarRestarTotalAccesorios(isChecked,accesorio);
                            }else{
                                sumarRestarTotalAccesorios(isChecked,accesorio);
                            }
                        }
                    });

                    listViewAccesorios.setAdapter(accesorio_vehiculo_adapter);
                    alertDialog_cargando.dismiss();
                    if (editar_crear.equals("editar")){
                        accesorios_WS = bindAccesoriosSelect(accesorios_WS);
                    }
                    accesorio_vehiculo_adapter.notifyDataSetChanged();
                    accesorios=accesorios_WS;

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
                parametros.put("id_vehiculo", id_vehiculo);
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    public void sumarRestarTotalAccesorios(Boolean isChecked, Accesorio accesorio){
        if (isChecked){
            total += accesorio.getPrecio_IVA();
            format_total = NumberFormat.getInstance().format(total);
            tv_total_cotizar.setText("$ "+format_total);

            total_sin_iva += accesorio.getValor();
            format_total_sin_iva = NumberFormat.getInstance().format(total_sin_iva);
            tv_total_cotizar_sin_iva.setText("$ "+format_total_sin_iva);

            total_iva += accesorio.getAumento_IVA();
            format_total_iva = NumberFormat.getInstance().format(total_iva);
            tv_total_cotizar_iva.setText("$ "+format_total_iva);

            accesorios_seleccionados.add(accesorio);
            //Toast.makeText(CotizarActivity.this, total+"",Toast.LENGTH_SHORT).show();
        }else{
            total -= accesorio.getPrecio_IVA();
            format_total = NumberFormat.getInstance().format(total);
            tv_total_cotizar.setText("$ "+format_total);

            total_sin_iva -= accesorio.getValor();
            format_total_sin_iva = NumberFormat.getInstance().format(total_sin_iva);
            tv_total_cotizar_sin_iva.setText("$ "+format_total_sin_iva);

            total_iva -= accesorio.getAumento_IVA();
            format_total_iva = NumberFormat.getInstance().format(total_iva);
            tv_total_cotizar_iva.setText("$ "+format_total_iva);

            accesorios_seleccionados.remove(accesorio);
            //Toast.makeText(CotizarActivity.this, total+"",Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDatosCliente(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_datos_cliente, null);
        builder.setView(viewInflated);

        final EditText et_dialog_nombre_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_cliente);
        final EditText et_dialog_celular_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_celular_cliente);
        final EditText et_dialog_correo_cliente = (EditText) viewInflated.findViewById(R.id.et_dialog_correo_cliente);
        final EditText et_dialog_ciudad_observaciones = (EditText) viewInflated.findViewById(R.id.et_dialog_ciudad_observaciones);
        final ImageButton ib_dialog_cargar_cliente = (ImageButton) viewInflated.findViewById(R.id.ib_dialog_cargar_cliente);
        final Button bt_dialog_enviar_cotizacion = (Button) viewInflated.findViewById(R.id.bt_dialog_enviar_cotizacion);
        final CheckBox cb_tratamiento_datos= (CheckBox) viewInflated.findViewById(R.id.cb_tratamiento_datos);
        final TextView tv_interes_cliente = (TextView) viewInflated.findViewById(R.id.tv_interes_cliente);
        final SeekBar seekBar_interes_cliente = (SeekBar) viewInflated.findViewById(R.id.seekBar_interes_cliente);
        seekBar_interes_cliente.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_interes_cliente.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        bt_dialog_enviar_cotizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_dialog_nombre_cliente.getText().toString();
                cliente = nombre;
                String celular = et_dialog_celular_cliente.getText().toString();
                String correo = et_dialog_correo_cliente.getText().toString().replace(" ","");
                String observaciones = et_dialog_ciudad_observaciones.getText().toString();
                String id_vehiculo = Util.getVehiculo().getId();
                String valor = total+"";
                String id_comercial = Util.getId_usuario();
                String numero_acc = accesorios_seleccionados.size()+"";
                String interes = tv_interes_cliente.getText().toString();
                if(cb_tratamiento_datos.isChecked()){
                    if (correo.isEmpty()){
                        Toast.makeText(CotizarActivity.this,"Llene el campo Correo",Toast.LENGTH_SHORT).show();
                    }else{
                        if (!validarEmail(correo)){
                            Toast.makeText(CotizarActivity.this,"Email no valido",Toast.LENGTH_SHORT).show();
                            et_dialog_correo_cliente.setError("Email no válido");
                        }else {
                            dialog.dismiss();
                            confirmarEnvio(nombre, celular, correo, id_vehiculo, valor, id_comercial, numero_acc, observaciones, interes);


                        }
                    }
                }else{
                    Toast.makeText(CotizarActivity.this,"Por favor apruebe el tratamiento de datos",Toast.LENGTH_SHORT).show();
                }

            }
        });

        ib_dialog_cargar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertCargarCliente(et_dialog_nombre_cliente, et_dialog_celular_cliente, et_dialog_correo_cliente, seekBar_interes_cliente);
            }
        });


    }

    private void showAlertCargarCliente(final EditText et_dialog_nombre_cliente, final EditText et_dialog_celular_cliente, final EditText et_dialog_correo_cliente, final SeekBar seekBar_interes_cliente) {

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
                et_dialog_celular_cliente.setText(cliente_selec.getCelular());
                et_dialog_correo_cliente.setText(cliente_selec.getCorreo());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    seekBar_interes_cliente.setProgress(cliente_selec.getInteres(),true);
                }else{
                    seekBar_interes_cliente.setProgress(cliente_selec.getInteres());
                }
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
                int interes;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        nombre = jsonArray.getJSONObject(i).getString("Nombre");
                        cedula_nit = jsonArray.getJSONObject(i).getString("Cedula/NIT");
                        celular = jsonArray.getJSONObject(i).getString("Celular");
                        correo = jsonArray.getJSONObject(i).getString("Correo");
                        ciudad = jsonArray.getJSONObject(i).getString("Ciudad");
                        interes = jsonArray.getJSONObject(i).getInt("Nivel_interes");

                        cliente = new Cliente(id,nombre,cedula_nit,celular,correo,ciudad);
                        cliente.setInteres(interes);
                        clientes.add(cliente);

                    }
                    spinner.setAdapter(new ArrayAdapter<Cliente>(CotizarActivity.this, android.R.layout.simple_list_item_1, clientes));

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

    private void showAlertDetallesCorreo(final String nombre, final String celular, final String correo, String id_vehiculo, final String valor, final String id_comercial, final String numero_acc, final String observaciones, final String interes) {

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
                cotizar_WebService(nombre, celular, correo, CotizarActivity.this.id_vehiculo, valor, id_comercial, numero_acc, observaciones, asunto, cuerpo, interes);
                alertDialog = new AlertDialog.Builder(CotizarActivity.this).create();
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
                + "\n" + "\n" + "En Golf y Turf SAS, distribuidores autorizados para Colombia de estas marcas, agradecemos la oportunidad de presentarle esta oferta, esperando que se atractiva para usted, de igual manera estaremos atentos a resolver cualquier duda que pueda surgir, nos puede contactar a través de los medios descritos a continuación.";
        et_cuerpo_correo.setText(cuerpo);
        String asunto = "Propuesta vehiculo EZ-GO/CUSHMAN";
        et_asunto_correo.setText(asunto);
    }

    private void confirmarEnvio(final String nombre, final String celular, final String correo, final String id_vehiculo, final String valor, final String id_comercial, final String numero_acc, final String observaciones, final String interes){
        AlertDialog alertDialog = new AlertDialog.Builder(CotizarActivity.this).create();
        alertDialog.setMessage("¿Desea solo guardar la cotización o enviarla ahora?");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviar_guardar = "2";
                showAlertDetallesCorreo(nombre, celular, correo, id_vehiculo, valor, id_comercial, numero_acc, observaciones, interes);
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviar_guardar = "1";
                cotizar_WebService(nombre, celular, correo, CotizarActivity.this.id_vehiculo, valor, id_comercial, numero_acc, observaciones, "", "", interes);
                alertDialog_cargando = new AlertDialog.Builder(CotizarActivity.this).create();
                alertDialog_cargando.setMessage("Guardando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });
        alertDialog.show();

    }

    private void cotizar_WebService(final String nombre, final String celular, final String correo, final String id_vehiculo, final String valor, final String id_comercial, final String numero_acc, final String observaciones, final String asunto, final String cuerpo, final String interes) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                Toast.makeText(CotizarActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_vehiculos.clear();
                    if(jsonObject.getString("Success").equals("true")){

                        Toast.makeText(CotizarActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CotizarActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(CotizarActivity.this, "El correo ingresado no es valido", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Util.cotizaciones_vehiculos.clear();
                    Log.w("ERROR_J",e.getMessage() + " ---- " + response);
                    Toast.makeText(CotizarActivity.this, "Error J" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Intent intent = new Intent(CotizarActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Util.cotizaciones_vehiculos.clear();
                    Toast.makeText(CotizarActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CotizarActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Util.cotizaciones_vehiculos.clear();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Enviar_guardar", enviar_guardar);
                parametros.put("Numero_cot", " ");
                parametros.put("Nombre_cliente", nombre);
                parametros.put("Celular_cliente", celular);
                parametros.put("Correo_cliente", correo);
                parametros.put("Id_comercial", id_comercial);
                parametros.put("Observaciones", observaciones);
                parametros.put("Interes", interes);
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
                    parametros.put("Valor_Vehiculo_sin_descuento"+j, Util.cotizaciones_vehiculos.get(j).getVehiculo().getValor_sin_descuento()+"");
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

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        if (editar_crear.equals("crear")){
            Intent intent = new Intent(CotizarActivity.this,VehiculosActivity.class);
            startActivity(intent);
        }else if (editar_crear.equals("editar")){
            Intent intent = new Intent(CotizarActivity.this,VerCotizacionVehiculosActivity.class);
            startActivity(intent);
        }
    }
}
