package gv.haha.auditoria_mp_walmart.clases;

import org.json.JSONObject;

public interface AsyncTaskComplete {
    // Define data you like to return from AysncTask
    public void onAsyncTaskComplete(JSONObject result, int option);


}
