package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Maquina_Filtro_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Maquina;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
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

public class ModificarFiltroActivity extends AppCompatActivity {

    private RecyclerView listViewFiltroMaquinas;
    private Button btn_actualizar_filtro;
    private Maquina_Filtro_Adapter adapter_maquinas;
    private ArrayList<Maquina> maquinas = new ArrayList<>();
    private ArrayList<Maquina> maquinas_en_filtro = new ArrayList<>();
    private AlertDialog alertDialog_cargando;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;


    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_filtro);

        request = Volley.newRequestQueue(this);

        listViewFiltroMaquinas = (RecyclerView) findViewById(R.id.listViewFiltroMaquinas);
        btn_actualizar_filtro = (Button) findViewById(R.id.btn_actualizar_filtro);

        listViewFiltroMaquinas.setHasFixedSize(true);
        listViewFiltroMaquinas.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        listViewFiltroMaquinas.setLayoutManager(mLayoutManager);

        traer_maquinas_en_filtro_WebService();

        alertDialog_cargando = new AlertDialog.Builder(ModificarFiltroActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        btn_actualizar_filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar_filtro_WebService();
                alertDialog_cargando = new AlertDialog.Builder(ModificarFiltroActivity.this).create();
                alertDialog_cargando.setMessage("Cargando...");
                alertDialog_cargando.setCancelable(false);
                alertDialog_cargando.setCanceledOnTouchOutside(false);
                alertDialog_cargando.show();
            }
        });


    }

    private void traer_maquinas_WebService() {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_maquinas.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Maquina maquina= null;
                ArrayList<Maquina> maquinas_WS = new ArrayList<>();

                String Id;
                String Modelo_maquina;
                String Referencia;
                int Precio;
                double IVA;
                String Link;
                String Informacion_tecnica;
                String Funcion;
                String Tipo_motor;
                String Marca_motor;
                String Marca;
                String Imagen_equipo;
                long Precio_IVA;
                long Aumento_IVA;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Referencia = jsonArray.getJSONObject(i).getString("Referencia");
                        Modelo_maquina = jsonArray.getJSONObject(i).getString("Modelo_maquina");
                        Precio = jsonArray.getJSONObject(i).getInt("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Link = jsonArray.getJSONObject(i).getString("Link");
                        Informacion_tecnica = jsonArray.getJSONObject(i).getString("Informacion_tecnica");
                        Funcion = jsonArray.getJSONObject(i).getString("Funcion");
                        Tipo_motor = jsonArray.getJSONObject(i).getString("Tipo_motor");
                        Marca_motor = jsonArray.getJSONObject(i).getString("Marca_motor");
                        Marca = jsonArray.getJSONObject(i).getString("Marca");
                        Imagen_equipo = jsonArray.getJSONObject(i).getString("Imagen_equipo");
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

                        maquina = new Maquina();

                        maquina.setId(Id);
                        maquina.setReferencia(Referencia);
                        maquina.setModelo_maquina(Modelo_maquina);
                        maquina.setPrecio(Precio);
                        maquina.setIVA(IVA);
                        maquina.setPrecio_IVA(Precio_IVA);
                        maquina.setAumento_IVA(Aumento_IVA);
                        maquina.setLink(Link);
                        maquina.setInformacion_tecnica(Informacion_tecnica);
                        maquina.setFuncion(Funcion);
                        maquina.setTipo_motor(Tipo_motor);
                        maquina.setMarca_motor(Marca_motor);
                        maquina.setMarca(Marca);
                        maquina.setImagen_equipo(Imagen_equipo);

                        maquinas.add(maquina);
                    }
                    maquinas = bindMaquinasEnFiltro(maquinas);
                    adapter_maquinas = new Maquina_Filtro_Adapter(ModificarFiltroActivity.this, maquinas, R.layout.list_view_item_filtro_maquinas, new Maquina_Filtro_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Maquina maquina, int position) {

                        }
                    }, new Maquina_Filtro_Adapter.OnButtonBorrarClickListener() {
                        @Override
                        public void onButtonBorrarClick(Maquina maquina, int position, boolean isChecked) {
                            if(isChecked){
                                maquina.setAgregado(true);
                            }else{
                                maquina.setAgregado(false);
                                maquinas_en_filtro.remove(maquinas.get(position));
                            }
                            comprobarMaquinasAgregadas();
                        }
                    });
                    listViewFiltroMaquinas.setAdapter(adapter_maquinas);
                    adapter_maquinas.notifyDataSetChanged();
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
                parametros.put("Funcion", "-1");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void comprobarMaquinasAgregadas() {
        maquinas_en_filtro = new ArrayList<>();
        for (int i = 0; i < maquinas.size() ; i++) {
            if(maquinas.get(i).isAgregado()){
                maquinas_en_filtro.add(maquinas.get(i));
            }
        }
        Toast.makeText(ModificarFiltroActivity.this,"Agregados:" +maquinas_en_filtro.size(),Toast.LENGTH_SHORT).show();
    }

    private void traer_maquinas_en_filtro_WebService() {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_maquinas.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                traer_maquinas_WebService();
                JSONArray jsonArray = null;
                Maquina maquina= null;
                ArrayList<Maquina> maquinas_filtro_WS = new ArrayList<>();

                String Id;
                String Modelo_maquina;
                String Referencia;
                int Precio;
                double IVA;
                String Link;
                String Informacion_tecnica;
                String Funcion;
                String Tipo_motor;
                String Marca_motor;
                String Marca;
                String Imagen_equipo;
                long Precio_IVA;
                long Aumento_IVA;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Referencia = jsonArray.getJSONObject(i).getString("Referencia");
                        Modelo_maquina = jsonArray.getJSONObject(i).getString("Modelo_maquina");
                        Precio = jsonArray.getJSONObject(i).getInt("Precio");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Link = jsonArray.getJSONObject(i).getString("Link");
                        Informacion_tecnica = jsonArray.getJSONObject(i).getString("Informacion_tecnica");
                        Funcion = jsonArray.getJSONObject(i).getString("Funcion");
                        Tipo_motor = jsonArray.getJSONObject(i).getString("Tipo_motor");
                        Marca_motor = jsonArray.getJSONObject(i).getString("Marca_motor");
                        Marca = jsonArray.getJSONObject(i).getString("Marca");
                        Imagen_equipo = jsonArray.getJSONObject(i).getString("Imagen_equipo");
                        Aumento_IVA = (long) (Precio*IVA);
                        Precio_IVA = (long) (Precio + (Aumento_IVA));

                        maquina = new Maquina();

                        maquina.setId(Id);
                        maquina.setReferencia(Referencia);
                        maquina.setModelo_maquina(Modelo_maquina);
                        maquina.setPrecio(Precio);
                        maquina.setIVA(IVA);
                        maquina.setPrecio_IVA(Precio_IVA);
                        maquina.setAumento_IVA(Aumento_IVA);
                        maquina.setLink(Link);
                        maquina.setInformacion_tecnica(Informacion_tecnica);
                        maquina.setFuncion(Funcion);
                        maquina.setTipo_motor(Tipo_motor);
                        maquina.setMarca_motor(Marca_motor);
                        maquina.setMarca(Marca);
                        maquina.setImagen_equipo(Imagen_equipo);

                        maquinas_filtro_WS.add(maquina);
                    }
                    maquinas_en_filtro = maquinas_filtro_WS;
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
                parametros.put("Funcion", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private ArrayList<Maquina> bindMaquinasEnFiltro(ArrayList<Maquina> maquinas) {
        for (int i=0; i<maquinas.size(); i++){
            for (int j=0; j<maquinas_en_filtro.size(); j++){
                if (maquinas.get(i).getId().equals(maquinas_en_filtro.get(j).getId())){
                    maquinas.get(i).setAgregado(true);
                }
            }
        }
        return maquinas;
    }

    private void actualizar_filtro_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/modificar_filtro_maquina.php";
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
                        Toast.makeText(ModificarFiltroActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ModificarFiltroActivity.this,MaquinasActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ModificarFiltroActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(ModificarFiltroActivity.this, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ModificarFiltroActivity.this,MaquinasActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("Numero_maquinas",maquinas.size()+"");
                for (int i=0; i<maquinas.size(); i++){
                    parametros.put("Id_maquina"+i,maquinas.get(i).getId());
                    parametros.put("Maquina_agregada"+i,maquinas.get(i).isAgregado()+"");
                }
                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);
    }
}
