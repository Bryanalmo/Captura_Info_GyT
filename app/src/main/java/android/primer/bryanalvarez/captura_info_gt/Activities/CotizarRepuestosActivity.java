package android.primer.bryanalvarez.captura_info_gt.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Clientes_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Contactos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Marca_Repuesto_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Productos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Adapters.Referencia_Repuestos_Adapter;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Contacto;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Repuesto;
import android.primer.bryanalvarez.captura_info_gt.Models.Marca;
import android.primer.bryanalvarez.captura_info_gt.Models.Producto;
import android.primer.bryanalvarez.captura_info_gt.R;
import android.primer.bryanalvarez.captura_info_gt.Util.Util;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CotizarRepuestosActivity extends AppCompatActivity implements View.OnClickListener {

    private AlertDialog alertDialog_cargando;
    private FloatingActionButton fab_ver_cotizaciones_repuestos;
    private EditText et_fecha_solicitud_producto;
    private ImageButton ib_fecha_solicitud_producto;
    //private Spinner spinner_clientes_producto;
    private Spinner spinner_contactos_productos;
    private AutoCompleteTextView spinner_clientes_producto;
    private ImageButton ib_agregar_productos;
    private RecyclerView linear_layout_productos_agregados;
    private RecyclerView recycler_view_contactos;
    private EditText et_observaciones_producto;
    private Button btn_enviar_cotizacion_productos;
    private FrameLayout frame_layout_ver_cotizaciones_repuestos;
    private ImageButton ib_crear_contacto;
    private Button ib_copiar_a_contactos;

    private Productos_Adapter productos_adapter;
    private Contactos_Adapter contactos_adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DividerItemDecoration itemDecoration;

    private ArrayList<Cliente> clientes;
    private ArrayList<Contacto> contactos;
    private ArrayList<Contacto> contactos_agregados = new ArrayList<>();
    private ArrayList<Producto> productos;
    private ArrayList<Marca> marcas = new ArrayList<>();
    private ArrayList<Marca> marcas_iniciales = new ArrayList<>();
    private ArrayList<Producto> productos_filtrados;
    private ArrayList<Producto> productos_agregados = new ArrayList<>();

    public final Calendar c = Calendar.getInstance();
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    private static final String CERO = "0";
    private static final String GUION = "-";

    private boolean clientesCargados = false;
    private boolean productosCargados = false;
    private boolean marcasCargadas = false;

    RequestQueue request;
    StringRequest stringRequest;
    String idCliente;
    String idContacto;
    String nombreCliente;
    String editar_crear;
    Cotizacion_Repuesto cotizacion_repuesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cotizar_repuestos);

        bindUI();

        if (productos_agregados.size() == 0){
            linear_layout_productos_agregados.setVisibility(View.GONE);
        }else{
            linear_layout_productos_agregados.setVisibility(View.VISIBLE);
        }

        editar_crear = getIntent().getExtras().getString("editar_crear");

        alertDialog_cargando = new AlertDialog.Builder(this).create();
        alertDialog_cargando.setMessage("Cargando...");
        alertDialog_cargando.setCancelable(false);
        alertDialog_cargando.setCanceledOnTouchOutside(false);
        alertDialog_cargando.show();

        request = Volley.newRequestQueue(this);

        traer_clientes_WebService();
        traer_productos_WebService();
        traer_marcas_WebService();

        ib_fecha_solicitud_producto.setOnClickListener(this);
        ib_agregar_productos.setOnClickListener(this);
        btn_enviar_cotizacion_productos.setOnClickListener(this);

        fab_ver_cotizaciones_repuestos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CotizarRepuestosActivity.this,CotizacionesRepuestosActivity.class);
                startActivity(intent);
            }
        });
        spinner_clientes_producto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < clientes.size(); i++) {
                    if (clientes.get(i).getNombre().equals(spinner_clientes_producto.getText().toString())){
                        Util.setCliente(clientes.get(i));
                        idCliente = clientes.get(i).getId();
                        nombreCliente= clientes.get(i).getNombre();
                        traer_contactos_WebService(spinner_contactos_productos);
                    }
                }
                Util.contactos_agregados.clear();
            }
        });

        spinner_contactos_productos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contacto contactoSelected = (Contacto) spinner_contactos_productos.getSelectedItem();
                //contactos.get(position).setAgregado(true);
                //contactos_agregados.add(contactoSelected);
                Util.setContacto(contactoSelected);
                idContacto = contactoSelected.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ib_crear_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertCrearContacto("Crear contácto","Ingrese los datos del contácto");
            }
        });
        ib_copiar_a_contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactos == null){
                    Toast.makeText(CotizarRepuestosActivity.this, "Se debe seleccionar un cliente primero", Toast.LENGTH_SHORT).show();
                }else{
                    showAlertCopiarAContactos();
                }

            }
        });
    }

    private void comprobarEditarCrear() {
        cotizacion_repuesto = Util.getCotizacion_repuesto();
        if (editar_crear.equals("editar")){
            et_fecha_solicitud_producto.setText(cotizacion_repuesto.getFechaSolicitud());
            et_observaciones_producto.setText(cotizacion_repuesto.getObservaciones());
            cargarCliente();
            //cargarContacto();
            bindAgregados();
            frame_layout_ver_cotizaciones_repuestos.setVisibility(View.GONE);

        }
    }

    private void cargarContacto() {
        if (editar_crear.equals("editar")){
            for (int i = 0; i < contactos.size(); i++) {
                if (contactos.get(i).getId().equals(cotizacion_repuesto.getIdContacto())){
                    spinner_contactos_productos.setSelection(i);
                }
            }
        }

    }

    private void bindAgregados() {
        for (int i = 0; i < cotizacion_repuesto.getProductos().size() ; i++) {
            productos_agregados.add(cotizacion_repuesto.getProductos().get(i));
            productos_adapter.notifyDataSetChanged();
        }
        linear_layout_productos_agregados.setVisibility(View.VISIBLE);
    }

    private void cargarCliente() {
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).getId().equals(cotizacion_repuesto.getIdCliente())){
                    spinner_clientes_producto.setText(clientes.get(i).getNombre());
                    spinner_clientes_producto.showDropDown();
                    //spinner_clientes.setSelection(i);
                    idCliente = clientes.get(i).getId();
                }
            }
    }

    private void cargarProducto(Spinner spinner, Producto producto) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equals(producto.getId())){
                spinner.setSelection(i);
            }
        }
    }

    private void bindUI() {
        et_fecha_solicitud_producto = (EditText) findViewById(R.id.et_fecha_solicitud_producto);
        ib_fecha_solicitud_producto = (ImageButton) findViewById(R.id.ib_fecha_solicitud_producto);
        spinner_clientes_producto = (AutoCompleteTextView) findViewById(R.id.spinner_clientes_producto);
        spinner_contactos_productos = (Spinner) findViewById(R.id.spinner_contactos_productos);
        //spinner_clientes_producto = (Spinner) findViewById(R.id.spinner_clientes_producto);
        ib_agregar_productos = (ImageButton) findViewById(R.id.ib_agregar_productos);
        linear_layout_productos_agregados = (RecyclerView) findViewById(R.id.linear_layout_productos_agregados);
        et_observaciones_producto = (EditText) findViewById(R.id.et_observaciones_producto);
        btn_enviar_cotizacion_productos = (Button) findViewById(R.id.btn_enviar_cotizacion_productos);
        fab_ver_cotizaciones_repuestos = (FloatingActionButton) findViewById(R.id.fab_ver_cotizaciones_repuestos);
        frame_layout_ver_cotizaciones_repuestos = (FrameLayout) findViewById(R.id.frame_layout_ver_cotizaciones_repuestos);
        ib_crear_contacto = (ImageButton) findViewById(R.id.ib_crear_contacto);
        ib_copiar_a_contactos = (Button) findViewById(R.id.ib_copiar_a_contactos);
        productos_adapter = new Productos_Adapter(this, productos_agregados, R.layout.list_view_item_producto_agregado, new Productos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Producto producto, int position) {
                showAlertAgregarProducto(producto,position);
            }
        }, new Productos_Adapter.OnButtonBorrarClickListener() {
            @Override
            public void onButtonBorrarClick(Producto producto, int position) {
                productos_agregados.remove(producto);
                productos_adapter.notifyDataSetChanged();
            }
        });

        linear_layout_productos_agregados.setHasFixedSize(true);
        linear_layout_productos_agregados.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        linear_layout_productos_agregados.setLayoutManager(mLayoutManager);
        linear_layout_productos_agregados.addItemDecoration(itemDecoration);
        linear_layout_productos_agregados.setAdapter(productos_adapter);
    }

    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                et_fecha_solicitud_producto.setText(year + GUION + mesFormateado + GUION+ diaFormateado);
            }
        },anio, mes, dia);
        recogerFecha.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_fecha_solicitud_producto:
                obtenerFecha();
                break;
            case R.id.ib_agregar_productos:
                showAlertAgregarProducto(null,0);
                break;
            case R.id.btn_enviar_cotizacion_productos:
                if (productos_agregados.size()>0){
                    showAlertDetallesCorreo();
                }else{
                    Toast.makeText(CotizarRepuestosActivity.this, "Debe añadir al menos un producto", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void traer_clientes_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_clientes_SILO.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Cliente cliente = null;
                clientes = new ArrayList<>();
                String id;
                String nombre;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        nombre = jsonArray.getJSONObject(i).getString("Nombre_completo");

                        cliente = new Cliente(id,nombre);
                        clientes.add(cliente);

                    }
                    //alertDialog_cargando.dismiss();
                    Clientes_Adapter adapter_clientes = new Clientes_Adapter(CotizarRepuestosActivity.this,R.layout.autocomplete_item_cliente,R.id.lbl_name,clientes);
                    spinner_clientes_producto.setAdapter(adapter_clientes);
                    //spinner_clientes_producto.setAdapter(new ArrayAdapter<Cliente>(CotizarRepuestosActivity.this, android.R.layout.simple_list_item_1, clientes));
                    clientesCargados = true;
                    hideAlertDialog();
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

    private void traer_contactos_WebService(final Spinner spinner_contactos) {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_contactos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Contacto contacto = null;
                contactos = new ArrayList<>();
                String id;
                String nombre;
                String apellido;
                String correo;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        nombre = jsonArray.getJSONObject(i).getString("Nombres");
                        apellido = jsonArray.getJSONObject(i).getString("Apellidos");
                        correo = jsonArray.getJSONObject(i).getString("Email");

                        contacto = new Contacto(id,nombre+" "+apellido,correo);
                        contacto.setAgregado(false);
                        contactos.add(contacto);

                    }
                    //alertDialog_cargando.dismiss();
                    if (spinner_contactos != null){
                        spinner_contactos.setAdapter(new ArrayAdapter<Contacto>(CotizarRepuestosActivity.this, android.R.layout.simple_list_item_1, contactos));
                        cargarContacto();
                        hideAlertDialog();
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
                parametros.put("Id_cliente", idCliente);
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void traer_productos_WebService() {


        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_productos.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                Producto producto = null;
                productos = new ArrayList<>();
                String Id;
                String Referencia;
                String Descripcion;
                String Stock;
                Long Precio;
                double IVA;
                String Marca;
                int Descuento;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        Id = jsonArray.getJSONObject(i).getString("Id");
                        Referencia = jsonArray.getJSONObject(i).getString("Referencia");
                        Descripcion = jsonArray.getJSONObject(i).getString("Descripcion");
                        Stock = jsonArray.getJSONObject(i).getString("Stock");
                        Precio = jsonArray.getJSONObject(i).getLong("Precio_venta");
                        IVA = jsonArray.getJSONObject(i).getDouble("IVA");
                        Marca = jsonArray.getJSONObject(i).getString("Marca");
                        Descuento = jsonArray.getJSONObject(i).getInt("Descuento");

                        producto = new Producto(Id,Referencia,Descripcion,Stock,Precio,IVA,Marca,Descuento);
                        productos.add(producto);

                    }
                    productosCargados = true;
                    hideAlertDialog();
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

    private void traer_marcas_WebService() {

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/get_marcas.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = null;
                String id;
                String des_marca;
                Marca marca;
                try {
                    jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length(); i++ ){

                        id = jsonArray.getJSONObject(i).getString("Id");
                        des_marca = jsonArray.getJSONObject(i).getString("Marca");

                        marca = new Marca(id,des_marca);
                        //marcas.add(marca);
                        marcas_iniciales.add(marca);

                    }
                    marcasCargadas = true;
                    hideAlertDialog();

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
                parametros.put("filtro", "0");
                return  parametros;
            }
        };
        request.add(stringRequest);
    }

    private void showAlertAgregarProducto(final Producto productoActual, final int position) {

        marcas.clear();
        for (int i = 0; i < marcas_iniciales.size() ; i++) {
            marcas.add(marcas_iniciales.get(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Producto");
        //Toast.makeText(CotizarRepuestosActivity.this, "marcas" + marcas.size(), Toast.LENGTH_SHORT).show();

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_productos, null);
        builder.setView(viewInflated);

        final AutoCompleteTextView dialog_producto_auto_tv_referencia = (AutoCompleteTextView) viewInflated.findViewById(R.id.dialog_producto_auto_tv_referencia);
        final AutoCompleteTextView dialog__auto_tv_marca = (AutoCompleteTextView) viewInflated.findViewById(R.id.dialog__auto_tv_marca);
        final TextView dialog_producto_tv_descripcion= (TextView) viewInflated.findViewById(R.id.dialog_producto_tv_descripcion);
        final TextView dialog_producto_tv_stock= (TextView) viewInflated.findViewById(R.id.dialog_producto_tv_stock);
        final TextView dialog_producto_tv_precio= (TextView) viewInflated.findViewById(R.id.dialog_producto_tv_precio);
        final TextView dialog_producto_tv_iva= (TextView) viewInflated.findViewById(R.id.dialog_producto_tv_iva);
        final EditText dialog_producto_et_cantidad = (EditText) viewInflated.findViewById(R.id.dialog_producto_et_cantidad);
        final EditText dialog_producto_et_descuento = (EditText) viewInflated.findViewById(R.id.dialog_producto_et_descuento);
        final String[] id_producto = {""};

        Marca_Repuesto_Adapter adapter_marcas = new Marca_Repuesto_Adapter(CotizarRepuestosActivity.this,R.layout.autocomplete_item,R.id.lbl_name,marcas);
        dialog__auto_tv_marca.setAdapter(adapter_marcas);
        //Referencia_Repuestos_Adapter adapter = new Referencia_Repuestos_Adapter(CotizarRepuestosActivity.this,R.layout.autocomplete_item_repuesto,R.id.lbl_name_repuesto,productos);
        //dialog_producto_auto_tv_referencia.setAdapter(adapter);

        if (productoActual != null){
            dialog_producto_auto_tv_referencia.setText(productoActual.getReferencia());
            dialog__auto_tv_marca.setText(productoActual.getMarca());
            //cargarProducto(dialog_producto_auto_tv_referencia,productoActual);
            dialog_producto_tv_descripcion.setText(productoActual.getDescripcion());
            dialog_producto_tv_stock.setText(productoActual.getStock());
            dialog_producto_tv_precio.setText("$ "+NumberFormat.getInstance().format(productoActual.getPrecio()));
            dialog_producto_tv_iva.setText(productoActual.getIVA()+"");
            dialog_producto_et_cantidad.setText(productoActual.getCantidad());
            dialog_producto_et_descuento.setText(productoActual.getDescuento()+"");
        }
        dialog__auto_tv_marca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                productos_filtrados = new ArrayList<>();
                for (int i = 0; i < productos.size() ; i++) {
                    Producto producto = productos.get(i);
                    if (producto.getMarca().equals(dialog__auto_tv_marca.getText().toString())){
                        productos_filtrados.add(producto);
                    }
                }
                Toast.makeText(CotizarRepuestosActivity.this, "productos" + productos_filtrados.size(), Toast.LENGTH_SHORT).show();
                Referencia_Repuestos_Adapter adapter = new Referencia_Repuestos_Adapter(CotizarRepuestosActivity.this,R.layout.autocomplete_item_repuesto,R.id.lbl_name_repuesto,productos_filtrados);
                dialog_producto_auto_tv_referencia.setAdapter(adapter);
            }
        });


        dialog_producto_auto_tv_referencia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < productos.size() ; i++) {
                    Producto producto = productos.get(i);
                    if (producto.getReferencia().equals(dialog_producto_auto_tv_referencia.getText().toString())){
                        id_producto[0] = producto.getId();
                        dialog_producto_tv_descripcion.setText(producto.getDescripcion());
                        dialog_producto_tv_stock.setText(producto.getStock());
                        dialog_producto_tv_precio.setText("$ "+NumberFormat.getInstance().format(producto.getPrecio()));
                        dialog_producto_tv_iva.setText(producto.getIVA()+"");
                        dialog_producto_et_descuento.setText(producto.getDescuento()+"");
                    }
                }
            }
        });

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int j = 0; j < productos.size(); j++) {
                    Producto producto = productos.get(j);
                    if (producto.getReferencia().equals(dialog_producto_auto_tv_referencia.getText().toString())){
                        String cantidad = dialog_producto_et_cantidad.getText().toString();
                        int descuento = Integer.parseInt(dialog_producto_et_descuento.getText().toString());
                        producto.setCantidad(cantidad);
                        producto.setDescuento(descuento);
                        if(productoActual != null){
                            productos_agregados.set(position,productoActual);
                        }else{
                            productos_agregados.add(producto);
                        }

                    }
                }
                //Toast.makeText(CotizarRepuestosActivity.this, "marcas" + marcas.size(), Toast.LENGTH_SHORT).show();

                productos_adapter.notifyDataSetChanged();
                linear_layout_productos_agregados.setVisibility(View.VISIBLE);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void buscarReferencia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar Referencia");

        productos_filtrados = new ArrayList<>();

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_buscar_referencia, null);
        builder.setView(viewInflated);

        final EditText et_buscar_referencia = (EditText) viewInflated.findViewById(R.id.et_buscar_referencia);

        builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String referencia = et_buscar_referencia.getText().toString().replace(" ","");
                for (int i = 0; i < productos.size() ; i++) {
                    if (productos.get(i).getReferencia().toLowerCase().contains(referencia)){
                        productos_filtrados.add(productos.get(i));
                    }
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void hideAlertDialog() {
        if (clientesCargados && productosCargados && marcasCargadas){
            comprobarEditarCrear();
            alertDialog_cargando.dismiss();
        }
    }

    private void crear_cotizacion_productos_WebService(final String forma_pago, final String asunto, final String cuerpo){

        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_cotizacion_producto_prueba.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                Toast.makeText(CotizarRepuestosActivity.this, response, Toast.LENGTH_LONG).show();
                try {
                    jsonObject = new JSONObject(response);
                    productos_agregados.clear();
                    Util.contactos_agregados.clear();
                    if(jsonObject.getString("Success").equals("true")){
                        Toast.makeText(CotizarRepuestosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CotizarRepuestosActivity.this,CotizacionesRepuestosActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(CotizarRepuestosActivity.this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    productos_agregados.clear();
                    Util.contactos_agregados.clear();
                    Toast.makeText(CotizarRepuestosActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    productos_agregados.clear();
                    Util.contactos_agregados.clear();
                    Toast.makeText(CotizarRepuestosActivity.this, "Creación exitosa", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CotizarRepuestosActivity.this,CotizacionesRepuestosActivity.class);
                    startActivity(intent);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                long valor_total = 0;
                String fecha_solicitud = et_fecha_solicitud_producto.getText().toString();
                String observaciones = et_observaciones_producto.getText().toString();

                parametros.put("Fecha_solicitud", fecha_solicitud );
                parametros.put("Id_cliente", idCliente );
                parametros.put("Id_contacto", idContacto );
                parametros.put("Nombre_cliente", nombreCliente);
                parametros.put("Observaciones", observaciones);
                parametros.put("Id_empleado", Util.getId_usuario() );
                parametros.put("Cantidad_productos", productos_agregados.size()+ "" );
                parametros.put("Forma_pago", forma_pago);
                parametros.put("Asunto", asunto);
                parametros.put("Cuerpo", cuerpo);
                for (int i=0; i<productos_agregados.size(); i++){
                    Producto producto = productos_agregados.get(i);
                    parametros.put("Id_producto"+i, producto.getId() );
                    parametros.put("Cantidad"+i, producto.getCantidad() );
                    parametros.put("Precio_un"+i, producto.getPrecio()+"" );
                    parametros.put("Descuento"+i, producto.getDescuento()+"" );
                }
                //String s="";
                parametros.put("Cantidad_contactos", Util.contactos_agregados.size()+ "" );
                for (int j = 0; j <Util.contactos_agregados.size() ; j++) {
                    parametros.put("Id_contacto"+j, Util.contactos_agregados.get(j).getId());
                    parametros.put("Correo_contacto"+j, Util.contactos_agregados.get(j).getCorreo());
                    parametros.put("Nombre_contacto"+j, Util.contactos_agregados.get(j).getNombre());
                    //s +=  contactos_agregados.get(j).getId() + " "+contactos_agregados.get(j).getCorreo() + "\n";
                }
                //Toast.makeText(CotizarRepuestosActivity.this, s, Toast.LENGTH_SHORT).show();

                return parametros;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        request.add(stringRequest);
    }

    private void showAlertDetallesCorreo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Detalles de correo electronico");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_detalles_correo, null);
        builder.setView(viewInflated);

        final RadioButton radioButton_30_dias = (RadioButton) viewInflated.findViewById(R.id.radioButton_30_dias);
        final RadioButton radioButton_50_50 = (RadioButton) viewInflated.findViewById(R.id.radioButton_50_50);
        final EditText et_asunto_correo = (EditText) viewInflated.findViewById(R.id.et_asunto_correo);
        final EditText et_cuerpo_correo = (EditText) viewInflated.findViewById(R.id.et_cuerpo_correo);

        cargarAsuntoyCuerpoCorreo(et_asunto_correo,et_cuerpo_correo);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String formaPago = (radioButton_30_dias.isChecked())?  "30 días":  "50% anticipado  50% contraentrega";
                String asunto = et_asunto_correo.getText().toString();
                String cuerpo = et_cuerpo_correo.getText().toString();
                //Toast.makeText(CotizarRepuestosActivity.this, formaPago, Toast.LENGTH_SHORT).show();
                if (productos_agregados.size()>0){
                    alertDialog_cargando = new AlertDialog.Builder(CotizarRepuestosActivity.this).create();
                    alertDialog_cargando.setMessage("Cargando...");
                    alertDialog_cargando.setCancelable(false);
                    alertDialog_cargando.setCanceledOnTouchOutside(false);
                    alertDialog_cargando.show();
                    crear_cotizacion_productos_WebService(formaPago,asunto,cuerpo);
                    //Toast.makeText(this, ""+idContacto, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, ""+idCliente, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CotizarRepuestosActivity.this, "Debe añadir al menos un producto", Toast.LENGTH_LONG).show();
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertCopiarAContactos() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Copiar a contáctos");
        for (int j = 0; j <contactos.size() ; j++) {
            for (int k = 0; k <Util.contactos_agregados.size() ; k++) {
                if (contactos.get(j).getId().equals(Util.contactos_agregados.get(k).getId())){
                    contactos.get(j).setAgregado(true);
                    break;
                }else{
                    contactos.get(j).setAgregado(false);
                }
            }
        }
        //Toast.makeText(CotizarRepuestosActivity.this, "encontrados "+coutn2, Toast.LENGTH_SHORT).show();

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_copiar_a_contactos, null);
        builder.setView(viewInflated);

        recycler_view_contactos = (RecyclerView) viewInflated.findViewById(R.id.recycler_view_contactos);
        recycler_view_contactos.setHasFixedSize(true);
        recycler_view_contactos.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_view_contactos.setLayoutManager(mLayoutManager);
        recycler_view_contactos.addItemDecoration(itemDecoration);

        contactos_adapter = new Contactos_Adapter(getApplicationContext(), contactos, R.layout.list_view_item_contacto, new Contactos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contacto contacto, int position) {

            }
        }, new Contactos_Adapter.OnButtonBorrarClickListener() {
            @Override
            public void onButtonBorrarClick(Contacto contacto, int position, boolean isChecked) {
                if(isChecked){
                    contacto.setAgregado(true);
                    Util.contactos_agregados.add(contacto);
                }else{
                    contacto.setAgregado(false);
                    Util.contactos_agregados.remove(contactos.get(position));
                }
               //Toast.makeText(CotizarRepuestosActivity.this,isChecked+" " + contactos_agregados.size()+ " " + contacto.getCorreo() + " " + contacto.getId(),Toast.LENGTH_SHORT).show();
            }
        });

        recycler_view_contactos.setAdapter(contactos_adapter);
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarAsuntoyCuerpoCorreo(EditText et_asunto_correo, EditText et_cuerpo_correo){

        String cuerpo = "En Golf & Turf SAS, distribuidores autorizados para Colombia de Jacobsen, E-Z-GO, Ventrac, entre otras reconocidas marcas, agradecemos la oportunidad de presentarle esta oferta, esperando que se atractiva para usted y que provea una solución integral a sus necesidades. Estaremos atentos a resolver cualquier inquietud que pueda surgir de la misma. A continuación presentaremos los medios a través de los cuales puede comunicarse con nosotros: ";
        et_cuerpo_correo.setText(cuerpo);
        String asunto = "Propuesta Repuestos - Golf y Turf";
        et_asunto_correo.setText(asunto);
    }

    private void showAlertCrearContacto(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_contacto, null);
        builder.setView(viewInflated);

        final EditText et_dialog_nombre_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_nombre_contacto);
        final EditText et_dialog_apellido_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_apellido_contacto);
        final EditText et_dialog_celular_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_celular_contacto);
        final EditText et_dialog_correo_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_correo_contacto);
        final EditText et_dialog_cargo_contacto = (EditText) viewInflated.findViewById(R.id.et_dialog_cargo_contacto);
        final Button bt_dialog_crear_contacto = (Button) viewInflated.findViewById(R.id.bt_dialog_crear_contacto);
        final CheckBox cb_tratamiento_datos= (CheckBox) viewInflated.findViewById(R.id.cb_tratamiento_datos);

        final AlertDialog dialog = builder.create();
        dialog.show();

        bt_dialog_crear_contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_dialog_nombre_contacto.getText().toString();
                String apellido = et_dialog_apellido_contacto.getText().toString();
                String celular = et_dialog_celular_contacto.getText().toString();
                String correo = et_dialog_correo_contacto.getText().toString();
                String cargo = et_dialog_cargo_contacto.getText().toString();
                if(cb_tratamiento_datos.isChecked()){
                    dialog.dismiss();
                    alertDialog_cargando.show();
                    crear_contacto_WebService(nombre,apellido,celular,correo,cargo);
                }else{
                    Toast.makeText(CotizarRepuestosActivity.this,"Por favor apruebe el tratamiento de datos",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void crear_contacto_WebService(final String nombre, final String apellido, final String celular, final String correo, final String cargo) {
        String url = "https://golfyturf.com/feria_automovil/AppWebServices/crear_contacto.php";
        url = url.replace(" ", "%20");

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                alertDialog_cargando.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("Success").equals("true")){
                        String id_contacto = jsonObject.getString("Id_contacto");
                        Toast.makeText(CotizarRepuestosActivity.this, "Contacto creado", Toast.LENGTH_SHORT).show();
                        //contactos.add(new Contacto(id_contacto,nombre,correo));
                        traer_contactos_WebService(spinner_contactos_productos);
                    }else{
                        Toast.makeText(CotizarRepuestosActivity.this, "Error al crear el contacto", Toast.LENGTH_SHORT).show();
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
                parametros.put("Id_cliente", idCliente);
                parametros.put("Nombre_contacto", nombre);
                parametros.put("Apellido_contacto", apellido);
                parametros.put("Celular_contacto", celular);
                parametros.put("Correo_contacto", correo);
                parametros.put("Cargo_contacto", cargo);
                return  parametros;
            }
        };
        request.add(stringRequest);

    }


    @Override
    public void onBackPressed() {
        if (editar_crear.equals("editar")){
            Intent intent = new Intent(CotizarRepuestosActivity.this,CotizacionesRepuestosActivity.class);
            startActivity(intent);
        }else if(editar_crear.equals("crear")){
            alertDialog_cargando = new AlertDialog.Builder(CotizarRepuestosActivity.this).create();
            alertDialog_cargando.setTitle("¿Está seguro que quiere salir?");
            alertDialog_cargando.setMessage("Si presiona continuar se perderan los datos de la cotización");
            alertDialog_cargando.setButton(DialogInterface.BUTTON_POSITIVE, "Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CotizarRepuestosActivity.this,MenuActivity.class);
                    startActivity(intent);
                }
            });
            alertDialog_cargando.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog_cargando.show();
        }

    }
}
