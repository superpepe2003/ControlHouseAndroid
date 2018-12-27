package com.controlhouse.utopiasoft.controlhouse.Entidades;

public class CConeccion {

    public static String bdEjemplo="&bd=true";
    public static String bdReal="";

    public static String bd;

    public static String URL_Ingreso_Movimiento= "http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=C";
    //id-fecha-categoriaid-cuentaid-monto-descripcion-tipo-hashtag-montoviejo-cuentaidvieja
    public static String URL_Modificar_Movimiento = "http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=U";
    public static String URL_Listar_Movimientos = "http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=L";
    public static String URL_Eliminar_Movimientos = "http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=R";

    public static String URL_Listar_Cuentas = "http://utopiasoft.duckdns.org:8080/wscontrol/servicecuentas.php?modo=L";

    public static String URL_Listar_Categorias = "http://utopiasoft.duckdns.org:8080/wscontrol/servicecategorias.php?modo=L";
    public static String URL_Ingresar_Categorias = "http://utopiasoft.duckdns.org:8080/wscontrol/servicecategorias.php?modo=C";

    public static String URL_Listar_Ranking = "http://utopiasoft.duckdns.org:8080/wscontrol/servicemovimientos.php?modo=conta";

}
