package edu.eci.arep.Spark;

import java.util.HashMap;

public class CreatedResponse extends Response{

    public CreatedResponse() {
        super("Creado exitosamente","Created","201", new HashMap<>());
    }

}