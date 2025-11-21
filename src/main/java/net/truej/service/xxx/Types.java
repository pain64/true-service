package net.truej.service.xxx;

import lombok.EqualsAndHashCode;

public class Types {
    // Type
    //     package          ,
    //     name             ,
    //     genericParameters,
    //
    //
    //     | LeafType
    //
    //
    //     | RecordType
    //
    //     | UnionType
    //
    // TypeInstance
    //

    // new LeafType("java.util", "List", List.of()))
    @EqualsAndHashCode public static sealed abstract class Type {
        public final boolean isNullable;
        protected Type(boolean isNullable) { this.isNullable = isNullable; }
    }

    @EqualsAndHashCode(callSuper = true) public static final class RecordType extends Type {
        public RecordType(boolean isNullable) {
            super(isNullable);
        }
    }

    @EqualsAndHashCode(callSuper = true) public static final class ScalarType extends Type {
        public ScalarType(boolean isNullable) {
            super(isNullable);
        }
    }
}
