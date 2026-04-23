package generate.searchtree;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static generate.Util.getLine;

public class SearchTreeAlg {

    static double stddev(double[] counters) {
        double sum = 0.0;
        for (double x : counters) sum += x;

        var mean = sum / counters.length;

        var standardDeviation = 0.0;
        for (double x : counters)
            standardDeviation += Math.pow(x - mean, 2);

        return Math.sqrt(standardDeviation / counters.length);
    }

    public interface SearchString {
        String getString();
        int getId();
    }

    public sealed interface SearchTree { }

    public record NonFinalTree(double stddev, int j, byte mask, int k, List<SearchTree> childrens) implements SearchTree { }

    public record FinalSearchCandidate(SearchString value) implements SearchTree { }

    public record SearchNotFound() implements SearchTree { }

    static <T extends SearchString> SearchTree searchTreeFor(List<T> strings) {
        var byteStrings = strings.stream().map(s -> s.getString().getBytes(StandardCharsets.UTF_8)).toList();

        if (byteStrings.isEmpty())
            return new SearchNotFound();
        else if (byteStrings.size() == 1)
            return new FinalSearchCandidate(strings.getFirst());

        final int divideBy;
        final int maxK;
        final byte mask;

        if (byteStrings.size() >= 15) {
            divideBy = 16; maxK = 5; mask = (byte) 0b0000_1111;
        } else if (byteStrings.size() >= 7) {
            divideBy = 8; maxK = 6; mask = (byte) 0b0000_0111;
        } else if (byteStrings.size() >= 3) {
            divideBy = 4; maxK = 7; mask = (byte) 0b0000_0011;
        } else {
            divideBy = 2; maxK = 8; mask = (byte) 0b0000_0001;
        }

        var minScore = (Double) null;
        var resultJ = 0;
        var resultK = 0;

        int MIN_ROUTES_LENGTH = byteStrings.stream().map(b -> b.length).min(Integer::compare).get();

        for (var j = 0; j < MIN_ROUTES_LENGTH; j++)
            for (var k = 0; k < maxK; k++) {
                var counters = new double[divideBy];

                for (var s : byteStrings)
                    counters[(s[j] & (mask << k)) >>> k]++; // FIXME: dedup

                var score = stddev(counters);

                if (minScore == null || score < minScore) {
                    minScore = score; resultJ = j; resultK = k;
                }
            }

        var sets = IntStream.range(0, divideBy)
            .mapToObj(i -> new ArrayList<SearchString>()).toList();

        for (var i = 0; i < byteStrings.size(); i++) {
            var s = byteStrings.get(i);
            sets.get((s[resultJ] & (mask << resultK)) >>> resultK).add(strings.get(i));
        }

        return new NonFinalTree(minScore, resultJ, mask, resultK, sets
            .stream().map(SearchTreeAlg::searchTreeFor).toList());
    }

    static int treeDepthMin(SearchTree tree) {
        return switch (tree) {
            case FinalSearchCandidate finalRoute -> 1;
            case NonFinalTree nonFinalTree -> 1 + nonFinalTree.childrens.stream()
                .map(SearchTreeAlg::treeDepthMin)
                .min(Integer::compare).get();
            case SearchNotFound notFoundRoute -> 1;
        };
    }

    static int treeDepthMax(SearchTree tree) {
        return switch (tree) {
            case FinalSearchCandidate finalRoute -> 1;
            case NonFinalTree nonFinalTree -> 1 + nonFinalTree.childrens.stream()
                .map(SearchTreeAlg::treeDepthMax)
                .max(Integer::compare).get();
            case SearchNotFound notFoundRoute -> 1;
        };
    }


    static int treeNodeCount(SearchTree tree) {
        return switch (tree) {
            case FinalSearchCandidate finalRoute -> 1;
            case NonFinalTree nonFinalTree -> 1 + nonFinalTree.childrens.stream()
                .map(SearchTreeAlg::treeDepthMax)
                .reduce(0, Integer::sum);
            case SearchNotFound notFoundRoute -> 1;
        };
    }

    public static <T extends SearchString> SearchTree createSearchTree(ArrayList<T> strings) {
        return searchTreeFor(strings);
    }

    private static String generateSearchTreeCodeInternal(SearchTree tree, int indent) {
        var code = "";

        if (tree instanceof NonFinalTree nft) {
            var switchStartLine = "switch ("+ "(rbs.lookahead(" + nft.j() + ") & ((byte) " + nft.mask() + " << " + nft.k() + ")) >>> " + nft.k() + ") {";
            code += getLine(switchStartLine, indent);

            for (var i = 0; i < nft.childrens().size(); i++) {
                var switchValue = "" + i;
                var caseStartLine = "case " + switchValue + ":";
                code += getLine(caseStartLine, indent + 1);

                code += generateSearchTreeCodeInternal(nft.childrens().get(i), indent + 2);
            }

            code += getLine("}", indent);
        } else if (tree instanceof FinalSearchCandidate(SearchString value)) {
            code += getLine("for (var b: \"" + value.getString() + "\".getBytes(StandardCharsets.UTF_8)) {", indent);
            code += getLine("if (rbs.lookahead(i++) != b) {", indent+1);
            code += getLine("return -1;", indent+2);
            code += getLine("}", indent+1);
            code += getLine("}", indent);
            code += getLine("return "+ value.getId() + " ;", indent);
        } else {

            code += getLine("return -1;", indent);
        }

        return code;
    }

    public static String generateSearchTreeCode(SearchTree tree, int indent) {
        var code = "";
        code += getLine("var i = 0;", indent);
        code += generateSearchTreeCodeInternal(tree, indent);

        return code;
    }

}
