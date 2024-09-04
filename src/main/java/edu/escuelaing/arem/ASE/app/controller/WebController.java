package edu.escuelaing.arem.ASE.app.controller;

import edu.escuelaing.arem.ASE.app.service.StaticService;
import edu.escuelaing.arem.ASE.app.tools.GetMapping;
import edu.escuelaing.arem.ASE.app.tools.Response;
import edu.escuelaing.arem.ASE.app.tools.RestController;

import java.io.IOException;

@RestController
public class WebController {
    public static StaticService staticService = new StaticService();

    @GetMapping("/")
    public static Response<String> getIndex() throws IOException { return staticService.getResource("index.html");}
}
