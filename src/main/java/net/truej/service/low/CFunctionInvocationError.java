package net.truej.service.low;

public class CFunctionInvocationError extends RuntimeException {
    public final int errnum;

    public CFunctionInvocationError(int errnum) {
        this.errnum = errnum;
        final String message;
        try {
            message = LibC.strerror(errnum);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        super(message);
    }

}
