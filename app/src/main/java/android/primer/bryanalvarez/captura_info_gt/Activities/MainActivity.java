package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Cotizaciones_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_General_Vehiculo;
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

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton floatingActionButtonReporte;
    private ListView listViewCotizaciones;
    private Cotizaciones_Adapter adapter;
    private ArrayList<Cotizacion_General_Vehiculo> cotizaciones = new ArrayList<>();
    private AlertDialog alertDialog_cargando;

    RequestQueue request;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertDialog_cargando = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Util.setId_usuario(Util.getuserIdPrefs(prefs));*/


        request = Volley.newRequestQueue(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButtonReporte = (FloatingActionButton) findViewById(R.id.floatingActionButtonReporte);
        listViewCotizaciones = (ListView) findViewById(R.id.listViewCotizaciones);
        listViewCotizaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,VerCotizacionVehiculosActivity.class);
                Util.setCotizacion_general_vehiculo(cotizaciones.get(position));
                Util.setCotizaciones_vehiculos(cotizaciones.get(position).getSubCotizaciones());
                startActivity(intent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,VehiculosActivity.class);
                startActivity(intent);
            }
        });
        floatingActionButtonReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generar_informe_WebService();
                Uri uri = Uri.parse("https://golfyturf.com/feria_automovil/AppWebServices/generar_informe_cotizaciones.php?Id_comercial="+Util.getId_usuario());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        traer_cotizaciones_WebService();

        registerForContextMenu(listViewCotizaciones);

    }

    private void traer_cotizaciones_WebService(){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_cotizaciones.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this, "r: " + response, Toast.LENGTH_SHORT).show();
                JSONArray jsonArray = null;
                Cotizacion_General_Vehiculo cotizacion = null;
                ArrayList<Cotizacion_General_Vehiculo> cotizaciones_WS = new ArrayList<>();

                String Id_cotizaion_general;
                String Numero;
                String Id_comercial;
                String Comercial;
                long Valor_general;
                String Observaciones;
                String Id_estado_envio;

                String Id_cliente;
                String Nombre_cliente;
                String Cedula_nit_cliente;
                String Celular_cliente;
                String Correo_cliente;
                String Ciudad_cliente;
                int Interes_cliente;

                String Id_sub_cotizacion;
                int Valor_sin_iva_sub_cotizacion;
                int Valor_iva_sub_cotizacion;
                int Valor_sub_cotizacion;

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
                double IVA_vehiculo;
                long Precio_vehiculo_IVA;
                long Aumento_vehiculo_IVA;
                double Descuento;
                long Precio_sin_descuento;

                String Id_accesorio;
                int Precio_accesorio;
                String Referencia;
                long Precio_accesorio_IVA;
                long Aumento_accesorio_IVA;
                double IVA_accesorio;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id_cotizaion_general = jsonArray.getJSONObject(i).getString("Id");
                        Numero = jsonArray.getJSONObject(i).getString("Numero");
                        Id_comercial = jsonArray.getJSONObject(i).getString("Id_empleado");
                        Comercial = jsonArray.getJSONObject(i).getString("Nombres");
                        Valor_general = jsonArray.getJSONObject(i).getLong("Valor");
                        Observaciones = jsonArray.getJSONObject(i).getString("Observaciones");
                        Id_estado_envio = jsonArray.getJSONObject(i).getString("Id_estado_envio");

                        Id_cliente = jsonArray.getJSONObject(i).getString("Id_cliente");
                        Nombre_cliente = jsonArray.getJSONObject(i).getString("Nombre");
                        Cedula_nit_cliente = jsonArray.getJSONObject(i).getString("Cedula/NIT");
                        Celular_cliente = jsonArray.getJSONObject(i).getString("Celular");
                        Correo_cliente = jsonArray.getJSONObject(i).getString("Correo");
                        Ciudad_cliente = jsonArray.getJSONObject(i).getString("Ciudad");
                        Interes_cliente = jsonArray.getJSONObject(i).getInt("Nivel_interes");

                        Cliente cliente = new Cliente(Id_cliente,Nombre_cliente,Cedula_nit_cliente,Celular_cliente,Correo_cliente,Ciudad_cliente);
                        cliente.setInteres(Interes_cliente);

                        Cotizacion subCotizacion = null;
                        Vehiculo vehiculo = null;
                        JSONArray jsonArraySubcotizaciones= null;
                        ArrayList<Cotizacion> subcotizaciones_WS = new ArrayList<>();

                        double moneda =0;
                        if (Valor_general > 1000000){
                            moneda = Util.TRM;
                        }else{
                            moneda = 1;
                        }

                        jsonArraySubcotizaciones = jsonArray.getJSONObject(i).getJSONArray("Sub_cotizaciones");
                        for (int j=0; j<jsonArraySubcotizaciones.length(); j++){

                            int valor_sin_iva_calculado = 0;
                            int valor_iva_calculado = 0;

                            Id_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getString("Id");
                            Valor_sin_iva_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getInt("Valor_sin_IVA");
                            Valor_iva_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getInt("Valor_IVA");
                            Valor_sub_cotizacion = jsonArraySubcotizaciones.getJSONObject(j).getInt("Valor");

                            Id_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getString("Id_vehiculo");
                            Precio_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getInt("Precio");
                            Precio_vehiculo = (int) (Precio_vehiculo * moneda);
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
                            IVA_vehiculo = jsonArraySubcotizaciones.getJSONObject(j).getDouble("IVA");
                            Descuento = jsonArraySubcotizaciones.getJSONObject(j).getDouble("Descuento");

                            Aumento_vehiculo_IVA = (long) (Precio_vehiculo*IVA_vehiculo);
                            Precio_vehiculo_IVA = (long) (Precio_vehiculo + (Aumento_vehiculo_IVA));
                            valor_sin_iva_calculado = Precio_vehiculo;
                            valor_iva_calculado = (int) Aumento_vehiculo_IVA;

                            if (Valor_general > 1000000){
                                Precio_vehiculo_IVA = redondearPrecio(Precio_vehiculo_IVA);
                                Precio_vehiculo = redondearPrecioSinIVa(Precio_vehiculo_IVA, IVA_vehiculo);
                                Aumento_vehiculo_IVA = recalcularAumentoIVA(Precio_vehiculo, IVA_vehiculo);
                            }

                            Precio_sin_descuento = Precio_vehiculo;

                            Precio_vehiculo_IVA = (long) (Precio_vehiculo_IVA * (1-Descuento));

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
                            vehiculo.setAumento_IVA(Aumento_vehiculo_IVA);
                            vehiculo.setValor_IVA(Precio_vehiculo_IVA);
                            vehiculo.setImagen(Imagen_vehiculo_vehiculo);
                            vehiculo.setValor_sin_descuento(Precio_sin_descuento);

                            Accesorio accesorio = null;
                            JSONArray jsonArrayAccesoriosSelec = null;
                            ArrayList<Accesorio> accesorios_WS = new ArrayList<>();

                            jsonArrayAccesoriosSelec = jsonArraySubcotizaciones.getJSONObject(j).getJSONArray("Accesorios");
                            for (int k=0; k<jsonArrayAccesoriosSelec.length(); k++){
                                Id_accesorio = jsonArrayAccesoriosSelec.getJSONObject(k).getString("Id_accesorio");
                                Precio_accesorio = jsonArrayAccesoriosSelec.getJSONObject(k).getInt("Precio");
                                Precio_accesorio = (int) (Precio_accesorio * moneda);
                                Referencia = jsonArrayAccesoriosSelec.getJSONObject(k).getString("Accesorio");
                                IVA_accesorio = jsonArrayAccesoriosSelec.getJSONObject(k).getDouble("IVA");

                                Aumento_accesorio_IVA = (long) (Precio_accesorio*IVA_accesorio);
                                Precio_accesorio_IVA = (long) (Precio_vehiculo + (Aumento_accesorio_IVA));

                                valor_sin_iva_calculado += Precio_accesorio;
                                valor_iva_calculado += (int) Aumento_accesorio_IVA;

                                accesorio = new Accesorio();
                                accesorio.setId(Id_accesorio);
                                accesorio.setValor(Precio_accesorio);
                                accesorio.setReferencia(Referencia);
                                accesorio.setCheck(true);
                                accesorio.setAumento_IVA(Aumento_accesorio_IVA);
                                accesorio.setPrecio_IVA(Precio_accesorio_IVA);

                                accesorios_WS.add(accesorio);
                            }

                            subCotizacion = new Cotizacion(Id_sub_cotizacion,valor_sin_iva_calculado,valor_iva_calculado,Valor_sub_cotizacion,vehiculo,accesorios_WS);
                            subcotizaciones_WS.add(subCotizacion);

                        }

                        cotizacion = new Cotizacion_General_Vehiculo(Id_cotizaion_general,Numero,Id_comercial,Comercial,Valor_general,cliente,subcotizaciones_WS,Observaciones, Id_estado_envio);
                        cotizaciones_WS.add(cotizacion);
                    }
                    adapter = new Cotizaciones_Adapter(MainActivity.this, cotizaciones_WS, R.layout.list_view_item_cotizacion);
                    listViewCotizaciones.setAdapter(adapter);
                    cotizaciones=cotizaciones_WS;
                    alertDialog_cargando.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "e: "+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    private long recalcularAumentoIVA(int precio, double iva) {
        return (long) (precio * iva);
    }

    private int redondearPrecioSinIVa(long precio_iva, double iva) {
        return (int) (precio_iva / (1+iva));
    }

    private long redondearPrecio(long precio) {

        double precio_objetivo = 0;
        double precio_round = precio/100000;
        for (int i = 1000000; i < 100000000 ; i+=1000000) {
            if(precio>=i && precio<(i+1000000)){
                int factor = 1000;
                while (precio_round != precio_objetivo){

                    long precio_div = precio/(factor);
                    double precio_div_round = Math.ceil(precio_div);
                    precio = (long) (precio_div_round*(factor));

                    precio_objetivo = i/100000;
                    precio_round = precio/100000;

                    factor = factor*10;

                }
                break;
            }
        }

        return precio - 100000;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this,VehiculosActivity.class);
        startActivity(intent);
    }
}
