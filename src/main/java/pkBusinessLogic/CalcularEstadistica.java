package pkBusinessLogic;

import pkDataAccess.pkDAO.ReservaDAO;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDAO.FacturaDAO;
import pkDataAccess.pkDTO.Reporte;
import pkDataAccess.pkDAO.ReporteDAO;

import java.util.List;

public class CalcularEstadistica {

    private final FacturaDAO facturaDAO;
    private final ReservaDAO reservaDAO;
    private final ReporteDAO reporteDAO;

    public CalcularEstadistica(
            FacturaDAO facturaDAO,
            ReservaDAO reservaDAO,
            ReporteDAO reporteDAO) {

        if (facturaDAO == null) {
            throw new IllegalArgumentException(
                    "FacturaDAO no puede ser nulo."
            );
        }

        if (reservaDAO == null) {
            throw new IllegalArgumentException(
                    "ReservaDAO no puede ser nulo."
            );
        }

        if (reporteDAO == null) {
            throw new IllegalArgumentException(
                    "ReporteDAO no puede ser nulo."
            );
        }

        this.facturaDAO = facturaDAO;
        this.reservaDAO = reservaDAO;
        this.reporteDAO = reporteDAO;
    }

    public double calcularTotalVentas() {

        List<Factura> facturas = facturaDAO.leerTodos();
        double totalVentas = 0;

        if (facturas == null) {
            return totalVentas;
        }

        for (Factura factura : facturas) {
            totalVentas += factura.getTotal()
                    - factura.getDescuento();
        }

        return totalVentas;
    }

    public int calcularTotalComidasVendidas() {

        /*
         * Si cada factura equivale a un pedido, puede
         * utilizarse temporalmente la cantidad de facturas.
         *
         * Si una factura contiene varias comidas,
         * debe sumarse la cantidad de sus detalles.
         */

        List<Factura> facturas = facturaDAO.leerTodos();

        return facturas == null ? 0 : facturas.size();
    }

    public int calcularTotalReservasHechas() {
        return reservaDAO.getCount();
    }

    public Reporte generarReporte() {

        Reporte reporte = new Reporte();

        reporte.setTotalVentas(calcularTotalVentas());
        reporte.setTotalPedidos(
                calcularTotalComidasVendidas()
        );
        reporte.setTotalReservas(
                calcularTotalReservasHechas()
        );

        reporteDAO.agregar(reporte);

        return reporte;
    }
}