package com.italankin.fifteen.export;

import android.net.Uri;

public interface Exporter {

    char DELIMITER = ';';

    void export(Uri uri, Callback callback);

    interface Callback {

        void onExportSuccess();

        void onExportError();
    }
}
