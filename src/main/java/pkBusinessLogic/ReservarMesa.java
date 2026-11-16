package pkBusinessLogic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pkDataAccess.pkDAO.MesaDAO;
import pkDataAccess.pkDAO.ReservaDAO;
import pkDataAccess.pkDTO.Mesa;
import pkDataAccess.pkDTO.Reserva;
import pkDataAccess.pkDTO.UsuarioPoliComedor;

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
            UsuarioPoliComedor usuario,
            String franjaHoraria) {

        if (mesa == null || usuario == null) {
            return false;
        }

        if (franjaHoraria == null
                || franjaHoraria.isBlank()) {

            return false;
        }

        if (!consultarDisponibilidadMesa(mesa)) {
            return false;
        }

        Reserva reserva = new Reserva();

        reserva.setFechaCreacion(new Date());
        reserva.setFranjaHoraria(franjaHoraria);

        /*
         * Temporalmente se mantiene List<Mesa>
         * porque el DTO Reserva actual todavía
         * utiliza esta estructura.
         *
         * El equipo acordó cambiar posteriormente
         * esta relación para que cada Reserva tenga
         * una sola Mesa.
         */
        List<Mesa> mesas = new ArrayList<>();
        mesas.add(mesa);

        reserva.setListMesa(mesas);
        reserva.setUsuario(usuario);

        /*
         * ReservaDAO todavía no persiste la relación
         * Reserva-Mesa. Esto queda pendiente de la
         * modificación de Data Access.
         */
        reservaDAO.agregar(reserva);

        /*
         * La mesa sí puede actualizarse actualmente
         * mediante MesaDAO.
         */
        return marcarMesaReservada(mesa, true);
    }

    public boolean consultarDisponibilidadMesa(
            Mesa mesa) {

        if (mesa == null
                || mesa.getIdMesa() == null) {

            return false;
        }

        Mesa mesaRegistrada =
                mesaDAO.buscarPorId(
                        mesa.getIdMesa()
                );

        if (mesaRegistrada == null) {
            return false;
        }

        return !mesaRegistrada.isReservada();
    }

    private boolean marcarMesaReservada(
            Mesa mesa,
            boolean reservada) {

        if (mesa == null
                || mesa.getIdMesa() == null) {

            return false;
        }

        Mesa mesaRegistrada =
                mesaDAO.buscarPorId(
                        mesa.getIdMesa()
                );

        if (mesaRegistrada == null) {
            return false;
        }

        mesaRegistrada.setReservada(reservada);

        return mesaDAO.actualizar(
                mesaRegistrada
        );
    }

    public boolean cancelarReserva(
            Reserva reserva,
            Mesa mesa) {

        if (reserva == null
                || reserva.getIdReserva() == null
                || mesa == null) {

            return false;
        }

        Reserva eliminada =
                reservaDAO.eliminar(
                        reserva.getIdReserva()
                );

        if (eliminada == null) {
            return false;
        }

        return marcarMesaReservada(
                mesa,
                false
        );
    }

    public List<Reserva> consultarHistorialReservas(
            String codigoEstudiante) {

        if (codigoEstudiante == null
                || codigoEstudiante.isBlank()) {

            return List.of();
        }

        return reservaDAO.buscarPorEstudiante(
                codigoEstudiante
        );
    }
}