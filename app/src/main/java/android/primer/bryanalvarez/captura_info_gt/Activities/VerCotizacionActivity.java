package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Maquinas_Cotizadas_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.Models.Contacto;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.SubCotizacion;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
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

public class VerCotizacionActivity extends AppCompatActivity {

    private AlertDialog alertDialog_cargando;
    private RecyclerView listViewMaquinasCotizadas;
    private TextView tv_nombre_cliente_resumen;
    private TextView tv_nombre_contacto_resumen;
    private TextView tv_total_cotizar_sin_iva_maquina_resumen;
    private TextView tv_total_cotizar_iva_maquina_resumen;
    private TextView tv_total_cotizar_maquina_resumen;
    private Button bt_guardar_enviar_cotizacion;
    private Maquinas_Cotizadas_Adapter maquinas_cotizadas_adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;
    private ArrayList<SubCotizacion> subCotizaciones= new ArrayList<>();
    private Cotizacion_Maquina cotizacion_maquina;
    RequestQueue request;
    StringRequest stringRequest;
    String idCliente;
    String idContacto;
    long precio_descuento=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cotizacion);

        bindU();

        request = Volley.newRequestQueue(this);

        cotizacion_maquina = Util.getCotizacion_maquina();

        idCliente = cotizacion_maquina.getId_cliente();
        idContacto = cotizacion_maquina.getId_contacto();

        tv_nombre_cliente_resumen.setText(cotizacion_maquina.getCliente());
        tv_nombre_contacto_resumen.setText(cotizacion_maquina.getContacto());
        subCotizaciones = cotizacion_maquina.getSubCotizaciones();
        sumarPrecios();
        maquinas_cotizadas_adapter = new Maquinas_Cotizadas_Adapter(this, subCotizaciones, R.layout.list_view_maquinas_cotizadas, new Maquinas_Cotizadas_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(SubCotizacion subCotizacion, int position) {
                Intent intent = new Intent(VerCotizacionActivity.this,CotizarMaquinaActivity.class);
                intent.putExtra("Id_maquina",subCotizacion.getId_modelo_maquina());
                intent.putExtra("Crear_Editar","Editar");
                Util.setSubCotizacion(subCotizacion);
                startActivity(intent);
            }
        });
        listViewMaquinasCotizadas.setAdapter(maquinas_cotizadas_adapter);

        bt_guardar_enviar_cotizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_cargando = new AlertDialog.Builder(VerCotizacionActivity.this).create();
                alertDialog_cargando.setMessage("Enviando datos");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
                crear_cotizacion_WebService();
            }
        });

    }

    private void sumarPrecios() {
        long valor = 0;
        long aumento_IVA = 0;
        long valor_final =0;
        for (int i = 0; i < subCotizaciones.size() ; i++) {
            valor += subCotizaciones.get(i).getValor();
            aumento_IVA += subCotizaciones.get(i).getValor_IVA();
            valor_final +=  subCotizaciones.get(i).getValor_total();
        }
        tv_total_cotizar_sin_iva_maquina_resumen.setText("$ "+ NumberFormat.getInstance().format(valor));
        tv_total_cotizar_iva_maquina_resumen.setText("$ "+NumberFormat.getInstance().format(aumento_IVA));
        tv_total_cotizar_maquina_resumen.setText("$ "+NumberFormat.getInstance().format(valor_final));
    }

    private void bindU() {

        bt_guardar_enviar_cotizacion = (Button) findViewById(R.id.bt_guardar_enviar_cotizacion);
        tv_nombre_cliente_resumen = (TextView) findViewById(R.id.tv_nombre_cliente_resumen);
        tv_nombre_contacto_resumen = (TextView) findViewById(R.id.tv_nombre_contacto_resumen);
        tv_total_cotizar_sin_iva_maquina_resumen = (TextView) findViewById(R.id.tv_total_cotizar_sin_iva_maquina_resumen);
        tv_total_cotizar_iva_maquina_resumen = (TextView) findViewById(R.id.tv_total_cotizar_iva_maquina_resumen);
        tv_total_cotizar_maquina_resumen = (TextView) findViewById(R.id.tv_total_cotizar_maquina_resumen);
        listViewMaquinasCotizadas = (RecyclerView) findViewById(R.id.listViewMaquinasCotizadas);
        listViewMaquinasCotizadas.setHasFixedSize(true);
        listViewMaquinasCotizadas.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listViewMaquinasCotizadas.setLayoutManager(mLayoutManager);
        listViewMaquinasCotizadas.addItemDecoration(itemDecoration);
    }


    private void crear_cotizacion_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion_maquina.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                Toast.makeText(VerCotizacionActivity.this, response, Toast.LENGTH_LONG).show();
                try {
                    jsonObject = new JSONObject(response);
                    Util.cotizaciones_maquinas.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(VerCotizacionActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerCotizacionActivity.this,CotizacionesMaquinariaActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(VerCotizacionActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(VerCotizacionActivity.this, "Creación exitosa, un email será enviado en breve.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerCotizacionActivity.this,CotizacionesMaquinariaActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                long valor_total = 0;
                parametros.put("Id_cliente", idCliente );
                parametros.put("Id_contacto", idContacto);
                parametros.put("Id_comercial", Util.getId_usuario() );
                parametros.put("Num_sub_cotizaciones", subCotizaciones.size()+ "" );
                for (int i=0; i<subCotizaciones.size(); i++){
                    SubCotizacion subCotizacion = subCotizaciones.get(i);
                    parametros.put("Id_modelo_maquina"+i, subCotizacion.getId_modelo_maquina() );
                    parametros.put("Valor"+i, subCotizacion.getValor()+"" );
                    valor_total += subCotizacion.getValor();
                    ArrayList<Componente> componentes = subCotizacion.getComponentes();
                    parametros.put("Num_componentes"+i, componentes.size()+ "" );
                    for (int j = 0; j <componentes.size() ; j++) {
                        Componente componente = componentes.get(j);
                        parametros.put("Id_componente"+i+j,componente.getId());
                        parametros.put("Nombre"+i+j,componente.getNombre());
                        parametros.put("Precio"+i+j,componente.getPrecio()+"");
                        parametros.put("IVA"+i+j,componente.getIVA()+"");
                        parametros.put("Valor_IVA"+i+j,componente.getValor_IVA() + "");
                        parametros.put("Precio_IVA"+i+j,componente.getPrecio_IVA() + "");
                        parametros.put("Descuento"+i+j,componente.getDescuento() + "");
                        parametros.put("Precio_descuento"+i+j,componente.getPrecio_descuento() + "");
                        parametros.put("Valor_IVA_descuento"+i+j,componente.getValor_IVA_descuento() + "");
                        parametros.put("Precio_IVA_descuento"+i+j,componente.getPrecio_IVA_descuento()+ "");
                        parametros.put("Cantidad"+i+j,componente.getCantidad()+ "");
                    }
                }
                parametros.put("Valor_total",valor_total+"");

                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerCotizacionActivity.this,CotizacionesMaquinariaActivity.class);
        startActivity(intent);
    }


}
