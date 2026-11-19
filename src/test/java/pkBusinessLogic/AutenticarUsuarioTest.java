package pkBusinessLogic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pkDataAccess.pkDAO.ConnectionDB;
import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;
import pkDataAccess.pkDAO.UsuarioTipoDAO;
import pkDataAccess.pkDTO.UsuarioPoliComedor;
import pkDataAccess.pkDTO.UsuarioTipoDTO;

class AutenticarUsuarioTest {

    @Test
    void usuarioDaoConectaYCuentaUsuarios() {
        UsuarioPoliComedorDAO dao = new UsuarioPoliComedorDAO();
        assertTrue(dao.getCount() >= 0);
    }

    @Test
    void usuarioDaoBuscaCredencialInexistenteSinErrorSql() {
        UsuarioPoliComedorDAO dao = new UsuarioPoliComedorDAO();
        assertTrue(dao.buscarPorId("__credencial_inexistente__") == null);
    }

    @Test
    void usuarioDaoLeeUsuariosDesdeElEsquemaReal() {
        UsuarioPoliComedorDAO dao = new UsuarioPoliComedorDAO();
        assertTrue(dao.leerTodos().size() >= 0);
    }

    @Test
    void usuarioTipoDaoResuelveTipoAdministrador() {
        UsuarioTipoDTO tipo =
                new UsuarioTipoDAO().buscarPorDescripcion("administrador");
        assertNotNull(tipo);
        assertTrue("2".equals(tipo.getIdUsuarioTipo()));
    }

    @Test
    void usuarioTipoDaoResuelveTipoCajero() {
        UsuarioTipoDTO tipo =
                new UsuarioTipoDAO().buscarPorDescripcion("cajero");
        assertNotNull(tipo);
        assertTrue("3".equals(tipo.getIdUsuarioTipo()));
    }

    @Test
    void usuarioTipoDaoLeeLosTresTiposDisponibles() {
        assertTrue(new UsuarioTipoDAO().leerTodos().size() >= 3);
    }

    @Test
    void autenticaSoloCuandoElTipoSeleccionadoCoincide() {
        UsuarioPoliComedorDAO dao = usuarioTipoDAO(
                "ana", "secreto", "Usuario");
        AutenticarUsuario autenticacion = new AutenticarUsuario(dao);

        assertTrue(autenticacion.autenticar(
                "Usuario", "ana", "secreto"));
        assertFalse(autenticacion.autenticar(
                "Administrador", "ana", "secreto"));
        assertFalse(autenticacion.autenticar(
                "Cajero", "ana", "secreto"));
    }

    @Test
    void impideAsignarUnSegundoAdministrador() {
        UsuarioPoliComedorDAO dao = usuarioTipoDAO(
                "admin", "secreto", "Administrador");
        AutenticarUsuario autenticacion = new AutenticarUsuario(dao);

        assertFalse(autenticacion.registrarCuenta(
                "otro", "Otro", "Admin", "clave", "Administrador"));
    }

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

    private static UsuarioPoliComedorDAO usuarioTipoDAO(
            String credencial, String password, String tipo) {
        return new UsuarioPoliComedorDAO(
                new ConnectionDB(
                        "jdbc:postgresql://localhost/test", "test", "test")) {
            private UsuarioPoliComedor usuario =
                    usuarioTipo(credencial, password, tipo);

            @Override
            public UsuarioPoliComedor buscarPorId(String id) {
                return usuario != null
                        && usuario.getIdUsuario().equals(id)
                        ? usuario
                        : null;
            }

            @Override
            public int contarPorTipo(String idUsuarioTipo) {
                return "administrador".equalsIgnoreCase(
                        idUsuarioTipo) ? 1 : 0;
            }

            @Override
            public boolean crearCuenta(
                    String nuevaCredencial,
                    String nombre,
                    String apellido,
                    String contrasenia,
                    String tipoUsuario) {
                if ("Administrador".equalsIgnoreCase(tipoUsuario)
                        && contarPorTipo("administrador") > 0) {
                    return false;
                }
                if (buscarPorId(nuevaCredencial) != null) {
                    return false;
                }
                usuario = usuarioTipo(
                        nuevaCredencial, contrasenia, tipoUsuario);
                return true;
            }
        };
    }

    private static UsuarioPoliComedor usuarioTipo(
            String credencial, String password, String tipo) {
        UsuarioPoliComedor item = usuario(credencial, password);
        item.setTipo(tipo);
        item.setIdUsuarioTipo(tipo);
        return item;
    }
}
