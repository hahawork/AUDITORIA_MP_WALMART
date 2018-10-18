package gv.haha.auditoria_mp_walmart.clases;

import android.os.Environment;

import org.json.JSONObject;

public interface Variables {

    String TBL_PUNTOSDEVENTA = "tbl_pdv";
    String TBL_REPORTE_ENCABEZADO = "tbl_rpt_encab";
    String TBL_REPORTE_DETALLE = "tbl_rpt_detalle";
    String TBL_CAT_INDICADORES = "tbl_cat_indicadores";
    String TBL_FOTO_INDCADOR = "tbl_foto_indicador";
    String TBL_EVAL_DISPLAY_ENC = "tbl_evaldisplay_enc";
    String TBL_EVAL_DISPLAY_DET = "tbl_evaldisplay_det";
    String TBL_NOMBRE_DISPLAY = "tbl_nombre_display";


    //para los web services
    String URL_WS_HOST = "http://www.grupovalor.com.ni/";
    String URL_WS_VERIFICA_PUNTOS_NUEVOS = URL_WS_HOST + "/ws/get_allpdv.php";
    String URL_WS_OBTENER_NOMBRES_DISPLAY = URL_WS_HOST +"ws/audit_mp_walmart/obtenerDisplay.php";
    String URL_WS_VERIFICA_NUEVA_VERSION = URL_WS_HOST + "ws/get_new_version.php";
    String URL_WS_REGISTRO = URL_WS_HOST + "ws/ws_getUsuario.php";
    String URL_WS_GUARDAR_DISPLAY = URL_WS_HOST + "ws/audit_mp_walmart/guardarDisplay.php";
    String URL_WS_GUARDA_REVISIONPDV_ENC = URL_WS_HOST + "ws/audit_mp_walmart/guardaRevisionPdvEnc.php";
    String URL_WS_GUARDA_REVISIONPDV_DET = URL_WS_HOST + "ws/audit_mp_walmart/guardaRevisionPdvDet.php";
    String URL_WS_GUARDA_REVISIONPDV_FOTOS = URL_WS_HOST + "ws/audit_mp_walmart/guardaRevisionPdvFotos.php";
    String URL_WS_GUARDA_EVALDISPLAY_ENC = URL_WS_HOST + "ws/audit_mp_walmart/guardarEvaluacionDisplayEnc.php";
    String URL_WS_GUARDA_EVALDISPLAY_DET = URL_WS_HOST + "ws/audit_mp_walmart/guardarEvaluacionDisplayDet.php";


    //para la repuesta del asynctask
    int option_registro = 1;
    int option_descPdv = 2;
    int option_verificaactualizacion = 3;

    //para el SharedPreferences
    String SETT_NOMBRE_USUARIO = "nombUsuario";
    String SETT_COD_USUARIO = "codUsuario";
    String SETT_CLIENTE_ASIGNADO = "stClienteAsignadostr";
    String SETT_FECHA_ULTIM_BK_DB_CSV = "stFechaUltimRespaldoBaseDatos";


    String CARPETA_RECURSOS = Environment.getExternalStorageDirectory() + "/grupovalor/formato_mp/";


    public interface AsyncTaskComplete {
        // Define data you like to return from AysncTask
        public void onAsyncTaskComplete(JSONObject result, int option);
    }
}

