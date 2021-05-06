package com.italankin.fifteen.export;

import android.net.Uri;

public interface Exporter {

    String MIME_TYPE = "text/csv";

    char DELIMITER = ';';

    void export(Uri uri, Callback callback);

    String defaultFilename();

    interface Callback {

        void onExportSuccess();

        void onExportError();
    }
}
