package pkBusinessLogic;

import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;
import pkDataAccess.pkDTO.UsuarioPoliComedor;

public class AutenticarUsuario {

    private final UsuarioPoliComedorDAO usuarioDAO;

    public AutenticarUsuario() {
        this.usuarioDAO = new UsuarioPoliComedorDAO();
    }

    public boolean autenticar(
            String metodo,
            String usuario,
            String pasw) {

        if (metodo == null
                || usuario == null
                || usuario.isBlank()) {

            return false;
        }

        switch (metodo.toLowerCase()) {

            case "estudiante":
                return autenticarEstudiante(usuario);

            case "personal":
                return autenticarPersonal(usuario, pasw);

            case "profesor":
                return autenticarProfesor(usuario, pasw);

            default:
                return false;
        }
    }

    private boolean autenticarEstudiante(
            String codigoUnico) {

        UsuarioPoliComedor usuario =
                usuarioDAO.buscarPorCodigoUnico(
                        codigoUnico
                );

        return usuario != null;
    }

    private boolean autenticarPersonal(
            String idUsuario,
            String pasw) {

        if (pasw == null || pasw.isBlank()) {
            return false;
        }

        UsuarioPoliComedor usuario =
                usuarioDAO.buscarPorId(idUsuario);

        return usuario != null
                && usuario.getContrasenia() != null
                && usuario.getContrasenia().equals(pasw);
    }

    private boolean autenticarProfesor(
            String cedula,
            String pasw) {

        if (pasw == null || pasw.isBlank()) {
            return false;
        }

        UsuarioPoliComedor usuario =
                usuarioDAO.buscarPorCedula(cedula);

        return usuario != null
                && usuario.getContrasenia() != null
                && usuario.getContrasenia().equals(pasw);
    }
}