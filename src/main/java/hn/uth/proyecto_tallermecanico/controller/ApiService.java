package hn.uth.proyecto_tallermecanico.controller;

import hn.uth.proyecto_tallermecanico.model.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {

    // =========================================================================
    // 1. USUARIOS (Endpoints: usuarios/) - PK: doc_identidad
    // =========================================================================

    // GET - Obtener todos los usuarios (activos)
    @GET("usuarios/")
    Call<ORDSCollectionResponse<Usuario>> getUsuarios();

    // GET - Obtener un usuario por su PK
    @GET("usuarios/{doc_identidad}")
    Call<Usuario> getUsuario(@Path("doc_identidad") String docIdentidad);

    // POST - Crear un nuevo usuario
    @POST("usuarios/")
    Call<Void> crearUsuario(@Body Usuario usuario);

    // PUT - Actualizar un usuario existente
    @PUT("usuarios/{doc_identidad}")
    Call<Void> actualizarUsuario(@Path("doc_identidad") String docIdentidad, @Body Usuario usuario);

    // DELETE - Desactivar (soft delete) un usuario
    @DELETE("usuarios/{doc_identidad}")
    Call<Void> eliminarUsuario(@Path("doc_identidad") String docIdentidad);


    // =========================================================================
    // 2. CLIENTES (Endpoints: clientes/) - PK: doc_identidad
    // =========================================================================

    // GET - Obtener todos los clientes (activos)
    @GET("clientes/")
    Call<ORDSCollectionResponse<Cliente>> getClientes();

    // GET - Obtener un cliente por su PK
    @GET("clientes/{doc_identidad}")
    Call<Cliente> getCliente(@Path("doc_identidad") String docIdentidad);

    // POST - Crear un nuevo cliente
    @POST("clientes/")
    Call<Void> crearCliente(@Body Cliente cliente);

    // PUT - Actualizar un cliente existente
    @PUT("clientes/{doc_identidad}")
    Call<Void> actualizarCliente(@Path("doc_identidad") String docIdentidad, @Body Cliente cliente);

    // DELETE - Desactivar (soft delete) un cliente
    @DELETE("clientes/{doc_identidad}")
    Call<Void> eliminarCliente(@Path("doc_identidad") String docIdentidad);


    // =========================================================================
    // 3. VEHICULOS (Endpoints: vehiculos/) - PK: placa
    // =========================================================================

    // GET - Obtener todos los vehículos (activos)
    @GET("vehiculos/")
    Call<ORDSCollectionResponse<Vehiculo>> getVehiculos();

    // GET - Obtener un vehículo por su PK
    @GET("vehiculos/{placa}")
    Call<Vehiculo> getVehiculo(@Path("placa") String placa);

    // POST - Crear un nuevo vehículo
    @POST("vehiculos/")
    Call<Void> crearVehiculo(@Body Vehiculo vehiculo);

    // PUT - Actualizar un vehículo existente
    @PUT("vehiculos/{placa}")
    Call<Void> actualizarVehiculo(@Path("placa") String placa, @Body Vehiculo vehiculo);

    // DELETE - Desactivar (soft delete) un vehículo
    @DELETE("vehiculos/{placa}")
    Call<Void> eliminarVehiculo(@Path("placa") String placa);


    // =========================================================================
    // 4. REPUESTOS (Endpoints: repuestos/) - PK: codigo_sku
    // =========================================================================

    // GET - Obtener todos los repuestos (activos)
    @GET("repuestos/")
    Call<ORDSCollectionResponse<Repuesto>> getRepuestos();

    // GET - Obtener un repuesto por su PK
    @GET("repuestos/{sku}")
    Call<Repuesto> getRepuesto(@Path("sku") String sku);

    // POST - Crear un nuevo repuesto
    @POST("repuestos/")
    Call<Void> crearRepuesto(@Body Repuesto repuesto);

    // PUT - Actualizar un repuesto existente
    @PUT("repuestos/{sku}")
    Call<Void> actualizarRepuesto(@Path("sku") String sku, @Body Repuesto repuesto);

    // DELETE - Desactivar (soft delete) un repuesto
    @DELETE("repuestos/{sku}")
    Call<Void> eliminarRepuesto(@Path("sku") String sku);


    // =========================================================================
    // 5. ORDENES (Endpoints: ordenes/) - PK: numero_orden
    // =========================================================================

    // GET - Obtener todas las órdenes (activas)
    @GET("ordenes/")
    Call<ORDSCollectionResponse<Orden>> getOrdenes();

    // GET - Obtener una orden por su PK
    @GET("ordenes/{num_orden}")
    Call<Orden> getOrden(@Path("num_orden") String numOrden);

    // POST - Crear una nueva orden
    @POST("ordenes/")
    Call<Void> crearOrden(@Body Orden orden);

    // PUT - Actualizar una orden existente
    @PUT("ordenes/{num_orden}")
    Call<Void> actualizarOrden(@Path("num_orden") String numOrden, @Body Orden orden);

    // DELETE - Desactivar (soft delete) una orden
    @DELETE("ordenes/{num_orden}")
    Call<Void> eliminarOrden(@Path("num_orden") String numOrden);


    // =========================================================================
    // 6. MATERIALES/REPUESTOS DE ORDEN (Nested: ordenes/{num}/repuestos/{sku})
    // =========================================================================

    // GET - Ver repuestos de una orden específica
    @GET("ordenes/{num_orden}/repuestos/")
    Call<ORDSCollectionResponse<OrdenRepuesto>> getRepuestosDeOrden(@Path("num_orden") String numOrden);

    // POST - Agregar un repuesto a una orden
    @POST("ordenes/{num_orden}/repuestos/{sku}")
    Call<Void> agregarRepuestoAOrden(@Path("num_orden") String numOrden, @Path("sku") String sku, @Body OrdenRepuesto ordenRepuesto);

    // DELETE - Quitar un repuesto de orden
    @DELETE("ordenes/{num_orden}/repuestos/{sku}")
    Call<Void> quitarRepuestoDeOrden(@Path("num_orden") String numOrden, @Path("sku") String sku);


    // =========================================================================
    // 7. MANO DE OBRA (Endpoints: ordenes-mano-obra/) - PK: id_detalle
    // =========================================================================

    // GET - Obtener detalles de mano de obra por orden (usa Query Parameter)
    @GET("ordenes-mano-obra/")
    Call<ORDSCollectionResponse<DetalleOrden>> getDetallesPorOrden(@Query("numero_orden") String numeroOrden);

    // GET - Obtener un detalle por su ID
    @GET("ordenes-mano-obra/{id}")
    Call<DetalleOrden> getDetalleOrden(@Path("id") Long idDetalle);

    // POST - Agregar un detalle de mano de obra
    @POST("ordenes-mano-obra/")
    Call<Void> crearDetalleOrden(@Body DetalleOrden detalleOrden);

    // PUT - Actualizar un detalle de mano de obra
    @PUT("ordenes-mano-obra/{id}")
    Call<Void> actualizarDetalleOrden(@Path("id") Long idDetalle, @Body DetalleOrden detalleOrden);

    // DELETE - Eliminar un detalle de mano de obra
    @DELETE("ordenes-mano-obra/{id}")
    Call<Void> eliminarDetalleOrden(@Path("id") Long idDetalle);


    // =========================================================================
    // 8. ASIGNACIONES (Endpoints: asignaciones/) - PK: id_asignacion
    // =========================================================================

    // GET - Obtener asignaciones por orden (usa Query Parameter)
    @GET("asignaciones/")
    Call<ORDSCollectionResponse<Asignacion>> getAsignacionesPorOrden(@Query("numero_orden") String numeroOrden);

    // GET - Obtener una asignación por su ID
    @GET("asignaciones/{id}")
    Call<Asignacion> getAsignacion(@Path("id") Long idAsignacion);

    // POST - Crear una nueva asignación
    @POST("asignaciones/")
    Call<Void> crearAsignacion(@Body Asignacion asignacion);

    // PUT - Actualizar una asignación existente
    @PUT("asignaciones/{id}")
    Call<Void> actualizarAsignacion(@Path("id") Long idAsignacion, @Body Asignacion asignacion);

    // DELETE - Eliminar una asignación
    @DELETE("asignaciones/{id}")
    Call<Void> eliminarAsignacion(@Path("id") Long idAsignacion);


    // =========================================================================
    // 9. FACTURAS (Endpoints: facturas/) - PK: numero_factura
    // =========================================================================

    // GET - Obtener todas las facturas (activas)
    @GET("facturas/")
    Call<ORDSCollectionResponse<Factura>> getFacturas();

    // GET - Obtener una factura por su PK
    @GET("facturas/{num_factura}")
    Call<Factura> getFactura(@Path("num_factura") String numFactura);

    // POST - Crear una nueva factura
    @POST("facturas/")
    Call<Void> crearFactura(@Body Factura factura);

    // PUT - Actualizar una factura existente
    @PUT("facturas/{num_factura}")
    Call<Void> actualizarFactura(@Path("num_factura") String numFactura, @Body Factura factura);

    // DELETE - Anular (soft delete/update) una factura
    @DELETE("facturas/{num_factura}")
    Call<Void> anularFactura(@Path("num_factura") String numFactura);
}