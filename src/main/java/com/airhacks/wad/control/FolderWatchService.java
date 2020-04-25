
package com.airhacks.wad.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author airhacks.com
 */
public interface FolderWatchService {

    long POLLING_INTERVALL = 500;

    String POM = "pom.xml";

    static void listenForChanges(List<Path> dirs, Runnable listener) throws IOException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        checkForChanges(scheduler, dirs, listener);
    }

    static void checkForChanges(ScheduledExecutorService scheduler, List<Path> dirs, Runnable changeListener) {
        long initialStamp = dirs.stream().mapToLong(FolderWatchService::getProjectModificationId).sum();
        boolean changeDetected;
        while (true) {
            try {
                final long previous = initialStamp;
                changeDetected = scheduler.
                        schedule(() -> detectModification(dirs, previous), POLLING_INTERVALL, TimeUnit.MILLISECONDS).
                        get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException("Scheduler error", ex);
        }
        if (changeDetected) {
                changeListener.run();
            initialStamp = dirs.stream().mapToLong(FolderWatchService::getProjectModificationId).sum();
            }
        }
    }

    static long getPomModificationStamp() {
        return getFileSize(Paths.get(POM));
    }

    static boolean detectModification(List<Path> dirs, long previousStamp) {
        long currentStamp = dirs.stream().mapToLong(FolderWatchService::getProjectModificationId).sum();
        return previousStamp != currentStamp;
    }

    static long getProjectModificationId(Path dir) {
        try {
            long modificationId = Files.walk(dir).
                    filter(Files::isRegularFile).
                    mapToLong(FolderWatchService::getFileSize).
                    sum();
            modificationId += getPomModificationStamp();
            return modificationId;
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot list files", ex);
        }
    }

    static long getFileSize(Path p) {
        try {
            return Files.size(p);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot obtain FileTime", ex);
        }
    }


}
