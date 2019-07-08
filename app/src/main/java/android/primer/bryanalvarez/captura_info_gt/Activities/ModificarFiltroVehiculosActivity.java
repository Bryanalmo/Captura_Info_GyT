package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Vehiculo_Filtro_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;
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

public class ModificarFiltroVehiculosActivity extends AppCompatActivity {

    private RecyclerView listViewFiltroVehiculos;
    private Button btn_actualizar_filtro_vehiculos;
    private Vehiculo_Filtro_Adapter adapter_vehiculos;
    private ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    private ArrayList<Vehiculo> vehiculos_en_filtro = new ArrayList<>();
    private AlertDialog alertDialog_cargando;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;


    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_filtro_vehiculos_);

        request = Volley.newRequestQueue(this);

        listViewFiltroVehiculos = (RecyclerView) findViewById(R.id.listViewFiltroVehiculos);
        btn_actualizar_filtro_vehiculos = (Button) findViewById(R.id.btn_actualizar_filtro_vehiculos);

        listViewFiltroVehiculos.setHasFixedSize(true);
        listViewFiltroVehiculos.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        listViewFiltroVehiculos.setLayoutManager(mLayoutManager);

        traer_vehiculos_en_filtro_WebService();

        alertDialog_cargando = new AlertDialog.Builder(ModificarFiltroVehiculosActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        btn_actualizar_filtro_vehiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar_filtro_vehiculos_WebService();
                alertDialog_cargando = new AlertDialog.Builder(ModificarFiltroVehiculosActivity.this).create();
                alertDialog_cargando.setMessage("Cargando...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });



    }

    private void actualizar_filtro_vehiculos_WebService() {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/modificar_filtro_vehiculos.php";
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
                        Toast.makeText(ModificarFiltroVehiculosActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ModificarFiltroVehiculosActivity.this,VehiculosActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ModificarFiltroVehiculosActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(ModificarFiltroVehiculosActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ModificarFiltroVehiculosActivity.this,VehiculosActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("Numero_vehiculos",vehiculos.size()+"");
                for (int i=0; i<vehiculos.size(); i++){
                    parametros.put("Id_vehiculo"+i,vehiculos.get(i).getId());
                    parametros.put("Vehiculo_agregado"+i,vehiculos.get(i).isAgregado()+"");
                }
                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);

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
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

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

                        vehiculos.add(vehiculo);
                    }
                    vehiculos = bindVehiculosEnFiltro(vehiculos);
                    adapter_vehiculos = new Vehiculo_Filtro_Adapter(ModificarFiltroVehiculosActivity.this, vehiculos, R.layout.list_view_item_filtro_vehiculos, new Vehiculo_Filtro_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Vehiculo vehiculo, int position) {

                        }
                    }, new Vehiculo_Filtro_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Vehiculo vehiculo, int position, boolean isChecked) {
                            if(isChecked){
                                vehiculo.setAgregado(true);
                            }else{
                                vehiculo.setAgregado(false);
                                vehiculos_en_filtro.remove(vehiculos.get(position));
                            }
                            comprobarVehiculosAgregados();
                        }
                    });
                    listViewFiltroVehiculos.setAdapter(adapter_vehiculos);
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
                parametros.put("En_cotizador", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void comprobarVehiculosAgregados() {
        vehiculos_en_filtro = new ArrayList<>();
        for (int i = 0; i < vehiculos.size() ; i++) {
            if(vehiculos.get(i).isAgregado()){
                vehiculos_en_filtro.add(vehiculos.get(i));
            }
        }
        Toast.makeText(ModificarFiltroVehiculosActivity.this,"Agregados:" +vehiculos_en_filtro.size(),Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Vehiculo> bindVehiculosEnFiltro(ArrayList<Vehiculo> vehiculos) {
        for (int i=0; i<vehiculos.size(); i++){
            for (int j=0; j<vehiculos_en_filtro.size(); j++){
                if (vehiculos.get(i).getId().equals(vehiculos_en_filtro.get(j).getId())){
                    vehiculos.get(i).setAgregado(true);
                }
            }
        }
        return vehiculos;
    }

    private void traer_vehiculos_en_filtro_WebService() {

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_vehiculos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Vehiculo vehiculo = null;
                ArrayList<Vehiculo> vehiculos_WS = new ArrayList<>();
                traer_vehiculos_WebService();
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
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

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

                        vehiculos_en_filtro.add(vehiculo);
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
                parametros.put("En_cotizador", "1");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }
}
