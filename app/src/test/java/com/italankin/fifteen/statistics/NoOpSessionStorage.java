package com.italankin.fifteen.statistics;

import java.io.File;
import java.util.List;
import java.util.Map;

class NoOpSessionStorage extends SessionStorage {

    NoOpSessionStorage(File sessionFile) {
        super(sessionFile);
    }

    @Override
    void write(Map<StatisticsKey, List<StatisticsEntry>> session) {
    }

    @Override
    void read(OnSessionLoaded action) {
    }

    @Override
    void clear() {
    }
}
