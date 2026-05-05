/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) {
        String origin = requestContext.getHeaderString("Origin");

        if (origin != null && (
                origin.equals("http://localhost:5173")
                || origin.equals("http://localhost:3000")
                || origin.equals("http://localhost:8080")
        )) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().putSingle("Vary", "Origin");
            responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with");
        }
    }
}