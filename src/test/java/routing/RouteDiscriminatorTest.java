package routing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RouteDiscriminatorTest {

    static byte bitClear(byte number, int n) {
        return (byte) (number & ~(1 << n));
    }

    static int bitCheck(byte number, int n) {
        return ((number >> n) & 1);
    }

    static byte bitSetTo(byte number, int n, int x) {
        return (byte) ((number & ~(1 << n)) | (x << n));
    }

    static class BitString {
        final byte[] data;

        BitString(byte[] data) { this.data = data; }

        @Override public boolean equals(Object obj) {
            return Arrays.equals(this.data, ((BitString) obj).data);
        }

        @Override public int hashCode() {
            return Arrays.hashCode(this.data);
        }
    }

    static final int ROUTE_LENGTH_BITS = 27 * 8;

    static void main() {
        var routes = List.of(
            "UsersApi.createUser________".getBytes(),
            "UsersApi.deleteUser________".getBytes(),
            "OwnersApi.createUser_______".getBytes(),
            "OwnersApi.changePermissions".getBytes()
        );

        var meaningfulBits = new HashSet<Integer>();
        for(var i = 0; i < ROUTE_LENGTH_BITS; i++)
            meaningfulBits.add(i);

        var bitStrings = routes.stream().map(BitString::new).toList();
        for (var i = 0; i < ROUTE_LENGTH_BITS; i++) {
            var byteIndex = i / 8;
            var bitIndex = i % 8;

            var unique = new HashSet<BitString>();
            boolean allUnique = true;

            for (var bs : bitStrings) {
                bs.data[byteIndex] = bitClear(bs.data[byteIndex], bitIndex);
                if (!unique.add(bs)) { allUnique = false; break; }
            }

            if (allUnique) meaningfulBits.remove(i);
            else {
                for (var j = 0; j < bitStrings.size(); j++) {
                    var bs = bitStrings.get(j);
                    var orig = routes.get(j);
                    // restore bit from original
                    bs.data[byteIndex] = bitSetTo(
                        bs.data[byteIndex], bitIndex, bitCheck(orig[byteIndex], bitIndex)
                    );
                }
            }
        }

        System.out.println(meaningfulBits);
    }
}
