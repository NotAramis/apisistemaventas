package com.sistema.apisistemaventas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Clases DTO para mapear el JSON enviado desde Android
    public static class PedidoDTO {
        public int idCliente;
        public int idEmpleado;
        public double total;
        public String metodoPago;
        public String observaciones;
        public List<DetalleDTO> detalles;
    }

    public static class DetalleDTO {
        public int idProducto;
        public int cantidad;
        public double precio;
    }

    // 1. Obtener Clientes
    @GetMapping("/clientes")
    public List getClientes() {
        return jdbcTemplate.queryForList("SELECT id_cliente, nombre, apellido FROM clientes ORDER BY nombre ASC");
    }

    // 2. Obtener Empleados/Vendedores
    @GetMapping("/empleados")
    public List getEmpleados() {
        return jdbcTemplate.queryForList("SELECT id_empleado, nombre, apellido FROM empleados ORDER BY nombre ASC");
    }

    // 3. Obtener Productos
    @GetMapping("/productos")
    public List getProductos() {
        return jdbcTemplate.queryForList("SELECT id_producto, nombre, precio FROM ventas_productos ORDER BY nombre ASC");
    }

    // 4. Obtener Historial
    @GetMapping("/historial")
    public List getHistorial() {
        return jdbcTemplate.queryForList("SELECT * FROM ordenes_venta");
    }

    // 5. Guardar Pedido por Wi-Fi
    @PostMapping("/guardarPedido")
    @Transactional
    public String guardarPedido(@RequestBody PedidoDTO pedido) {
        try {
            String sqlOrden = "INSERT INTO ordenes_venta (fecha, id_cliente, id_empleado, total, estado, metodo_pago, observaciones) VALUES (CURRENT_DATE(), ?, ?, ?, 'Pendiente', ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, pedido.idCliente);
                ps.setInt(2, pedido.idEmpleado);
                ps.setDouble(3, pedido.total);
                ps.setString(4, pedido.metodoPago);
                ps.setString(5, pedido.observaciones);
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key == null) return "ERROR";
            int idOrden = key.intValue();

            String sqlDetalle = "INSERT INTO orden_venta_detalles (id_orden_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            
            for (DetalleDTO detalle : pedido.detalles) {
                jdbcTemplate.update(sqlDetalle, idOrden, detalle.idProducto, detalle.cantidad, detalle.precio);
            }

            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }
}