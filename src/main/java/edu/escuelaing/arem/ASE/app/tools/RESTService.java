package edu.escuelaing.arem.ASE.app.tools;

import java.io.IOException;

public interface RESTService {
    String response(Request request, Response response) throws IOException;
}
