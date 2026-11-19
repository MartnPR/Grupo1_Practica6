package pkBusinessLogic;

import java.util.List;

import pkDataAccess.pkDAO.FacturaDAO;
import pkDataAccess.pkDAO.ReporteDAO;
import pkDataAccess.pkDAO.ReservaDAO;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Reporte;
import pkDataAccess.pkDTO.Reserva;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CalcularEstadistica {

    private final FacturaDAO facturaDAO;
    private final ReservaDAO reservaDAO;
    private final ReporteDAO reporteDAO;

    public CalcularEstadistica() {

        this.facturaDAO =
                new FacturaDAO();

        this.reservaDAO =
                new ReservaDAO();

        this.reporteDAO =
                new ReporteDAO();
    }

    public double calcularTotalVentas() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        double totalVentas = 0;

        if (facturas == null) {
            return totalVentas;
        }

        for (Factura factura : facturas) {

            totalVentas +=
                    factura.getTotal()
                    - factura.getDescuento();
        }

        return totalVentas;
    }

    public int calcularTotalComidasVendidas() {

        /*
         * Temporalmente se toma la cantidad
         * de facturas como cantidad de pedidos.
         *
         * Si se requiere la cantidad exacta
         * de alimentos, debe calcularse desde
         * las líneas de factura.
         */

        List<Factura> facturas =
                facturaDAO.leerTodos();

        return facturas == null
                ? 0
                : facturas.size();
    }

    public int calcularTotalReservasHechas() {

        return reservaDAO.getCount();
    }

    public double calcularTotalVentas(String franjaHoraria) {
        if (franjaHoraria == null || franjaHoraria.isBlank()) {
            return 0;
        }

        List<Reserva> reservas =
                reservaDAO.buscarPorFranjaHoraria(franjaHoraria);
        List<Factura> facturas = facturaDAO.leerTodos();
        if (reservas == null || reservas.isEmpty()
                || facturas == null || facturas.isEmpty()) {
            return 0;
        }

        Set<String> fechas = new HashSet<>();
        for (Reserva reserva : reservas) {
            if (reserva.getFechaCreacion() != null) {
                fechas.add(fecha(reserva.getFechaCreacion()));
            }
        }

        double totalVentas = 0;
        for (Factura factura : facturas) {
            if (factura.getFecha() == null) {
                continue;
            }
            if (fechas.contains(fecha(factura.getFecha()))) {
                totalVentas += factura.getTotal() - factura.getDescuento();
            }
        }
        return totalVentas;
    }

    public int calcularTotalReservasHechas(String franjaHoraria) {
        if (franjaHoraria == null || franjaHoraria.isBlank()) {
            return 0;
        }
        List<Reserva> reservas =
                reservaDAO.buscarPorFranjaHoraria(franjaHoraria);
        return reservas == null ? 0 : reservas.size();
    }

    public List<String> consultarFranjasHorarias() {
        List<Reserva> reservas = reservaDAO.leerTodos();
        java.util.LinkedHashSet<String> franjas =
                new java.util.LinkedHashSet<>();
        if (reservas != null) {
            for (Reserva reserva : reservas) {
                if (reserva.getFranjaHoraria() != null
                        && !reserva.getFranjaHoraria().isBlank()) {
                    franjas.add(reserva.getFranjaHoraria().trim());
                }
            }
        }
        return java.util.List.copyOf(franjas);
    }

    public Reporte generarReporte() {

        Reporte reporte = new Reporte();
        double totalVentas = calcularTotalVentas();
        int totalReservas = calcularTotalReservasHechas();
        reporte.setFechaGeneracion(new Date());
        reporte.setFranjaHoraria("Todas");
        reporte.setDescripcion(
                "totalVentas=" + totalVentas
                        + "; totalReservas=" + totalReservas);
        reporte.setTotalVentas(totalVentas);
        reporte.setTotalReservas(totalReservas);
        reporte.setIdReporte(crearIdReporte());
        reporte.setGraficos(false);

        reporteDAO.agregar(reporte);

        return reporte;
    }

    public Reporte generarReporte(
            String franjaHoraria,
            boolean incluirGraficos) {
        if (franjaHoraria == null || franjaHoraria.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una franja horaria.");
        }

        Reporte reporte = new Reporte();
        reporte.setIdReporte(crearIdReporte());
        reporte.setFechaGeneracion(new Date());
        reporte.setFranjaHoraria(franjaHoraria);
        reporte.setGraficos(incluirGraficos);
        double totalVentas = calcularTotalVentas(franjaHoraria);
        int totalReservas =
                calcularTotalReservasHechas(franjaHoraria);
        reporte.setDescripcion(
                "totalVentas=" + totalVentas
                        + "; totalReservas=" + totalReservas);
        reporte.setTotalVentas(totalVentas);
        reporte.setTotalReservas(totalReservas);

        reporteDAO.agregar(reporte);
        return reporte;
    }

    private String crearIdReporte() {
        int correlativo = reporteDAO.getCount() + 1;
        String id;
        do {
            id = "Reporte " + correlativo++;
        } while (reporteDAO.buscarPorId(id) != null);
        return id;
    }

    private String fecha(Date fecha) {
        java.util.Calendar calendario =
                java.util.Calendar.getInstance();
        calendario.setTime(fecha);
        return calendario.get(java.util.Calendar.YEAR) + "-"
                + (calendario.get(java.util.Calendar.MONTH) + 1) + "-"
                + calendario.get(java.util.Calendar.DAY_OF_MONTH);
    }
}