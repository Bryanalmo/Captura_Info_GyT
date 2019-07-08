package android.primer.bryanalvarez.captura_info_gt.Util;

import android.content.SharedPreferences;
import android.primer.bryanalvarez.captura_info_gt.Models.Accesorio;
import android.primer.bryanalvarez.captura_info_gt.Models.Arrendamiento_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Models.Cliente;
import android.primer.bryanalvarez.captura_info_gt.Models.Componente;
import android.primer.bryanalvarez.captura_info_gt.Models.Contacto;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Arrendamiento;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_General_Vehiculo;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.Cotizacion_Repuesto;
import android.primer.bryanalvarez.captura_info_gt.Models.Maquina;
import android.primer.bryanalvarez.captura_info_gt.Models.SubCotizacion;
import android.primer.bryanalvarez.captura_info_gt.Models.Vehiculo;

import java.util.ArrayList;

/**
 * Created by nayar on 9/10/2018.
 */

public class Util {

    public static String Id_usuario;

    public static String Id_cargo;

    public static double TRM;

    public static double monedaActual;

    public static Vehiculo vehiculo;

    public static Cotizacion_General_Vehiculo cotizacion_general_vehiculo;

    public static Cotizacion cotizacion;

    public static ArrayList<Cotizacion> cotizaciones_vehiculos = new ArrayList<>();

    public static Maquina maquina;

    public static Cliente cliente;

    public static Contacto contacto;

    public static Cotizacion_Maquina cotizacion_maquina;

    public static SubCotizacion subCotizacion;

    public static ArrayList<SubCotizacion> cotizaciones_maquinas = new ArrayList<>();

    public static Cotizacion_Repuesto cotizacion_repuesto;

    public static ArrayList<Contacto> contactos_agregados = new ArrayList<>();

    public static ArrayList<Arrendamiento_Vehiculo> arrendamientos_vehiculos = new ArrayList<>();

    public static Cotizacion_Arrendamiento cotizacion_arrendamiento;

    public static Arrendamiento_Vehiculo arrendamiento_vehiculo;

    public static Maquina getMaquina() {
        return maquina;
    }

    public static void setMaquina(Maquina maquina) {
        Util.maquina = maquina;
    }

    public static Vehiculo getVehiculo() {
        return vehiculo;
    }

    public static String getId_usuario() {
        return Id_usuario;
    }

    public static void setId_usuario(String id_usuario) {
        Id_usuario = id_usuario;
    }

    public static String getId_cargo() {
        return Id_cargo;
    }

    public static void setId_cargo(String id_cargo) {
        Id_cargo = id_cargo;
    }

    public static void setVehiculo(Vehiculo vehiculo) {
        Util.vehiculo = vehiculo;
    }


    public static Cotizacion_General_Vehiculo getCotizacion_general_vehiculo() {
        return cotizacion_general_vehiculo;
    }

    public static void setCotizacion_general_vehiculo(Cotizacion_General_Vehiculo cotizacion_general_vehiculo) {
        Util.cotizacion_general_vehiculo = cotizacion_general_vehiculo;
    }

    public static ArrayList<Cotizacion> getCotizaciones_vehiculos() {
        return cotizaciones_vehiculos;
    }

    public static void setCotizaciones_vehiculos(ArrayList<Cotizacion> cotizaciones_vehiculos) {
        Util.cotizaciones_vehiculos = cotizaciones_vehiculos;
    }

    public static Cotizacion getCotizacion() {
        return cotizacion;
    }

    public static void setCotizacion(Cotizacion cotizacion) {
        Util.cotizacion = cotizacion;
    }

    public static Cliente getCliente() { return cliente;}

    public static void setCliente(Cliente cliente) {Util.cliente = cliente;}

    public static Contacto getContacto() {return contacto;}

    public static void setContacto(Contacto contacto) {Util.contacto = contacto;}

    public static Cotizacion_Maquina getCotizacion_maquina() {
        return cotizacion_maquina;
    }

    public static void setCotizacion_maquina(Cotizacion_Maquina cotizacion_maquina) {
        Util.cotizacion_maquina = cotizacion_maquina;
    }

    public static SubCotizacion getSubCotizacion() {
        return subCotizacion;
    }

    public static void setSubCotizacion(SubCotizacion subCotizacion) {
        Util.subCotizacion = subCotizacion;
    }

    public static ArrayList<SubCotizacion> getCotizaciones_maquinas() {
        return cotizaciones_maquinas;
    }

    public static void setCotizaciones_maquinas(ArrayList<SubCotizacion> cotizaciones_maquinas) {
        Util.cotizaciones_maquinas = cotizaciones_maquinas;
    }

    public static Cotizacion_Repuesto getCotizacion_repuesto() {
        return cotizacion_repuesto;
    }

    public static void setCotizacion_repuesto(Cotizacion_Repuesto cotizacion_repuesto) {
        Util.cotizacion_repuesto = cotizacion_repuesto;
    }

    public static String getuserUserPrefs(SharedPreferences preferences){
        return preferences.getString("User","");
    }
    public static String getuserPasswordPrefs(SharedPreferences preferences){
        return preferences.getString("Pass","");
    }
    public static String getuserIdPrefs(SharedPreferences preferences){
        return preferences.getString("Id","");
    }
    public static String getuserCargoPrefs(SharedPreferences preferences){
        return preferences.getString("Cargo","");
    }
    public static void deleteUserandPass(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("User");
        editor.remove("Pass");
        editor.remove("Cargo");
        editor.remove("Id");
        editor.apply();
    }
}
