package http.generate;

import java.util.HashMap;

public class EndpointGenerator {



    // "middleware" types
        // some
        // decorator

    // header variables controller
    public HashMap<String, String> headersVariables;

    // variable counter
    public int variableCounter = 0;

    // body inited
    public boolean isBodyVariableInit = false;

    // create endpoint code expecting that request-line has already read
    public void createEndpoint(
        String path,
        String method,

    ) {
        // preprocess
        // got header list

        // do headers parsing


        // read request

        // do middleware

            // read body on demand

            // do middleware ...

            // do endpoint code

            // write response

        // do middleware


    }
}
