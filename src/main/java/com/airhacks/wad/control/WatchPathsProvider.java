package com.airhacks.wad.control;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WatchPathsProvider {

    public static List<Path> get(String[] args) {
        Optional<String> watchPaths = Arrays.stream(args).filter(arg -> arg.startsWith("watchPaths=")).findFirst();

        return watchPaths.map(s -> Arrays.stream(s.split("=")[1].split(","))
                .map(path -> Paths.get(path))
                .collect(Collectors.toList()))
                .orElseGet(() -> Collections.singletonList(Paths.get("./src/main")));
    }
}
