package gv.haha.auditoria_mp_walmart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gv.haha.auditoria_mp_walmart.clases.BaseDatos;
import gv.haha.auditoria_mp_walmart.clases.DisplayList;
import gv.haha.auditoria_mp_walmart.clases.classDisplayRecyclerViewAdapter;

import static gv.haha.auditoria_mp_walmart.clases.Globales.isExternalStorageAvailable;
import static gv.haha.auditoria_mp_walmart.clases.Globales.isExternalStorageReadOnly;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_DET;
import static gv.haha.auditoria_mp_walmart.clases.Variables.TBL_EVAL_DISPLAY_ENC;


public class FragmentTodosReportes extends Fragment implements AdapterView.OnItemSelectedListener {

    BaseDatos baseDatos;
    RecyclerView recyclerView;
    View view;
    Spinner spnFiltro;
    private int mColumnCount = 1;
    public static View.OnClickListener myOnClickListener;

    //este arreglo contendra la lista de reportes
    List<DisplayList> arrContenido = new ArrayList<>();
    //este arreglo contendra los nombres para mostrar en el spinner de nombres de display
    List<String> arrNombre = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //permite agregar menu
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_todos_reporte_evaldisplay, container, false);

        baseDatos = new BaseDatos(getContext());
        myOnClickListener = new MyOnClickListener(getContext());
        spnFiltro = (Spinner) view.findViewById(R.id.spnFiltroNombre_fragmTodosRpt_EvalDisp);

        getDataIndicadores("");

        // Set the adapter
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrNombre);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFiltro.setAdapter(dataAdapter);
        spnFiltro.setOnItemSelectedListener(this);

        return view;
    }

    private void getDataIndicadores(String nombreDisplay) {
        // Add some sample items.
        //instancia del calendario
        Calendar calendar = Calendar.getInstance();
        //se resta un mes a la fecha actual
        calendar.add(Calendar.MONTH, -1);
        //obtienen las variables con la nueva fecha
        int year = calendar.get(Calendar.YEAR);
        //al mes se le suma 1 xq enero = 0;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Date d = calendar.getTime();


        arrContenido.clear();
        //arrNombre.clear();
        if (!arrNombre.contains("Todos"))
            arrNombre.add("Todos");

        String filtroNombre = nombreDisplay.length() > 0 ? "NombreDislay = '" + nombreDisplay + "' AND " : "";

        Cursor cdata = baseDatos.obtenerRegistroWhereArgs(
                TBL_EVAL_DISPLAY_ENC,
                filtroNombre + "strftime('%Y-%m-%d', FechReg) BETWEEN '" + new SimpleDateFormat("yyyy-MM-dd").format(d) + "' and '" + new SimpleDateFormat("yyyy-MM-dd").format(new
                        Date()) + "'");


        if (cdata.getCount() > 0) {

            for (cdata.moveToFirst(); !cdata.isAfterLast(); cdata.moveToNext()) {

                int id = cdata.getInt(cdata.getColumnIndex("idEvalDispEnc"));
                String nombre = cdata.getString(cdata.getColumnIndex("NombreDislay"));
                int idenviado = cdata.getInt(cdata.getColumnIndex("idEnviado"));

                String fechaReg = idenviado == 0 ? "*Pendiente* 😐 ‍" + cdata.getString(cdata.getColumnIndex("FechReg")) :
                        "*Enviado* 😁 ‍" + cdata.getString(cdata.getColumnIndex("FechReg")) ;

                arrContenido.add(new DisplayList(id, nombre, fechaReg));

                if (!arrNombre.contains(nombre))
                    arrNombre.add(nombre);

            }
        } else {
            arrContenido.add(new DisplayList(0, "No hay registros", new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new classDisplayRecyclerViewAdapter(arrContenido));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            getDataIndicadores("");
        } else {
            getDataIndicadores(arrNombre.get(position));
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_todoslosreportes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_save) {

            //si ya filtro por nombre los reportes
            if (spnFiltro.getSelectedItemPosition() > 0) {

                if (GuardarArchivoExcel()) {

                } else {
                    Toast.makeText(getContext(), "no se ha podido guardar el reporte", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Primero seleccione un display", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            IndicadorSeleccionado(v);
        }

        private void IndicadorSeleccionado(View v) {
            // la posicion seleccionada en la lista de reportes
            final int selectedItemPosition = recyclerView.getChildPosition(v);

            // si ya FILTRO un nombre con el spinner
            if (spnFiltro.getSelectedItemPosition() > 0) {

                //muestra el dialogo con las opciones
                new AlertDialog.Builder(getContext())
                        .setTitle("Opciones")
                        .setMessage("Has seleccionado a " + spnFiltro.getSelectedItem().toString() +
                                " ¿Que deseas ver?")
                        .setPositiveButton("Promedio Total", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogEvaluacionTotalDisplay();
                            }
                        })
                        .setNegativeButton("Detalles Reporte (" + arrContenido.get(selectedItemPosition).getIdRegistro() + ")", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FragmentDetalleReporte(selectedItemPosition);
                            }
                        })
                        .show();

            } else { //si no ha filtrado con el spinner, lo lleva directo a ver el reporte
                FragmentDetalleReporte(selectedItemPosition);
            }
        }
    }

    private void FragmentDetalleReporte(int IdReporte) {

        Bundle bundle = new Bundle();
        bundle.putInt("idGuardado", arrContenido.get(IdReporte).getIdRegistro());
// set MyFragment Arguments
        FragmentUltimoReporte fReporte = new FragmentUltimoReporte();
        fReporte.setArguments(bundle);
//para cargar el fragment
        FragmentManager FM = getActivity().getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        FT.replace(R.id.fragment_container, fReporte);
        FT.commit();
    }

    private void DialogEvaluacionTotalDisplay() {
        try {
            StringBuilder detalles = new StringBuilder("Total\t\tFecha\n");
            int promediogeneral = 0;
            //recorre el arreglo con los reportes
            for (int i = 0; i < arrContenido.size(); i++) {

                int sumarept = 0;

                //obtine el detalle del reporte para sacar la suma del puntaje
                Cursor cEval = baseDatos.obtenerRegistroWhereArgs(TBL_EVAL_DISPLAY_DET, "idEDE = " + arrContenido.get(i).getIdRegistro());
                //recorre el cursor
                for (cEval.moveToFirst(); !cEval.isAfterLast(); cEval.moveToNext()) {

                    sumarept = sumarept + cEval.getInt(cEval.getColumnIndex("puntaje"));
                }

                promediogeneral = promediogeneral + sumarept;

                detalles.append(sumarept + " \t\t\t" + arrContenido.get(i).getFecha() + "\n");
            }

            detalles.append("\nPROMEDIO: " + (promediogeneral / arrContenido.size()));

            //muestra un dialogo con la sumatoria de los reportes
            new AlertDialog.Builder(getActivity())
                    .setTitle("Evaluación de " + spnFiltro.getSelectedItem().toString())
                    .setMessage(detalles)
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean GuardarArchivoExcel() {
        boolean archivoGuardado = false;
        try {

            // check if available and not read only
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Log.e("Error", "Storage not available or read only");
                return false;
            }

            int posicFila = 0;

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            workbook.setSheetName(0, spnFiltro.getSelectedItem().toString());
            // Generate column headings
            HSSFRow hRow = null;
            HSSFCell hCell = null;

            //oculta la grilla de las celdas
            sheet.setDisplayGridlines(false);

            // create 2 fonts objects
            HSSFFont fmainhead = workbook.createFont();
            HSSFFont fheader = workbook.createFont();
            HSSFFont ftableheader = workbook.createFont();
            HSSFFont fdata = workbook.createFont();
            // create 3 cell styles
            HSSFCellStyle stmainhead = workbook.createCellStyle();
            HSSFCellStyle sthead = workbook.createCellStyle();
            HSSFCellStyle sttablehead = workbook.createCellStyle();
            HSSFCellStyle stdata = workbook.createCellStyle();
            HSSFCellStyle stdata_centered = workbook.createCellStyle();


            sheet.setColumnWidth(0, (10 * 256));
            sheet.setColumnWidth(1, (20 * 256));
            sheet.setColumnWidth(2, (20 * 256));
            sheet.setColumnWidth(3, (20 * 256));
            sheet.setColumnWidth(4, (20 * 256));
            sheet.setColumnWidth(5, (20 * 256));
            sheet.setColumnWidth(6, (20 * 256));
            sheet.setColumnWidth(7, (20 * 256));
            sheet.setColumnWidth(8, (20 * 256));
            sheet.setColumnWidth(9, (20 * 256));
            sheet.setColumnWidth(10, (20 * 256));
            sheet.setColumnWidth(11, (20 * 256));

            fmainhead.setBoldweight(Font.BOLDWEIGHT_BOLD);
            fmainhead.setFontHeightInPoints((short) 15);
            fmainhead.setColor(HSSFColor.WHITE.index);
            fmainhead.setBold(true);

            fheader.setBoldweight(Font.BOLDWEIGHT_BOLD);
            fheader.setFontHeightInPoints((short) 12);
            fheader.setColor(HSSFColor.BLACK.index);

            ftableheader.setBoldweight(Font.BOLDWEIGHT_BOLD);
            ftableheader.setFontHeightInPoints((short) 12);

            fdata.setColor(HSSFColor.BLACK.index);
            fdata.setFontHeightInPoints((short) 10);

            stmainhead.setFont(fmainhead);
            stmainhead.setAlignment(CellStyle.ALIGN_CENTER);
            stmainhead.setFillPattern(CellStyle.SOLID_FOREGROUND);
            stmainhead.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
            stmainhead.setAlignment(CellStyle.ALIGN_CENTER);
            stmainhead.setBorderBottom(CellStyle.BORDER_THICK);
            stmainhead.setBorderLeft(CellStyle.BORDER_THICK);
            stmainhead.setBorderRight(CellStyle.BORDER_THICK);
            stmainhead.setBorderTop(CellStyle.BORDER_THICK);

            sthead.setAlignment(CellStyle.ALIGN_RIGHT);
            sthead.setFont(fheader);
            sthead.setVerticalAlignment(CellStyle.VERTICAL_CENTER);


            sttablehead.setFont(ftableheader);
            sttablehead.setWrapText(true);
            sttablehead.setAlignment(CellStyle.ALIGN_CENTER);
            sttablehead.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            sttablehead.setBorderBottom(CellStyle.BORDER_MEDIUM);
            sttablehead.setBorderLeft(CellStyle.BORDER_MEDIUM);
            sttablehead.setBorderRight(CellStyle.BORDER_MEDIUM);
            sttablehead.setBorderTop(CellStyle.BORDER_MEDIUM);

            stdata.setFont(fdata);
            stdata.setWrapText(true);
            stdata.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            stdata.setBorderBottom(CellStyle.BORDER_THIN);
            stdata.setBorderLeft(CellStyle.BORDER_THIN);
            stdata.setBorderRight(CellStyle.BORDER_THIN);
            stdata.setBorderTop(CellStyle.BORDER_THIN);

            stdata_centered.setFont(fdata);
            stdata_centered.setAlignment(CellStyle.ALIGN_CENTER);
            stdata_centered.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            stdata_centered.setBorderBottom(CellStyle.BORDER_THIN);
            stdata_centered.setBorderLeft(CellStyle.BORDER_THIN);
            stdata_centered.setBorderRight(CellStyle.BORDER_THIN);
            stdata_centered.setBorderTop(CellStyle.BORDER_THIN);

            // Merges the cells
            CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, 11);
            sheet.addMergedRegion(cellRangeAddress);

            hRow = sheet.createRow(posicFila++);
            hCell = hRow.createCell(0);
            hCell.setCellStyle(stmainhead);
            hCell.setCellValue("REPORTE DE EVALUACION PARA " + spnFiltro.getSelectedItem().toString());

            posicFila = posicFila + 2;
            hRow = sheet.createRow(posicFila++);
            hCell = hRow.createCell(0);
            hCell.setCellStyle(sttablehead);
            hCell.setCellValue("FECHA");

            String[] indicadores = new String[]{
                    "CONTROL DE DINAMICA COMERCIAL",
                    "LLENADO DE CATEGORIA PRIORIDAD PESO/VENTA TOP 10",
                    "CHECK OUT",
                    "INTERACCION CON PDV COMUNICACION ACTIVA",
                    "REPORTE DE RIESGO VENC/APLICAN ESTRATEGIA",
                    "HORARIO/PRESENTACION/SEGURIDAD/DISCIPLINA",
                    "MANTENIMIENTO DE ROTULACION",
                    "SEGUIMIENTO A MODULARES EJECUCION DE REPORTE ITEM SIN",
                    "MOV/ BLOQUEADO / NEGATIVA",
                    "LINEA DE FRIO"};

            if (indicadores.length > 0) {
                for (int i = 0; i < indicadores.length; i++) {
                    hCell = hRow.createCell(i + 1);
                    hCell.setCellStyle(sttablehead);
                    hCell.setCellValue(indicadores[i]);
                }
            }
            hCell = hRow.createCell(11);
            hCell.setCellStyle(sttablehead);
            hCell.setCellValue("TOTAL");

            //SE AGREGAN LOS REFGISTROS DEL REPORTE
            for (int i = 0; i < arrContenido.size(); i++) {

                int sumarept = 0;
                int columna = 0;

                //EN LA PRIMER columna se pone la fecha
                hRow = sheet.createRow(posicFila++);
                hCell = hRow.createCell(columna++);
                hCell.setCellStyle(stdata);
                hCell.setCellValue(arrContenido.get(i).getFecha());

                //en las sig columnas los puntajes
                Cursor cEval = baseDatos.obtenerRegistroWhereArgs(TBL_EVAL_DISPLAY_DET, "idEDE = " + arrContenido.get(i).getIdRegistro());
                //recorre el cursor
                for (cEval.moveToFirst(); !cEval.isAfterLast(); cEval.moveToNext()) {

                    hCell = hRow.createCell(columna++);
                    hCell.setCellStyle(stdata);
                    hCell.setCellValue(cEval.getString(cEval.getColumnIndex("puntaje")));

                    sumarept = sumarept + cEval.getInt(cEval.getColumnIndex("puntaje"));
                }

                hCell = hRow.createCell(columna++);
                hCell.setCellStyle(stdata);
                hCell.setCellValue(String.valueOf(sumarept));

            }


            // Create a path where we will place our List of objects on external storage
            File file = new File(getActivity().getExternalFilesDir(null), "ReporteEvaluacionDisplay_" + new SimpleDateFormat("dd_MM_yyyy").format(new Date()) + ".xls");
            FileOutputStream os = null;

            try {
                os = new FileOutputStream(file);
                workbook.write(os);
                Log.w("FileUtils", "Writing file" + file);
                archivoGuardado = true;
                Toast.makeText(getContext(), "Se ha generado el archivo con éxito.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.w("FileUtils", "Error writing " + file, e);
                Toast.makeText(getContext(), "Error writing " + file + ", error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.w("FileUtils", "Failed to save file", e);
                Toast.makeText(getContext(), "Failed to save file " + file + ", error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                try {
                    if (null != os)
                        os.close();
                } catch (Exception ex) {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return archivoGuardado;
        }

        return archivoGuardado;
    }
}
