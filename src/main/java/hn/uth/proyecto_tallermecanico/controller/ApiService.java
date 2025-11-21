package hn.uth.proyecto_tallermecanico.controller;

import hn.uth.proyecto_tallermecanico.model.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // =========================================================================
    // 1. USUARIOS (Endpoints: usuarios/) - PK: doc_identidad
    // =========================================================================

    // MODIFICADO: Paginación Server-Side
    @GET("usuarios/")
    Call<ORDSCollectionResponse<Usuario>> getUsuarios(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // NUEVO: Conteo total para paginación
    @GET("usuarios/total")
    Call<CountResponse> getTotalUsuarios();

    @GET("usuarios/{doc_identidad}")
    Call<Usuario> getUsuario(@Path("doc_identidad") String docIdentidad);

    @POST("usuarios/")
    Call<Void> crearUsuario(@Body Usuario usuario);

    @PUT("usuarios/{doc_identidad}")
    Call<Void> actualizarUsuario(@Path("doc_identidad") String docIdentidad, @Body Usuario usuario);

    @DELETE("usuarios/{doc_identidad}")
    Call<Void> eliminarUsuario(@Path("doc_identidad") String docIdentidad);


    // =========================================================================
    // 2. CLIENTES (Endpoints: clientes/) - PK: doc_identidad
    // =========================================================================

    @GET("clientes/")
    Call<ORDSCollectionResponse<Cliente>> getClientes(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("clientes/total")
    Call<CountResponse> getTotalClientes();

    @GET("clientes/{doc_identidad}")
    Call<Cliente> getCliente(@Path("doc_identidad") String docIdentidad);

    @POST("clientes/")
    Call<Void> crearCliente(@Body Cliente cliente);

    @PUT("clientes/{doc_identidad}")
    Call<Void> actualizarCliente(@Path("doc_identidad") String docIdentidad, @Body Cliente cliente);

    @DELETE("clientes/{doc_identidad}")
    Call<Void> eliminarCliente(@Path("doc_identidad") String docIdentidad);


    // =========================================================================
    // 3. VEHICULOS (Endpoints: vehiculos/) - PK: placa
    // =========================================================================

    @GET("vehiculos/")
    Call<ORDSCollectionResponse<Vehiculo>> getVehiculos(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("vehiculos/total")
    Call<CountResponse> getTotalVehiculos();

    @GET("vehiculos/{placa}")
    Call<Vehiculo> getVehiculo(@Path("placa") String placa);

    @POST("vehiculos/")
    Call<Void> crearVehiculo(@Body Vehiculo vehiculo);

    @PUT("vehiculos/{placa}")
    Call<Void> actualizarVehiculo(@Path("placa") String placa, @Body Vehiculo vehiculo);

    @DELETE("vehiculos/{placa}")
    Call<Void> eliminarVehiculo(@Path("placa") String placa);


    // =========================================================================
    // 4. REPUESTOS (Endpoints: repuestos/) - PK: codigo_sku
    // =========================================================================

    @GET("repuestos/")
    Call<ORDSCollectionResponse<Repuesto>> getRepuestos(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("repuestos/total")
    Call<CountResponse> getTotalRepuestos();

    @GET("repuestos/{sku}")
    Call<Repuesto> getRepuesto(@Path("sku") String sku);

    @POST("repuestos/")
    Call<Void> crearRepuesto(@Body Repuesto repuesto);

    @PUT("repuestos/{sku}")
    Call<Void> actualizarRepuesto(@Path("sku") String sku, @Body Repuesto repuesto);

    @DELETE("repuestos/{sku}")
    Call<Void> eliminarRepuesto(@Path("sku") String sku);


    // =========================================================================
    // 5. ORDENES (Endpoints: ordenes/) - PK: numero_orden
    // =========================================================================

    @GET("ordenes/")
    Call<ORDSCollectionResponse<Orden>> getOrdenes(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("ordenes/total")
    Call<CountResponse> getTotalOrdenes();

    @GET("ordenes/{num_orden}")
    Call<Orden> getOrden(@Path("num_orden") String numOrden);

    @POST("ordenes/")
    Call<Void> crearOrden(@Body Orden orden);

    @PUT("ordenes/{num_orden}")
    Call<Void> actualizarOrden(@Path("num_orden") String numOrden, @Body Orden orden);

    @DELETE("ordenes/{num_orden}")
    Call<Void> eliminarOrden(@Path("num_orden") String numOrden);


    // =========================================================================
    // 6. MATERIALES/REPUESTOS DE ORDEN (Nested) - Sin paginación (lista corta)
    // =========================================================================

    @GET("ordenes/{num_orden}/repuestos/")
    Call<ORDSCollectionResponse<OrdenRepuesto>> getRepuestosDeOrden(@Path("num_orden") String numOrden);

    @POST("ordenes/{num_orden}/repuestos/{sku}")
    Call<Void> agregarRepuestoAOrden(@Path("num_orden") String numOrden, @Path("sku") String sku, @Body OrdenRepuesto ordenRepuesto);

    @DELETE("ordenes/{num_orden}/repuestos/{sku}")
    Call<Void> quitarRepuestoDeOrden(@Path("num_orden") String numOrden, @Path("sku") String sku);


    // =========================================================================
    // 7. MANO DE OBRA (Endpoints: ordenes-mano-obra/) - PK: id_detalle
    // =========================================================================

    // Soporta filtrado por orden Y paginación global
    @GET("ordenes-mano-obra/")
    Call<ORDSCollectionResponse<DetalleOrden>> getDetallesPorOrden(
            @Query("numero_orden") String numeroOrden,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("ordenes-mano-obra/total")
    Call<CountResponse> getTotalDetallesManoObra();

    @GET("ordenes-mano-obra/{id}")
    Call<DetalleOrden> getDetalleOrden(@Path("id") Long idDetalle);

    @POST("ordenes-mano-obra/")
    Call<Void> crearDetalleOrden(@Body DetalleOrden detalleOrden);

    @PUT("ordenes-mano-obra/{id}")
    Call<Void> actualizarDetalleOrden(@Path("id") Long idDetalle, @Body DetalleOrden detalleOrden);

    @DELETE("ordenes-mano-obra/{id}")
    Call<Void> eliminarDetalleOrden(@Path("id") Long idDetalle);


    // =========================================================================
    // 8. ASIGNACIONES (Endpoints: asignaciones/) - PK: id_asignacion
    // =========================================================================

    @GET("asignaciones/")
    Call<ORDSCollectionResponse<Asignacion>> getAsignacionesPorOrden(
            @Query("numero_orden") String numeroOrden,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("asignaciones/total")
    Call<CountResponse> getTotalAsignaciones();

    @GET("asignaciones/{id}")
    Call<Asignacion> getAsignacion(@Path("id") Long idAsignacion);

    @POST("asignaciones/")
    Call<Void> crearAsignacion(@Body Asignacion asignacion);

    @PUT("asignaciones/{id}")
    Call<Void> actualizarAsignacion(@Path("id") Long idAsignacion, @Body Asignacion asignacion);

    @DELETE("asignaciones/{id}")
    Call<Void> eliminarAsignacion(@Path("id") Long idAsignacion);


    // =========================================================================
    // 9. FACTURAS (Endpoints: facturas/) - PK: numero_factura
    // =========================================================================

    @GET("facturas/")
    Call<ORDSCollectionResponse<Factura>> getFacturas(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("facturas/total")
    Call<CountResponse> getTotalFacturas();

    @GET("facturas/{num_factura}")
    Call<Factura> getFactura(@Path("num_factura") String numFactura);

    @POST("facturas/")
    Call<Void> crearFactura(@Body Factura factura);

    @PUT("facturas/{num_factura}")
    Call<Void> actualizarFactura(@Path("num_factura") String numFactura, @Body Factura factura);

    @DELETE("facturas/{num_factura}")
    Call<Void> anularFactura(@Path("num_factura") String numFactura);
}