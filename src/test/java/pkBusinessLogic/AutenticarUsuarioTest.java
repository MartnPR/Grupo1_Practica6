package pkBusinessLogic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pkDataAccess.pkDAO.ConnectionDB;
import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;
import pkDataAccess.pkDTO.UsuarioPoliComedor;

class AutenticarUsuarioTest {

    @Test
    void autenticaSoloCuandoExisteLaCuentaYCoincideLaPassword() {
        UsuarioPoliComedorDAO dao = usuarioDAO("ana", "secreto");
        AutenticarUsuario autenticacion = new AutenticarUsuario(dao);

        assertTrue(autenticacion.autenticar("Personal", "ana", "secreto"));
        assertFalse(autenticacion.autenticar("Personal", "ana", "incorrecta"));
        assertFalse(autenticacion.autenticar("Personal", "nadie", "secreto"));
        assertFalse(autenticacion.autenticar("Personal", "", "secreto"));
    }

    @Test
    void registraUnaCuentaNuevaPeroNoReemplazaUnaExistente() {
        UsuarioPoliComedorDAO dao = usuarioDAO("ana", "secreto");
        AutenticarUsuario autenticacion = new AutenticarUsuario(dao);

        assertFalse(autenticacion.registrarCuenta(
                "ana", "Ana", "Perez", "nueva"));
        assertTrue(autenticacion.registrarCuenta(
                "luis", "Luis", "Mora", "clave"));
    }

    private static UsuarioPoliComedorDAO usuarioDAO(
            String credencial, String password) {
        return new UsuarioPoliComedorDAO(
                new ConnectionDB(
                        "jdbc:postgresql://localhost/test", "test", "test")) {
            private UsuarioPoliComedor usuario =
                    usuario(credencial, password);

            @Override
            public UsuarioPoliComedor buscarPorId(String id) {
                return usuario != null
                        && usuario.getIdUsuario().equals(id)
                        ? usuario
                        : null;
            }

            @Override
            public boolean crearCuenta(
                    String nuevaCredencial,
                    String nombre,
                    String apellido,
                    String contrasenia) {
                if (buscarPorId(nuevaCredencial) != null) {
                    return false;
                }
                usuario = usuario(nuevaCredencial, contrasenia);
                return true;
            }
        };
    }

    private static UsuarioPoliComedor usuario(
            String credencial, String password) {
        UsuarioPoliComedor item = new UsuarioPoliComedor();
        item.setIdUsuario(credencial);
        item.setContrasenia(password);
        return item;
    }
}
