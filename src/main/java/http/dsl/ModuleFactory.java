package http.dsl;

public interface ModuleFactory<C> {
    ModuleConfiguration newInstance(C configuration);
}
