package edu.eci.arep.Components;

import edu.eci.arep.SpringBoot.*;
import edu.eci.arep.Services.*;


@Component
public class ServiceManager {
    @RequestMapping("/apps/")
    public static Service wService() {
        return new WebService();
    }

    @RequestMapping("/apps/search")
    public static Service sService() {
        return new SearchService();
    }

    @RequestMapping("/apps/blog")
    public static Service bService() {
        Service service = BlogService.getInstance();
        return service;
    }
}