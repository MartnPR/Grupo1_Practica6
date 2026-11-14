package pkBusinessLogic;

import java.util.List;

import pkDataAccess.MesaDAO;
import pkDataAccess.ReservaDAO;
import pkEntity.Estudiante;
import pkDataAccess.Mesa;
import pkDataAccess.Reserva;

public class ReservarMesa {

    private final ReservaDAO reservaDAO;
    private final MesaDAO mesaDAO;

    public ReservarMesa(
            ReservaDAO reservaDAO,
            MesaDAO mesaDAO) {

        if (reservaDAO == null) {
            throw new IllegalArgumentException(
                    "ReservaDAO no puede ser nulo."
            );
        }

        if (mesaDAO == null) {
            throw new IllegalArgumentException(
                    "MesaDAO no puede ser nulo."
            );
        }

        this.reservaDAO = reservaDAO;
        this.mesaDAO = mesaDAO;
    }

    public boolean crearReserva(
            Mesa mesa,
            Estudiante estudiante,
            String franjaHoraria) {

        if (mesa == null || estudiante == null) {
            return false;
        }

        if (franjaHoraria == null
                || franjaHoraria.isBlank()) {

            return false;
        }

        if (!consultarDisponibilidadMesa(mesa)) {
            return false;
        }

        /*
         * Aquí:
         *
         * Reserva reserva = new Reserva();
         * reserva.setMesa(mesa);
         * reserva.setEstudiante(estudiante);
         * reserva.setFranjaHoraria(franjaHoraria);
         *
         * return reservaDAO.guardar(reserva);
         */

        return true;
    }

    public boolean modificarReserva(
            Reserva reserva,
            boolean cancelada) {

        if (reserva == null) {
            return false;
        }

        /*
         * reserva.setCancelada(cancelada);
         * return reservaDAO.actualizar(reserva);
         */

        return true;
    }

    public boolean consultarDisponibilidadMesa(
            Mesa mesa) {

        if (mesa == null) {
            return false;
        }

        /*
         * Aquí consultaríamos MesaDAO
         */

        return true;
    }

    public List<Reserva> consultarHistorialReservas(
            Estudiante estudiante) {

        if (estudiante == null) {
            return List.of();
        }

        /*
         * return reservaDAO.buscarPorEstudiante(
         *     estudiante.getCodigoUnico()
         * );
         */

        return List.of();
    }
}