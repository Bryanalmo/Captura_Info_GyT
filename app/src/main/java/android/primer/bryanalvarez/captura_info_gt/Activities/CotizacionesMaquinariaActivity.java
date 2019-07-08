package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Cotizaciones_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Cotizaciones_Maquinas_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.SubCotizacion;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CotizacionesMaquinariaActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private FloatingActionButton fab_nueva_cotizacion;
    private FloatingActionButton fab_reporte_cotizaciones_maquinaria;
    private ListView listViewCotizacionesMaquinaria;
    private Cotizaciones_Maquinas_Adapter adapter;
    private ArrayList<Cotizacion_Maquina> cotizaciones_maquinas = new ArrayList<>();
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizaciones_maquinaria);

        alertDialog_cargando = new AlertDialog.Builder(CotizacionesMaquinariaActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        request = Volley.newRequestQueue(this);

        fab_nueva_cotizacion = (FloatingActionButton) findViewById(R.id.fab_nueva_cotizacion);
        fab_reporte_cotizaciones_maquinaria = (FloatingActionButton) findViewById(R.id.fab_reporte_cotizaciones_maquinaria);
        listViewCotizacionesMaquinaria = (ListView) findViewById(R.id.listViewCotizacionesMaquinaria);
        listViewCotizacionesMaquinaria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Util.setCotizacion_maquina(cotizaciones_maquinas.get(position));
                Intent intent = new Intent(CotizacionesMaquinariaActivity.this,VerCotizacionActivity.class);
                startActivity(intent);
            }
        });
        fab_nueva_cotizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CotizacionesMaquinariaActivity.this,MaquinasActivity.class);
                startActivity(intent);
            }
        });

        fab_reporte_cotizaciones_maquinaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://golfyturf.com/feria_automovil/AppWebServices/generar_informe_cotizaciones_maquinaria.php?Id_comercial="+Util.getId_usuario());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        traer_cotizaciones_maquinaria_WebService();

        registerForContextMenu(listViewCotizacionesMaquinaria);
    }

    private void traer_cotizaciones_maquinaria_WebService() {

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_cotizaciones_maquinaria.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Cotizacion_Maquina cotizacion = null;
                ArrayList<Cotizacion_Maquina> cotizaciones_WS = new ArrayList<>();

                String Id_cotizacion;
                String Numero;
                String Fecha;
                String Id_cliente;
                String Id_contacto;
                String Id_comercial;
                long Valor;
                String Nombre_completo;
                String Nombre_contacto;
                String Nombre_comercial;

                String Id_sub_cotizacion;
                String Id_modelo_maquina;
                String Imagen_equipo;
                long Valor_sub_cotizacion;
                long Valor_IVA_sub_cotizacion;
                long Valor_total_sub_cotizacion;
                String Modelo_maquina;

                String Id_componente;
                String Nombre;
                long Precio;
                double IVA;
                long Valor_IVA;
                long Precio_IVA;
                long Descuento;
                long Precio_descuento;
                long Valor_IVA_descuento;
                long Precio_IVA_descuento;
                int Cantidad;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id_cotizacion = jsonArray.getJSONObject(i).getString("Id");
                        Numero = jsonArray.getJSONObject(i).getString("Numero");
                        Fecha = jsonArray.getJSONObject(i).getString("Fecha");
                        Id_cliente = jsonArray.getJSONObject(i).getString("Id_cliente");
                        Id_contacto = jsonArray.getJSONObject(i).getString("Id_contacto");
                        Id_comercial = jsonArray.getJSONObject(i).getString("Id_comercial");
                        Valor = jsonArray.getJSONObject(i).getLong("Valor");
                        Nombre_completo = jsonArray.getJSONObject(i).getString("Nombre_completo");
                        Nombre_contacto = jsonArray.getJSONObject(i).getString("Nombre_contacto");
                        Nombre_comercial = jsonArray.getJSONObject(i).getString("Nombre_comercial");

                        SubCotizacion subCotizacion = null;
                        JSONArray jsonArraySubcotizaciones= null;
                        ArrayList<SubCotizacion> subcotizaciones_WS = new ArrayList<>();

                        jsonArraySubcotizaciones = jsonArray.getJSONObject(i).getJSONArray("Sub_cotizaciones");
                        for (int j=0; j<jsonArraySubcotizaciones.length(); j++){

                            Id_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getString("Id");
                            Id_modelo_maquina = jsonArraySubcotizaciones.getJSONObject(j).getString("Id_modelo_maquina");
                            Imagen_equipo = jsonArraySubcotizaciones.getJSONObject(j).getString("Imagen_equipo");
                            Valor_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getLong("Valor_sin_IVA");
                            Valor_IVA_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getLong("IVA_total");
                            Valor_total_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getLong("Valor");
                            Modelo_maquina = jsonArraySubcotizaciones.getJSONObject(j).getString("Modelo_maquina");
                            Componente componente = null;
                            JSONArray jsonArrayComponentes= null;
                            ArrayList<Componente> componentes_WS = new ArrayList<>();
                            jsonArrayComponentes = jsonArraySubcotizaciones.getJSONObject(j).getJSONArray("Componentes");
                            for (int k = 0; k < jsonArrayComponentes.length() ; k++) {

                                Id_componente = jsonArrayComponentes.getJSONObject(k).getString("Id_componente");
                                Descuento = jsonArrayComponentes.getJSONObject(k).getLong("Descuento");
                                Nombre = jsonArrayComponentes.getJSONObject(k).getString("Nombre");
                                Precio = jsonArrayComponentes.getJSONObject(k).getLong("Precio");
                                IVA = jsonArrayComponentes.getJSONObject(k).getDouble("IVA");
                                Cantidad = jsonArrayComponentes.getJSONObject(k).getInt("Cantidad");
                                Valor_IVA = (long) (Precio*IVA);
                                Precio_IVA = Precio + Valor_IVA;
                                Precio_descuento = Precio - Descuento;
                                Valor_IVA_descuento = (long) (Precio_descuento*IVA);
                                Precio_IVA_descuento = Precio_descuento + Valor_IVA_descuento;

                                componente = new Componente(Id_componente,Nombre,Precio,IVA,Valor_IVA,Precio_IVA,Descuento,Precio_descuento,Valor_IVA_descuento,Precio_IVA_descuento,Cantidad);
                                componentes_WS.add(componente);
                            }

                            subCotizacion = new SubCotizacion(Id_sub_cotizacion,Id_modelo_maquina,Modelo_maquina,Valor_sub_cotizacion,Valor_IVA_sub_cotizacion,Valor_total_sub_cotizacion,componentes_WS,Imagen_equipo);
                            subcotizaciones_WS.add(subCotizacion);
                        }
                        cotizacion = new Cotizacion_Maquina(Id_cotizacion,Numero,Id_cliente,Nombre_completo,Id_contacto,Nombre_contacto,Id_comercial,Nombre_comercial,Valor,subcotizaciones_WS);
                        cotizaciones_WS.add(cotizacion);
                    }
                    adapter = new Cotizaciones_Maquinas_Adapter(CotizacionesMaquinariaActivity.this, cotizaciones_WS, R.layout.list_view_item_cotizacion_maquinaria);
                    listViewCotizacionesMaquinaria.setAdapter(adapter);
                    cotizaciones_maquinas=cotizaciones_WS;
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
                parametros.put("Id_comercial", Util.getId_usuario());
                return  parametros;
            }
        };
        request.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CotizacionesMaquinariaActivity.this,MaquinasActivity.class);
        startActivity(intent);
    }
}
