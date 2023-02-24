package edu.eci.arep.Components;

import edu.eci.arep.SpringBoot.*;
import edu.eci.arep.Services.*;

@Component
public class FileManager {
    @RequestMapping("/file/")
    public static Service files(String path){
        return  new FileReader(path);
    }
}