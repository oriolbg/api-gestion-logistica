package erp.gestion.apilogistica.testcontainers;

import erp.gestion.apilogistica.entity.*;
import erp.gestion.apilogistica.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración real contra MySQL (Testcontainers) para {@link PedidoRepository}.
 *
 * <p>Valida la persistencia de Pedidos con sus Detalles (cascade ALL + orphanRemoval),
 * la consulta por correlativo y la paginación por fechas.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests de Integración (Testcontainers/MySQL) - PedidoRepository")
class PedidoRepositoryIntegrationTest extends AbstractMySQLContainerTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TipoComprobanteRepository tipoComprobanteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    private Cliente cliente;
    private TipoComprobante tipoComprobante;
    private Producto producto;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        productoRepository.deleteAll();
        clienteRepository.deleteAll();
        tipoComprobanteRepository.deleteAll();
        unidadRepository.deleteAll();

        // Datos base necesarios por las FK de Pedido
        // Unidad: ID es String (no auto-generado), requiere id y descripcion
        Unidad unidad = unidadRepository.save(
                Unidad.builder()
                        .id("UND")
                        .descripcion("Unidad")
                        .build()
        );

        // Cliente: requiere tipoDocumento y numeroDocumento (nullable=false, unique)
        cliente = clienteRepository.save(
                Cliente.builder()
                        .nombre("Empresa Demo S.A.")
                        .tipoDocumento(TipoDocumento.RUC)
                        .numeroDocumento("20123456789")
                        .email("demo@empresa.com")
                        .build()
        );

        // TipoComprobante: codigo es el @Id (String)
        tipoComprobante = tipoComprobanteRepository.save(
                TipoComprobante.builder()
                        .codigo("F1")
                        .descripcion("Factura")
                        .build()
        );

        // Producto: todos los campos nullable=false son obligatorios
        producto = productoRepository.save(
                Producto.builder()
                        .codigo("PROD-001")
                        .descripcion("Producto de prueba para test")
                        .unidad(unidad)
                        .codImp("10")
                        .precioBase(new BigDecimal("45.00"))
                        .precioUnitario(new BigDecimal("50.00"))
                        .activo(true)
                        .build()
        );
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Pedido buildPedidoConDetalle(int correlativo, BigDecimal total) {
        PedidoDetalle detalle = PedidoDetalle.builder()
                .producto(producto)
                .cantidad(2)
                .precioUnitario(new BigDecimal("50.00"))
                .subtotal(new BigDecimal("100.00"))
                .build();

        Pedido pedido = Pedido.builder()
                .fecha(LocalDate.now())
                .cliente(cliente)
                .tipoComprobante(tipoComprobante)
                .serie("F001")
                .correlativo(correlativo)
                .total(total)
                .detalles(new ArrayList<>())
                .build();

        detalle.setPedido(pedido);
        pedido.getDetalles().add(detalle);

        return pedido;
    }

    // =========================================================================
    // Persistencia básica
    // =========================================================================

    @Nested
    @DisplayName("Persistencia Pedido + Detalles")
    class PersistenciaBasica {

        @Test
        @DisplayName("Debe persistir un Pedido con su detalle y recuperarlos por ID")
        void persistirPedidoConDetalle_Exitoso() {
            Pedido pedido = buildPedidoConDetalle(1, new BigDecimal("100.00"));
            Pedido saved = pedidoRepository.save(pedido);

            assertNotNull(saved.getId());

            Optional<Pedido> found = pedidoRepository.findById(saved.getId());
            assertTrue(found.isPresent());
            assertEquals("F001", found.get().getSerie());
            assertEquals(1, found.get().getCorrelativo());
            assertEquals(0, new BigDecimal("100.00").compareTo(found.get().getTotal()));
        }

        @Test
        @DisplayName("El cascade ALL debe persistir los detalles junto con el pedido")
        void cascadeAll_PersistirDetallesAutomaticamente() {
            Pedido pedido = buildPedidoConDetalle(2, new BigDecimal("100.00"));
            Pedido saved = pedidoRepository.save(pedido);

            // El detalle debe haberse guardado con cascade sin llamar explícitamente a PedidoDetalleRepository
            assertFalse(saved.getDetalles().isEmpty());
            assertNotNull(saved.getDetalles().get(0).getId());
        }

        @Test
        @DisplayName("Debe eliminar el pedido y sus detalles por orphanRemoval")
        void orphanRemoval_EliminarDetallesConPedido() {
            Pedido pedido = buildPedidoConDetalle(3, new BigDecimal("100.00"));
            Pedido saved = pedidoRepository.save(pedido);
            Long pedidoId = saved.getId();

            pedidoRepository.deleteById(pedidoId);

            assertTrue(pedidoRepository.findById(pedidoId).isEmpty());
        }

        @Test
        @DisplayName("Debe actualizar el total del pedido en base de datos")
        void actualizarTotal_PersistenteEnBD() {
            Pedido pedido = buildPedidoConDetalle(4, new BigDecimal("100.00"));
            Pedido saved = pedidoRepository.save(pedido);

            saved.setTotal(new BigDecimal("250.00"));
            Pedido updated = pedidoRepository.save(saved);

            assertEquals(0, new BigDecimal("250.00").compareTo(updated.getTotal()));
        }
    }

    // =========================================================================
    // findByCorrelativo
    // =========================================================================

    @Nested
    @DisplayName("findByCorrelativo()")
    class FindByCorrelativo {

        @Test
        @DisplayName("Debe retornar pedido cuando el correlativo coincide")
        void correlativoExistente_RetornaPedido() {
            pedidoRepository.save(buildPedidoConDetalle(100, new BigDecimal("100.00")));

            Page<Pedido> result = pedidoRepository.findByCorrelativo(
                    PageRequest.of(0, 10), "100");

            assertEquals(1, result.getTotalElements());
            assertEquals(100, result.getContent().get(0).getCorrelativo());
        }

        @Test
        @DisplayName("Debe retornar página vacía si el correlativo no existe")
        void correlativoInexistente_PaginaVacia() {
            Page<Pedido> result = pedidoRepository.findByCorrelativo(
                    PageRequest.of(0, 10), "9999");

            assertEquals(0, result.getTotalElements());
        }
    }

    // =========================================================================
    // findByFechaBetween
    // =========================================================================

    @Nested
    @DisplayName("findByFechaBetween()")
    class FindByFechaBetween {

        @Test
        @DisplayName("Debe retornar pedidos dentro del rango de fechas")
        void pedidoDentroRango_EsRetornado() {
            // Pedido hoy
            Pedido pedidoHoy = buildPedidoConDetalle(5, new BigDecimal("100.00"));
            pedidoHoy.setFecha(LocalDate.now());
            pedidoRepository.save(pedidoHoy);

            // Pedido hace 2 años (fuera del rango)
            Pedido pedidoViejo = buildPedidoConDetalle(6, new BigDecimal("100.00"));
            pedidoViejo.setFecha(LocalDate.now().minusYears(2));
            pedidoRepository.save(pedidoViejo);

            LocalDate inicio = LocalDate.now().minusDays(1);
            LocalDate fin    = LocalDate.now().plusDays(1);

            Page<Pedido> result = pedidoRepository.findByFechaBetween(
                    PageRequest.of(0, 10), inicio, fin);

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Debe retornar vacío cuando no hay pedidos en el rango")
        void sinPedidosEnRango_PaginaVacia() {
            Pedido pedido = buildPedidoConDetalle(7, new BigDecimal("100.00"));
            pedido.setFecha(LocalDate.now().minusYears(5));
            pedidoRepository.save(pedido);

            LocalDate inicio = LocalDate.now().minusDays(10);
            LocalDate fin    = LocalDate.now().minusDays(1);

            Page<Pedido> result = pedidoRepository.findByFechaBetween(
                    PageRequest.of(0, 10), inicio, fin);

            assertEquals(0, result.getTotalElements());
        }
    }

    // =========================================================================
    // Paginación
    // =========================================================================

    @Nested
    @DisplayName("Paginación findAll")
    class Paginacion {

        @Test
        @DisplayName("Debe paginar correctamente con múltiples pedidos")
        void paginacion_MultiplesPedidos() {
            for (int i = 10; i <= 14; i++) {
                pedidoRepository.save(buildPedidoConDetalle(i, new BigDecimal("100.00")));
            }

            Page<Pedido> page0 = pedidoRepository.findAll(PageRequest.of(0, 2));
            Page<Pedido> page1 = pedidoRepository.findAll(PageRequest.of(1, 2));

            assertEquals(2, page0.getNumberOfElements());
            assertEquals(2, page1.getNumberOfElements());
            assertEquals(5, page0.getTotalElements());
            assertEquals(3, page0.getTotalPages()); // ceil(5/2)
        }
    }
}
