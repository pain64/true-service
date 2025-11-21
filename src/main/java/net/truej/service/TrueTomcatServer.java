package net.truej.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.truej.service.servlet.HttpServletExchange;
import net.truej.service.servlet.HttpServletServer;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.lang.management.ManagementFactory;

public class TrueTomcatServer {
    public static void serve(int port, HttpServletServer config) {

        System.out.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");

        var tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        var ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        Tomcat.addServlet(ctx, "hello", new HttpServlet() {
            @Override protected void service(
                HttpServletRequest request, HttpServletResponse response
            ) {

                config.serve(new HttpServletExchange(request, response));
            }
        });

        ctx.addServletMappingDecoded("/*", "hello");

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }

        System.out.println("time: " + ManagementFactory.getRuntimeMXBean().getUptime() + "ms");

        tomcat.getServer().await();
    }
}
