package http.dsl;

public class ModuleConfiguration {

    public static class ApiConfiguration extends
        ApiConfigurationBase<ModuleConfiguration, ApiConfiguration.MethodConfiguration> {

        public static class MethodConfiguration extends
            MethodConfigurationBase<ApiConfiguration>{ }
    }


    public ModuleConfiguration websocketApi() {
        return this;
    }

    public ApiConfiguration httpApi(Object apiInstance) {
        return null;
    }

    public ModuleConfiguration submodule(/* ??? */ ModuleConfiguration xxx) {
        return this;
    }


    public static class WithConfiguration { // ???

        public static class ApiConfiguration extends
            ApiConfigurationBase<WithConfiguration, ApiConfiguration.MethodConfiguration> {

            public static class MethodConfiguration extends
                MethodConfigurationBase<ApiConfiguration> {}
        }

        public WithConfiguration websocketApi() { return this; }

        public ApiConfiguration httpApi(Object apiInstance) { return null; }

        public WithConfiguration submodule(/* ??? */ ModuleConfiguration xxx) { return this; }

        public ModuleConfiguration end() { return new ModuleConfiguration(); }
    }

    public WithConfiguration with(Middleware... middlewares) {
        return new WithConfiguration();
    }
}
