package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Cotizaciones_Repuestos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Repuesto;
import android.primer.bryanalvarez.captura_info_gt.Models.Producto;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CotizacionesRepuestosActivity extends AppCompatActivity {

    private FloatingActionButton fab_nueva_cotizacion_repuestos;
    private FloatingActionButton fab_reporte_cotizaciones_repuestos;
    private ListView listViewCotizacionesRepuestos;
    private Cotizaciones_Repuestos_Adapter adapter;
    private ArrayList<Cotizacion_Repuesto> cotizaciones_repuestos = new ArrayList<>();
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizaciones_repuestos);

        alertDialog_cargando = new AlertDialog.Builder(CotizacionesRepuestosActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        request = Volley.newRequestQueue(this);

        fab_nueva_cotizacion_repuestos = (FloatingActionButton) findViewById(R.id.fab_nueva_cotizacion_repuestos);
        fab_reporte_cotizaciones_repuestos = (FloatingActionButton) findViewById(R.id.fab_reporte_cotizaciones_repuestos);
        listViewCotizacionesRepuestos = (ListView) findViewById(R.id.listViewCotizacionesRepuestos);
        listViewCotizacionesRepuestos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CotizacionesRepuestosActivity.this,CotizarRepuestosActivity.class);
                intent.putExtra("editar_crear", "editar" );
                Util.setCotizacion_repuesto(cotizaciones_repuestos.get(position));
                startActivity(intent);
            }
        });
        fab_nueva_cotizacion_repuestos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CotizacionesRepuestosActivity.this,CotizarRepuestosActivity.class);
                intent.putExtra("editar_crear", "crear" );
                startActivity(intent);
            }
        });

        traer_cotizaciones_maquinaria_WebService();
    }

    private void traer_cotizaciones_maquinaria_WebService() {

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_cotizaciones_repuestos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Cotizacion_Repuesto cotizacion = null;
                ArrayList<Cotizacion_Repuesto> cotizaciones_WS = new ArrayList<>();

                String id_cotizacion;
                String idCliente;
                String idContacto;
                String numero;
                String nombreCliente;
                String fechaSolicitud;
                String descuento;
                String estado;
                String observaciones;

                String Id_producto;
                String Referencia;
                String Descripcion;
                String Stock;
                Long Precio;
                double IVA;
                String Marca;
                String Cantidad;
                int Descuento;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id_cotizacion = jsonArray.getJSONObject(i).getString("Id");
                        idCliente = jsonArray.getJSONObject(i).getString("Id_cliente");
                        idContacto = jsonArray.getJSONObject(i).getString("Id_contacto");
                        numero = jsonArray.getJSONObject(i).getString("Numero");
                        nombreCliente = jsonArray.getJSONObject(i).getString("Nombre_completo");
                        fechaSolicitud = jsonArray.getJSONObject(i).getString("Fecha_solicitud");
                        descuento = jsonArray.getJSONObject(i).getString("Descuento");
                        estado = jsonArray.getJSONObject(i).getString("Estado");
                        observaciones = jsonArray.getJSONObject(i).getString("Observaciones");

                        Producto producto = null;
                        JSONArray jsonArrayProductos= null;
                        ArrayList<Producto> productos_WS = new ArrayList<>();

                        jsonArrayProductos = jsonArray.getJSONObject(i).getJSONArray("Productos");
                        for (int j=0; j<jsonArrayProductos.length(); j++){

                            Id_producto = jsonArrayProductos.getJSONObject(j).getString("Id");
                            Referencia = jsonArrayProductos.getJSONObject(j).getString("Referencia");
                            Descripcion = jsonArrayProductos.getJSONObject(j).getString("Descripcion");
                            Stock = jsonArrayProductos.getJSONObject(j).getString("Stock");
                            Precio = jsonArrayProductos.getJSONObject(j).getLong("Precio_venta");
                            IVA = jsonArrayProductos.getJSONObject(j).getDouble("IVA");
                            Marca = jsonArrayProductos.getJSONObject(j).getString("Marca");
                            Cantidad = jsonArrayProductos.getJSONObject(j).getString("Cantidad");
                            Descuento = jsonArrayProductos.getJSONObject(j).getInt("Descuento");

                            producto = new Producto(Id_producto,Referencia,Descripcion,Stock,Precio,IVA,Marca,Descuento);
                            producto.setCantidad(Cantidad);
                            productos_WS.add(producto);
                        }
                        cotizacion = new Cotizacion_Repuesto(id_cotizacion,idCliente,idContacto,numero,nombreCliente,fechaSolicitud,descuento,estado,observaciones,productos_WS);
                        cotizaciones_WS.add(cotizacion);
                    }
                    adapter = new Cotizaciones_Repuestos_Adapter(CotizacionesRepuestosActivity.this,cotizaciones_WS,R.layout.list_view_item_cotizacion_repuesto);
                    listViewCotizacionesRepuestos.setAdapter(adapter);
                    cotizaciones_repuestos=cotizaciones_WS;
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
        super.onBackPressed();
        Intent intent = new Intent(CotizacionesRepuestosActivity.this,CotizarRepuestosActivity.class);
        intent.putExtra("editar_crear", "crear" );
        startActivity(intent);
    }
}
