package gv.haha.auditoria_mp_walmart.clases;

import android.os.Environment;

import org.json.JSONException;
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
    String TBL_ACTIVIDAD_COMERCIAL_DATA = "tbl_actividad_comercial_data";
    String TBL_RPT_ACTIVIDAD_COMERCIAL = "tbl_rpt_actividad_comercial";
    String TBL_DATA_HAND_HELD = "tbl_audit_mp_walmart_handheld";
    String TBL_DATA_HAND_HELD_PDV = "tbl_audit_mp_walmart_handheld_pdv";
    String TBL_OPORTUNIDADES = "tbl_Oportunidades";



    //para los web services
    String URL_WS_HOST = "https://www.grupovalor.com.ni/";
    //String URL_WS_HOST = "http://192.168.1.200/";
    String URL_WS_MULTIPLE_OPCIONES = URL_WS_HOST + "/ws/webservice_select.php";
    String URL_WS_VERIFICA_PUNTOS_NUEVOS = URL_WS_HOST + "/ws/get_allpdv.php";
    String URL_WS_OBTENER_NOMBRES_DISPLAY = URL_WS_HOST + "ws/audit_mp_walmart/obtenerDisplay.php";
    String URL_WS_VERIFICA_NUEVA_VERSION = URL_WS_HOST + "ws/get_new_version.php";
    String URL_WS_REGISTRO = URL_WS_HOST + "ws/ws_getUsuario.php";
    String URL_WS_GUARDAR_DISPLAY = URL_WS_HOST + "ws/audit_mp_walmart/guardarDisplay.php";
    String URL_WS_GUARDA_REVISIONPDV_ENC = URL_WS_HOST + "ws/audit_mp_walmart/guardaRevisionPdvEnc.php";
    String URL_WS_GUARDA_REVISIONPDV_DET = URL_WS_HOST + "ws/audit_mp_walmart/guardaRevisionPdvDet.php";
    String URL_WS_GUARDA_REVISIONPDV_FOTOS = URL_WS_HOST + "ws/audit_mp_walmart/guardaRevisionPdvFotos.php";
    String URL_WS_GUARDA_EVALDISPLAY_ENC = URL_WS_HOST + "ws/audit_mp_walmart/guardarEvaluacionDisplayEnc.php";
    String URL_WS_GUARDA_EVALDISPLAY_DET = URL_WS_HOST + "ws/audit_mp_walmart/guardarEvaluacionDisplayDet.php";
    String URL_WS_OBTENER_ACTIVCOMERC_DATA = URL_WS_HOST + "ws/audit_mp_walmart/obtenerActividadComercialData.php";
    String URL_WS_GUARDAR_ACTIVCOMERC = URL_WS_HOST + "ws/audit_mp_walmart/guardaActividadComercialData.php";
    String URL_WS_GUARDAR_ACTIVCOMERC_FOTO = URL_WS_HOST + "ws/audit_mp_walmart/guardaActividadComercialDataNew.php";
    String URL_WS_GUARDAR_OPORTUNIDAD = URL_WS_HOST + "ws/audit_mp_walmart/guardarOportunidades.php";
    String URL_WS_OBTENER_HANDHELD = URL_WS_HOST + "ws/audit_mp_walmart/obtenerHandHeldData.php";


    //para la repuesta del asynctask
    int option_registro = 1;
    int option_descPdv = 2;
    int option_verificaactualizacion = 3;

    //para el SharedPreferences
    String SETT_NOMBRE_USUARIO = "nombUsuario";
    String SETT_COD_USUARIO = "codUsuario";
    String SETT_CLIENTE_ASIGNADO = "stClienteAsignadostr";
    String SETT_FECHA_ULTIM_BK_DB_CSV = "stFechaUltimRespaldoBaseDatos";
    String SETT_ULT_PDV_ACTCOMERC_SELECC="stUltimoPdvActComercSelecc";


    String CARPETA_RECURSOS = Environment.getExternalStorageDirectory() + "/grupovalor/formato_mp/";


    //para los envios pendientes
    String VAR_PARAMETRO="TipoEnvioPend";
    int ENV_PEND_REVIS_PDV = 1;
    int ENV_PEND_EVAL_DISPLAY = 2;
    int ENV_PEND_ACTIVID_COMERC = 3;

    //configuracion
    String SETT_FOTO_SIZE_KEY = "setMaxFotoSize";

}

