package pkBusinessLogic;

import java.util.Date;
import java.util.List;

import pkDataAccess.pkDAO.MesaDAO;
import pkDataAccess.pkDAO.ReservaDAO;
import pkDataAccess.pkDTO.Mesa;
import pkDataAccess.pkDTO.Reserva;
import pkDataAccess.pkDTO.UsuarioPoliComedor;
import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;

public class ReservarMesa {

    private final ReservaDAO reservaDAO;
    private final MesaDAO mesaDAO;
    private final UsuarioPoliComedorDAO usuarioDAO;

    public ReservarMesa() {

        this.reservaDAO =
                new ReservaDAO();

        this.mesaDAO =
                new MesaDAO();
        this.usuarioDAO =
                new UsuarioPoliComedorDAO();
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
        

        reserva.setMesa(mesa);
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

        return consultarDisponibilidadMesa(mesa.getIdMesa());
    }

    public boolean consultarDisponibilidadMesa(int idMesa) {
        Mesa mesaRegistrada = mesaDAO.buscarPorId(idMesa);

        if (mesaRegistrada == null) {
            return false;
        }

        return !mesaRegistrada.isReservada();
    }

    public boolean consultarDisponibilidadMesa(String mesaEnVista) {
        if (mesaEnVista == null) {
            return false;
        }

        int separador = mesaEnVista.indexOf(" | ");
        if (separador <= 0) {
            return false;
        }

        try {
            return consultarDisponibilidadMesa(
                    Integer.parseInt(mesaEnVista.substring(0, separador)));
        } catch (NumberFormatException error) {
            return false;
        }
    }

    public List<String> listarMesasParaVista() {
        return mesaDAO.leerTodos().stream()
                .filter(mesa -> !mesa.isReservada())
                .map(mesa -> "%d | Mesa %d — %s".formatted(
                        mesa.getIdMesa(),
                        mesa.getIdMesa(),
                        mesa.isReservada() ? "reservada" : "disponible"))
                .toList();
    }

    public boolean reservarMesa(
            String mesaEnVista,
            String credencialUsuario,
            String franjaHoraria) {
        if (mesaEnVista == null
                || credencialUsuario == null
                || credencialUsuario.isBlank()
                || franjaHoraria == null
                || franjaHoraria.isBlank()) {
            return false;
        }

        int separador = mesaEnVista.indexOf(" | ");
        if (separador <= 0) {
            return false;
        }

        final int idMesa;
        try {
            idMesa = Integer.parseInt(
                    mesaEnVista.substring(0, separador));
        } catch (NumberFormatException error) {
            return false;
        }

        UsuarioPoliComedor usuario =
                usuarioDAO.buscarPorId(
                        credencialUsuario.trim());
        if (usuario == null) {
            try {
                usuario = usuarioDAO.buscarPorCodigoUnico(
                        credencialUsuario.trim());
            } catch (IllegalArgumentException ignored) {
                usuario = null;
            }
        }
        if (usuario == null) {
            usuario = usuarioDAO.buscarPorCedula(
                    credencialUsuario.trim());
        }
        if (usuario == null) {
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(idMesa);
        return crearReserva(mesa, usuario, franjaHoraria.trim());
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