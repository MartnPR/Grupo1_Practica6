package pkBusinessLogic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pkDataAccess.pkDTO.Mesa;
import pkDataAccess.pkDAO.MesaDAO;
import pkDataAccess.pkDTO.Reserva;
import pkDataAccess.pkDAO.ReservaDAO;
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
        List<Mesa> listMesa = new ArrayList<>();
        listMesa.add(mesa);
        reserva.setListMesa(listMesa);
        reserva.setUsuario(usuario);
        reserva.setFranjaHoraria(franjaHoraria);
        reservaDAO.agregar(reserva);

        return true;
    }

    public boolean modificarReserva(
            Reserva reserva,
            boolean cancelada) {

        if (reserva == null) {
            return false;
        }

        reserva.setCancelada(cancelada);
        return reservaDAO.actualizar(reserva);
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
            UsuarioPoliComedor usuario) {

        if (usuario == null) {
            return List.of();
        }

        return reservaDAO.buscarPorEstudiante(usuario.getApellido());
    }
}