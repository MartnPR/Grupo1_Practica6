package pkBusinessLogic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pkDataAccess.pkDAO.FacturaDAO;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Reporte;

public class GeneradorGrafico {

    private final FacturaDAO facturaDAO;

    public GeneradorGrafico() {

        this.facturaDAO =
                new FacturaDAO();
    }

    public Map<String, Integer>
            generarDiagramaBarraPedidos() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        Map<String, Integer> pedidosPorMetodo =
                new LinkedHashMap<>();

        if (facturas == null) {
            return pedidosPorMetodo;
        }

        for (Factura factura : facturas) {

            String metodo =
                    factura.getMetodoPago();

            if (metodo == null || metodo.isBlank()) {
                metodo = "NO ESPECIFICADO";
            }

            pedidosPorMetodo.put(
                    metodo,
                    pedidosPorMetodo.getOrDefault(
                            metodo,
                            0
                    ) + 1
            );
        }

        return pedidosPorMetodo;
    }

    public Map<String, Double>
            generarDiagramaPastel() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        Map<String, Double> ventasPorMetodo =
                new LinkedHashMap<>();

        if (facturas == null) {
            return ventasPorMetodo;
        }

        for (Factura factura : facturas) {

            String metodo =
                    factura.getMetodoPago();

            if (metodo == null || metodo.isBlank()) {
                metodo = "NO ESPECIFICADO";
            }

            double valor =
                    factura.getTotal()
                    - factura.getDescuento();

            ventasPorMetodo.put(
                    metodo,
                    ventasPorMetodo.getOrDefault(
                            metodo,
                            0.0
                    ) + valor
            );
        }

        return ventasPorMetodo;
    }

    public Reporte generarReporte() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        int cantidadPedidos = 0;
        double totalVentas = 0;

        if (facturas != null) {

            cantidadPedidos =
                    facturas.size();

            for (Factura factura : facturas) {

                totalVentas +=
                        factura.getTotal()
                        - factura.getDescuento();
            }
        }

        Reporte reporte =
                new Reporte();

        reporte.setTotalPedidos(
                cantidadPedidos
        );

        reporte.setTotalVentas(
                totalVentas
        );

        return reporte;
    }
}