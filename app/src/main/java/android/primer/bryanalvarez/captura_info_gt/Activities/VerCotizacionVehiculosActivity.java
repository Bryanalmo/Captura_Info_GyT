package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Vehiculos_Cotizados_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_General_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerCotizacionVehiculosActivity extends AppCompatActivity {

    private AlertDialog alertDialog_cargando;
    private RecyclerView listViewVehiculosCotizados;
    private TextView tv_nombre_cliente_resumen_cot_vehiculo;
    private TextView tv_nombre_comercial_resumen_cot_vehiculo;
    private TextView tv_total_cotizar_sin_iva_resumen_cot_vehiculo;
    private TextView tv_total_cotizar_iva_resumen_cot_vehiculo;
    private TextView tv_total_cotizar_resumen_cot_vehiculo;
    private Button bt_guardar_enviar_cotizacion_resumen_cot_vehiculo;
    private Vehiculos_Cotizados_Adapter vehiculos_cotizados_adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;
    private ArrayList<Cotizacion> subCotizaciones= new ArrayList<>();
    private Cotizacion_General_Vehiculo cotizacion_general_vehiculo;
    RequestQueue request;
    StringRequest stringRequest;
    String idCliente;
    long precio_descuento=0;
    String enviar_guardar = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cotizacion_vehiculos);
        bindUI();

        cotizacion_general_vehiculo = Util.getCotizacion_general_vehiculo();

        request = Volley.newRequestQueue(this);

        idCliente = cotizacion_general_vehiculo.getCliente().getId();
        tv_nombre_cliente_resumen_cot_vehiculo.setText(cotizacion_general_vehiculo.getCliente().getNombre());
        tv_nombre_comercial_resumen_cot_vehiculo.setText(cotizacion_general_vehiculo.getComercial());
        subCotizaciones = cotizacion_general_vehiculo.getSubCotizaciones();
        sumarPrecios();
        vehiculos_cotizados_adapter = new Vehiculos_Cotizados_Adapter(this, subCotizaciones, R.layout.list_view_vehiculos_cotizados, new Vehiculos_Cotizados_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cotizacion subCotizacion, int position) {
                Intent intent = new Intent(VerCotizacionVehiculosActivity.this,CotizarActivity.class);
                intent.putExtra("editar_crear","editar");
                intent.putExtra("id_vehiculo",subCotizaciones.get(position).getVehiculo().getId());
                Util.setCotizacion(subCotizacion);
                Util.setVehiculo(subCotizaciones.get(position).getVehiculo());
                //Toast.makeText(VerCotizacionVehiculosActivity.this, "sze:" + subCotizacion.getAccesorios_adicionados().size(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        listViewVehiculosCotizados.setAdapter(vehiculos_cotizados_adapter);

        bt_guardar_enviar_cotizacion_resumen_cot_vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cotizacion_general_vehiculo.getId_estado_envio().equals("1")){
                    enviar_guardar = "2";
                    showAlertDetallesCorreo();
                }else{
                    confirmarEnvio();
                }
            }
        });


    }

    private void sumarPrecios() {
        long valor = 0;
        long aumento_IVA = 0;
        long valor_final =0;
        for (int i = 0; i < subCotizaciones.size() ; i++) {
            valor += subCotizaciones.get(i).getValor_sin_iva();
            aumento_IVA += subCotizaciones.get(i).getValor_iva();
            valor_final +=  subCotizaciones.get(i).getValor();
        }
        tv_total_cotizar_sin_iva_resumen_cot_vehiculo.setText("$ "+ NumberFormat.getInstance().format(valor));
        tv_total_cotizar_iva_resumen_cot_vehiculo.setText("$ "+NumberFormat.getInstance().format(aumento_IVA));
        tv_total_cotizar_resumen_cot_vehiculo.setText("$ "+NumberFormat.getInstance().format(valor_final));
    }

    private void bindUI() {
        listViewVehiculosCotizados = (RecyclerView) findViewById(R.id.listViewVehiculosCotizados);
        tv_nombre_cliente_resumen_cot_vehiculo = (TextView) findViewById(R.id.tv_nombre_cliente_resumen_cot_vehiculo);
        tv_nombre_comercial_resumen_cot_vehiculo = (TextView) findViewById(R.id.tv_nombre_comercial_resumen_cot_vehiculo);
        tv_total_cotizar_sin_iva_resumen_cot_vehiculo = (TextView) findViewById(R.id.tv_total_cotizar_sin_iva_resumen_cot_vehiculo);
        tv_total_cotizar_iva_resumen_cot_vehiculo = (TextView) findViewById(R.id.tv_total_cotizar_iva_resumen_cot_vehiculo);
        tv_total_cotizar_resumen_cot_vehiculo = (TextView) findViewById(R.id.tv_total_cotizar_resumen_cot_vehiculo);
        bt_guardar_enviar_cotizacion_resumen_cot_vehiculo = (Button) findViewById(R.id.bt_guardar_enviar_cotizacion_resumen_cot_vehiculo);
        listViewVehiculosCotizados.setHasFixedSize(true);
        listViewVehiculosCotizados.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listViewVehiculosCotizados.setLayoutManager(mLayoutManager);
        listViewVehiculosCotizados.addItemDecoration(itemDecoration);
    }

    private void confirmarEnvio(){
        AlertDialog alertDialog = new AlertDialog.Builder(VerCotizacionVehiculosActivity.this).create();
        alertDialog.setMessage("¿Desea solo guardar la cotización o enviarla ahora?");
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviar_guardar = "2";
                showAlertDetallesCorreo();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enviar_guardar = "1";
                cotizar_WebService("","");
                alertDialog_cargando = new AlertDialog.Builder(VerCotizacionVehiculosActivity.this).create();
                alertDialog_cargando.setMessage("Guardando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });
        alertDialog.show();

    }

    private void showAlertDetallesCorreo() {

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
                cotizar_WebService(asunto, cuerpo);
                alertDialog_cargando = new AlertDialog.Builder(VerCotizacionVehiculosActivity.this).create();
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
                + "\n" + "\n" + "En Golf y Turf SAS, distribuidores autorizados para Colombia de estas marcas, agradecemos la oportunidad de presentarle esta oferta, esperando que se atractiva para usted, de igual manera estaremos atentos a resolver cualquier duda que pueda surgir, nos puede contactar a través de los medios descritos a continuación.";
        et_cuerpo_correo.setText(cuerpo);
        String asunto = "Propuesta vehiculo EZ-GO/CUSHMAN";
        et_asunto_correo.setText(asunto);
    }


    private void cotizar_WebService(final String asunto, final String cuerpo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){

                        Toast.makeText(VerCotizacionVehiculosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerCotizacionVehiculosActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(VerCotizacionVehiculosActivity.this, "El correo ingresado no es valido", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Util.cotizaciones_vehiculos.clear();
                    Log.w("ERROR_J",e.getMessage() + " ---- " + response);
                    Toast.makeText(VerCotizacionVehiculosActivity.this, "Error J" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Intent intent = new Intent(VerCotizacionVehiculosActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Util.cotizaciones_vehiculos.clear();
                    Toast.makeText(VerCotizacionVehiculosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerCotizacionVehiculosActivity.this,MainActivity.class);
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
                parametros.put("Numero_cot", cotizacion_general_vehiculo.getNumero());
                parametros.put("Nombre_cliente", cotizacion_general_vehiculo.getCliente().getNombre());
                parametros.put("Celular_cliente", cotizacion_general_vehiculo.getCliente().getCelular());
                parametros.put("Correo_cliente", cotizacion_general_vehiculo.getCliente().getCorreo());
                parametros.put("Id_comercial", cotizacion_general_vehiculo.getId_comercial());
                parametros.put("Observaciones", cotizacion_general_vehiculo.getObservaciones());
                parametros.put("Interes", cotizacion_general_vehiculo.getCliente().getInteres()+"");
                parametros.put("Asunto", asunto );
                parametros.put("Cuerpo", cuerpo );
                parametros.put("Numero_sub_cotizaciones", Util.cotizaciones_vehiculos.size()+"");
                parametros.put("Moneda", Util.monedaActual+"");
                long valor_total = 0;
                for (int j = 0; j < Util.cotizaciones_vehiculos.size(); j++) {
                    parametros.put("Id_sub_cotizacion"+j, Util.cotizaciones_vehiculos.get(j).getId());
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(stringRequest);

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerCotizacionVehiculosActivity.this,MainActivity.class);
        startActivity(intent);
        Util.cotizaciones_vehiculos.clear();
    }
}
