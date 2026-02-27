package http;

import java.util.ArrayList;
import java.util.HashSet;

public class AllowedRequestTargetGenerator {

    public static final class PathNode {
        public final byte[] subValue;
        public final byte[] fullValue;
        private ArrayList<PathNode> nextNodes = new ArrayList<>();

        public PathNode(byte[] subValue, byte[] fullValue) {
            this.subValue = subValue;
            this.fullValue = fullValue;
        }

        public void addNext(PathNode next) {
            this.nextNodes.add(next);
        }

        public ArrayList<PathNode> gNext() {
            return nextNodes;
        }
    }


    public static ArrayList<PathNode> allowedRequestTargetNavigator(ArrayList<String> targets) {
        var proceededTargets = new ArrayList<PathNode>();
        var targetsTokens = new ArrayList<ArrayList<String>>();

        for (var target: targets) {
            var targetTokens = new ArrayList<String>();

            for (var i = 0; i < target.length(); i++) {
                var k = 1;
                if (target.charAt(i) == '/' && i != (target.length()-1)) {
                    while ( target.charAt(i+k) != '/') k++;
                }
                targetTokens.add(target.substring(i, k-1));
            }
            targetsTokens.add(targetTokens);
        }

        var descent = new HashSet<String >();

        for (var targetTokens : targetsTokens) {

        }

        return null;
    }

    public
}
