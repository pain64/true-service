package generate.routing;

import generate.searchtree.SearchTreeAlg;

import java.util.ArrayList;

import static generate.Util.*;

public class RouterGenerator {

    private static final String ROUTE_SEARCH_FUNCTION_NAME = "searchRoute";
    private static final String ROUTER_CLASS_NAME = "Router";

    public RouterGenerator(ArrayList<RouteCfg> routeCfgs) {
        this.routeCfgs = routeCfgs;
    }

    public record RouteCfg(String path, int id, String endpointFunctionName) { }

    public final ArrayList<RouteCfg> routeCfgs;

    public class RouteSearch implements SearchTreeAlg.SearchString {
        public final RouteCfg routeCfg;

        public RouteSearch(RouteCfg routeCfg) {
            this.routeCfg = routeCfg;
        }

        @Override
        public String getString() {
            return routeCfg.path;
        }

        @Override
        public int getId() {
            return routeCfg.id;
        }

    }

    public String getRouteSearchFunction() {
        var code = "";
        code += getLine("private static int " + ROUTE_SEARCH_FUNCTION_NAME + "(RequestByteStream rbs) {", 0);

        var routeStrings = new ArrayList<>(routeCfgs.stream().map(RouteSearch::new).toList());
        var tree = SearchTreeAlg.createSearchTree(routeStrings);
        code += SearchTreeAlg.generateSearchTreeCode(tree, 1);

        code += getLine("return -1;", 1);
        code += getLine("}", 0);
        return code;
    }

    public String getRoutesInvocationCode() {
        var code = "";

        code += getLine("switch (" + ROUTE_SEARCH_FUNCTION_NAME + "(rbs)) {", 0);
        for (var r : routeCfgs) {
            code += getLine("case " + r.id + ":", 1);
            code += getLine(r.endpointFunctionName + "(rbs);", 2);
            code += getLine("break;", 2);
        }
        code += getLine("default:", 1);
        code += getLine("invoke_404(bs);", 2);
        code += getLine("break;", 2);
        code += getLine("}", 0);

        return code;
    }

    public String getRouterClass() {
        var code = "";
        code += getLine("public class " + ROUTER_CLASS_NAME + " {", 0);
        code += getRouteSearchFunction(); code += getLine("", 0);

        code += getLine("public void invoke(RequestByteStream rbs) {", 1);
        code += getRoutesInvocationCode();
        code += getLine("}", 1);

        code += getLine("}", 0);

        return code;
    }


}
