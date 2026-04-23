package internalapi;


public interface TrueServiceApi {
    /**
     * @return true if connection still alive else false
     */
    boolean handleHttpRequest(CheetahApi cheetah);
}
