package android.primer.bryanalvarez.captura_info_gt;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

public class Initializer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.gyt_Captura_Info_G&T";
    }
}
