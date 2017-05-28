# JHipster Dashboard

This is the [JHipster](http://jhipster.github.io/) dashboard, based on [Hystrix Dashboard](https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard) and its [Spring Cloud integration](http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_circuit_breaker_hystrix_dashboard) integration.

This dashboard is used to monitor **Circuit Breakers**.
To monitor the Hystrix metrics stream of an application point it to the `/hystrix.stream` endpoint. Please note that this application must be annotated with @EnableHystrixDashboard.

For example to monitor circuit breakers for the gateway: 

    http://localhost:8080/hystrix.stream

You can also aggregate several Hystrix streams using the embedded [Turbine](https://github.com/Netflix/Turbine/wiki) (a stream aggregator) in JHipster Dashboard by adding the following file to the registry's central configuration source:

**dashboard.yml**

    turbine:
        aggregator:
            clusterConfig: GATEWAY
        appConfig: gateway

Where turbine.appConfig is a comma separated list of the names of the apps you want to monitor. Then you can access the `GATEWAY` cluster on the following stream:

    http://localhost:8762/turbine.stream?cluster=GATEWAY

For instruction on how to use it please refer to the [Hystrix Dashboard documentation](https://github.com/Netflix/Hystrix/wiki/Dashboard).
