package pkBusinessLogic;

import pkDataAccess.FacturaDAO;
import pkDataAccess.Factura;
import pkDataAccess.Reporte;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GeneradorGrafico {

   private final FacturaDAO facturaDAO;

    public GeneradorGrafico(FacturaDAO facturaDAO) {
        if (facturaDAO == null) {
            throw new IllegalArgumentException(
                   "FacturaDAO no puede ser nulo."
            );
       }

        this.facturaDAO = facturaDAO;
    }

    public Map<String, Integer> generarDiagramaBarraPedidos() {

        List<Factura> facturas = facturaDAO.obtenerTodas();
        Map<String, Integer> pedidosPorMetodo =
                new LinkedHashMap<>();

        if (facturas == null) {
            return pedidosPorMetodo;
        }

       for (Factura factura : facturas) {

            String metodo = factura.getMetodoPago();

           if (metodo == null || metodo.isBlank()) {
                metodo = "NO ESPECIFICADO";
            }

            pedidosPorMetodo.put(
                metodo,
                pedidosPorMetodo.getOrDefault(metodo, 0) + 1
            );
        }

        return pedidosPorMetodo;
    }

    public Map<String, Double> generarDiagramaPastel(){

        List<Factura> facturas = facturaDAO.obtenerTodas();
        Map<String, Double> ventasPorMetodo =
                new LinkedHashMap<>();

        if (facturas == null) {
            return ventasPorMetodo;
        }

        for (Factura factura : facturas) {

           String metodo = factura.getMetodoPago();

            if (metodo == null || metodo.isBlank()) {
               metodo = "NO ESPECIFICADO";
            }

            double valor =
                    factura.getTotal() - factura.getDescuento();

            ventasPorMetodo.put(
                    metodo,
                   ventasPorMetodo.getOrDefault(metodo, 0.0)
                           + valor
           );
        }

        return ventasPorMetodo;
    }

    public Reporte generarReporte() {

        List<Factura> facturas = facturaDAO.obtenerTodas();

        int cantidadPedidos = 0;
        double totalVentas = 0;

        if (facturas != null) {
            cantidadPedidos = facturas.size();

            for (Factura factura : facturas) {
                totalVentas += factura.getTotal()
                        - factura.getDescuento();
            }
        }

        Reporte reporte = new Reporte();
        reporte.setCantidadPedidos(cantidadPedidos);
        reporte.setTotalVentas(totalVentas);

        return reporte;
    }
}