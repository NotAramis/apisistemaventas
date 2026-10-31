# API REST - Sistema de Ventas

API desarrollada en **Spring Boot** para la gestión de ventas, integrada con base de datos MySQL local y consumo desde aplicación móvil Android.

## Endpoints Disponibles
- `GET /api/clientes` - Obtener catálogo de clientes
- `GET /api/empleados` - Obtener catálogo de empleados
- `GET /api/productos` - Obtener catálogo de productos
- `GET /api/historial` - Obtener historial de órdenes de venta
- `POST /api/guardarPedido` - Registrar una nueva orden de venta transaccional
