package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.net.Uri;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Cotizaciones_Arrendamientos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Arrendamiento_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Arrendamiento;
import android.primer.bryanalvarez.captura_info_gt.Models.Plazo;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class CotizacionesArrendamientosActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private FloatingActionButton floatingActionButtonReporte;
    private ListView listViewCotizaciones;
    private Cotizaciones_Arrendamientos_Adapter adapter;
    private ArrayList<Cotizacion_Arrendamiento> cotizaciones = new ArrayList<>();
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizaciones_arrendamientos);
        alertDialog_cargando = new AlertDialog.Builder(CotizacionesArrendamientosActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        request = Volley.newRequestQueue(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButtonReporte = (FloatingActionButton) findViewById(R.id.floatingActionButtonReporte);
        listViewCotizaciones = (ListView) findViewById(R.id.listViewCotizacionesArrendamientos);
        listViewCotizaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CotizacionesArrendamientosActivity.this,VerCotizacionArrendamientoActivity.class);
                Util.cotizacion_arrendamiento = cotizaciones.get(position);
                Util.arrendamientos_vehiculos = cotizaciones.get(position).getSubCotizaciones();
                startActivity(intent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CotizacionesArrendamientosActivity.this,ArrendamientoActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButtonReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://golfyturf.com/feria_automovil/AppWebServices/generar_informe_cotizaciones.php?Id_comercial="+Util.getId_usuario());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        traer_cotizaciones_WebService();

        registerForContextMenu(listViewCotizaciones);

    }

    private void traer_cotizaciones_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_cotizaciones_arrendamientos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(CotizacionesArrendamientosActivity.this, "r: " + response, Toast.LENGTH_SHORT).show();
                JSONArray jsonArray = null;
                Cotizacion_Arrendamiento cotizacion = null;
                ArrayList<Cotizacion_Arrendamiento> cotizaciones_WS = new ArrayList<>();

                String Id_cotizaion_general;
                String Numero;
                String Id_comercial;
                String Comercial;
                String Fecha;
                String Observaciones;

                String Id_cliente;
                String Nombre_cliente;
                String Cedula_nit_cliente;
                String Celular_cliente;
                String Correo_cliente;
                String Ciudad_cliente;

                String Id_sub_cotizacion;
                String Cantidad;
                String Id_vehiculo;
                int Precio_vehiculo;
                String Tipo_vehiculo;
                String Marca_vehiculo;
                String Modelo_vehiculo;
                String Motor_vehiculo;
                String Chasis_vehiculo;
                String Velocidad_vehiculo;
                String Capacidad_vehiculo;
                String Capacidad_carga_vehiculo;
                String Frenos_vehiculo;
                String Ancho_vehiculo;
                String Largo_vehiculo;
                String Peso_vehiculo;
                String Imagen_vehiculo_vehiculo;

                String Id_plazo;
                String Plazo;
                int Precio;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id_cotizaion_general = jsonArray.getJSONObject(i).getString("Id");
                        Numero = jsonArray.getJSONObject(i).getString("Numero");
                        Id_comercial = jsonArray.getJSONObject(i).getString("Id_empleado");
                        Comercial = jsonArray.getJSONObject(i).getString("Nombres");
                        Fecha = jsonArray.getJSONObject(i).getString("Fecha");
                        Observaciones = jsonArray.getJSONObject(i).getString("Observaciones");

                        Id_cliente = jsonArray.getJSONObject(i).getString("Id_cliente");
                        Nombre_cliente = jsonArray.getJSONObject(i).getString("Nombre");
                        Cedula_nit_cliente = jsonArray.getJSONObject(i).getString("Cedula/NIT");
                        Celular_cliente = jsonArray.getJSONObject(i).getString("Celular");
                        Correo_cliente = jsonArray.getJSONObject(i).getString("Correo");
                        Ciudad_cliente = jsonArray.getJSONObject(i).getString("Ciudad");

                        Cliente cliente = new Cliente(Id_cliente,Nombre_cliente,Cedula_nit_cliente,Celular_cliente,Correo_cliente,Ciudad_cliente);

                        Arrendamiento_Vehiculo subCotizacion = null;
                        Vehiculo vehiculo = null;
                        JSONArray jsonArraySubcotizaciones= null;
                        ArrayList<Arrendamiento_Vehiculo> subcotizaciones_WS = new ArrayList<>();

                        jsonArraySubcotizaciones = jsonArray.getJSONObject(i).getJSONArray("Sub_cotizaciones");
                        for (int j=0; j<jsonArraySubcotizaciones.length(); j++){

                            Id_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getString("Id_Arrendamiento_Vehiculo");
                            //Cambiar en WebService
                            Cantidad = jsonArraySubcotizaciones.getJSONObject(j).getString("Cantidad");
                            //Cantidad = "0";
                            Id_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Id_vehiculo");
                            Precio_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getInt("Precio");
                            Tipo_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Tipo");
                            Marca_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Marca");
                            Modelo_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Nombre_vehiculo");
                            Motor_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Motor");
                            Chasis_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Chasis");
                            Velocidad_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Velocidad");
                            Capacidad_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Capacidad");
                            Capacidad_carga_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Capacidad_carga");
                            Frenos_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Frenos");
                            Ancho_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Ancho");
                            Largo_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Largo");
                            Peso_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Peso");
                            Imagen_vehiculo_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Imagen_vehiculo");

                            vehiculo = new Vehiculo();

                            vehiculo.setId(Id_vehiculo);
                            vehiculo.setTipo(Tipo_vehiculo);
                            vehiculo.setMarca(Marca_vehiculo);
                            vehiculo.setModelo(Modelo_vehiculo);
                            vehiculo.setMotor(Motor_vehiculo);
                            vehiculo.setChasis(Chasis_vehiculo);
                            vehiculo.setVelocidad(Velocidad_vehiculo);
                            vehiculo.setCapacidad(Capacidad_vehiculo);
                            vehiculo.setCapacidad_carga(Capacidad_carga_vehiculo);
                            vehiculo.setFrenos(Frenos_vehiculo);
                            vehiculo.setAncho_vehiculo(Ancho_vehiculo);
                            vehiculo.setLargo_vehiculo(Largo_vehiculo);
                            vehiculo.setPeso(Peso_vehiculo);
                            vehiculo.setValor(Precio_vehiculo);
                            vehiculo.setImagen(Imagen_vehiculo_vehiculo);

                            Plazo plazo = null;
                            JSONArray jsonArrayPlazos = null;
                            ArrayList<Plazo> plazos_WS = new ArrayList<>();

                            jsonArrayPlazos = jsonArraySubcotizaciones.getJSONObject(j).getJSONArray("Plazos");
                            for (int k=0; k<jsonArrayPlazos.length(); k++){

                                Id_plazo = jsonArrayPlazos.getJSONObject(k).getString("Id_Plazo");
                                Plazo = jsonArrayPlazos.getJSONObject(k).getString("Plazo");
                                Precio = jsonArrayPlazos.getJSONObject(k).getInt("Precio");

                                plazo = new Plazo(Id_plazo,Plazo,Precio);
                                plazos_WS.add(plazo);
                            }

                            subCotizacion = new Arrendamiento_Vehiculo(Id_sub_cotizacion,vehiculo,Cantidad,plazos_WS);
                            subcotizaciones_WS.add(subCotizacion);

                        }

                        cotizacion = new Cotizacion_Arrendamiento(Id_cotizaion_general,Numero,Id_comercial,Fecha,Comercial,cliente,subcotizaciones_WS,Observaciones);
                        cotizaciones_WS.add(cotizacion);
                    }
                    adapter = new Cotizaciones_Arrendamientos_Adapter(CotizacionesArrendamientosActivity.this, cotizaciones_WS, R.layout.list_view_item_cotizaciones_arrendamientos);
                    listViewCotizaciones.setAdapter(adapter);
                    cotizaciones=cotizaciones_WS;
                    alertDialog_cargando.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(CotizacionesArrendamientosActivity.this, "e: "+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, "ve: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(CotizacionesArrendamientosActivity.this,ArrendamientoActivity.class);
        startActivity(intent);
    }
}
