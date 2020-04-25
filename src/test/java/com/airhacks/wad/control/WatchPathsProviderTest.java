package com.airhacks.wad.control;

import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class WatchPathsProviderTest {

    @Test
    public void get() {
        String[] args = {"watchPaths=/module1/src/main,/module2/src/main"};

        List<Path> paths = WatchPathsProvider.get(args);

        System.out.println(paths);
    }
}