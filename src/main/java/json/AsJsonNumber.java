package json;

// TODO: magic non-alloc lexer API???
public interface AsJsonNumber {
}

// TODO: map sealed interface and not sealed
// bind interface это всегда json object ???
// pass discriminator function ???
// binding = discriminator(v)
// binding.encode(v)
// .interfaceDiscrimination(
//     MyIface, String.class, s =>
//        "aaa" -> MyImpl1
//        "bbb" -> MyImpl2
// )

// UnionBind2<B, D, V1, V2>