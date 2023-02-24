package edu.eci.arep.SpringBoot;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class MicroSpringBoot {

    ArrayList<String> clases;

        public ArrayList<String> getComponents(ArrayList javaFiles, String path) {
            Path filePath = Paths.get(path);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(filePath)) {
                for (Path files : directoryStream) {
                    if (!Files.isHidden(files)) {
                        if (Files.isDirectory(files)) {
                            getComponents(javaFiles, files.toString());
                        }
                        else if (Files.isRegularFile(files)) {
                            if(files.toString().split("\\.")[1].equals("java")){

                                String className = files.toString().split("java/")[0].replace("/", ".").replace(".java", "");
                                try {
                                    if(Class.forName(className).isAnnotationPresent(Component.class)){
                                        javaFiles.add(className);
                                    }
                                } catch (ClassNotFoundException e) {
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
            }
            return javaFiles;
        }
    }