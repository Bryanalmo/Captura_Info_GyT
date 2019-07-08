package android.primer.bryanalvarez.captura_info_gt.Activities;

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
import android.view.View;
import android.widget.Button;
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
                alertDialog_cargando = new AlertDialog.Builder(VerCotizacionVehiculosActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
                cotizar_WebService();
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

    private void cotizar_WebService() {
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
                    e.printStackTrace();
                    Toast.makeText(VerCotizacionVehiculosActivity.this, "e: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(VerCotizacionVehiculosActivity.this, "Creación exitosa, un email será enviado en breve.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerCotizacionVehiculosActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put("Nombre_cliente", cotizacion_general_vehiculo.getCliente().getNombre());
                parametros.put("Nit_cliente", cotizacion_general_vehiculo.getCliente().getCedula_nit());
                parametros.put("Celular_cliente", cotizacion_general_vehiculo.getCliente().getCelular());
                parametros.put("Correo_cliente", cotizacion_general_vehiculo.getCliente().getCorreo());
                parametros.put("Ciudad_cliente", cotizacion_general_vehiculo.getCliente().getCiudad());
                parametros.put("Id_comercial", cotizacion_general_vehiculo.getId_comercial());
                parametros.put("Observaciones", cotizacion_general_vehiculo.getObservaciones());
                parametros.put("Numero_sub_cotizaciones", Util.cotizaciones_vehiculos.size()+"");
                long valor_total = 0;
                for (int j = 0; j < Util.cotizaciones_vehiculos.size(); j++) {
                    parametros.put("Id_vehiculo"+j, Util.cotizaciones_vehiculos.get(j).getVehiculo().getId());
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
    }
}
