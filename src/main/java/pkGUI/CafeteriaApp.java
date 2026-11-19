package pkGUI;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import pkBusinessLogic.AutenticarUsuario;
import pkBusinessLogic.CalcularEstadistica;
import pkBusinessLogic.GestorTicket;
import pkBusinessLogic.PagarTarjeta;
import pkBusinessLogic.ReservarMesa;
import pkBusinessLogic.SeleccionarAlimento;
import pkDataAccess.pkDAO.AlimentoDAO;
import pkDataAccess.pkDAO.MesaDAO;
import pkDataAccess.pkDAO.ReservaDAO;
import pkDataAccess.pkDAO.TicketDAO;
import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;
import pkGUI.boundary.Login;
import pkGUI.boundary.MenuPrincipal;
import pkGUI.boundary.PantallaBase;
import pkGUI.boundary.PantallaCajero;
import pkGUI.boundary.PantallaComedor;
import pkGUI.boundary.PantallaEstadisticas;
import pkGUI.boundary.PantallaMesasDisponibles;
import pkGUI.boundary.PantallaPago;
import pkGUI.boundary.PantallaTicket;
import pkGUI.boundary.SelectorVentanas;

/**
 * Punto de entrada de la aplicación gráfica Food4All.
 */
public final class CafeteriaApp {

    private CafeteriaApp() {
    }

    /**
     * Inicia la aplicación Swing.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("La interfaz Swing requiere un entorno gráfico.");
            return;
        }
        SwingUtilities.invokeLater(() -> new CafeteriaApp().iniciar());
    }

    /**
     * Construye y muestra las pantallas disponibles.
     */ 
    private void iniciar() {
        AlimentoDAO alimentoDAO = new AlimentoDAO();
        SeleccionarAlimento seleccionAlimentos =
                new SeleccionarAlimento();
        UsuarioPoliComedorDAO usuarioDAO = new UsuarioPoliComedorDAO();
        AutenticarUsuario autenticacion = new AutenticarUsuario();
        Login login = new Login(autenticacion);
        MesaDAO mesaDAO = new MesaDAO();
        Map<String, PantallaBase> pantallas = new LinkedHashMap<>();
        PantallaCajero cajero = new PantallaCajero(new GestorTicket());
        PantallaEstadisticas estadisticas =
                new PantallaEstadisticas(new CalcularEstadistica());
        pantallas.put("menu", new MenuPrincipal(
                new SelectorVentanas("comedor")));
        pantallas.put("comedor",
                new PantallaComedor(seleccionAlimentos));
        pantallas.put("pago",
                new PantallaPago(seleccionAlimentos, new PagarTarjeta()));
        pantallas.put("mesas",
                new PantallaMesasDisponibles(new ReservarMesa()));
        pantallas.put("ticket", new PantallaTicket());
        pantallas.put("cajero", cajero);
        pantallas.put("administrador", estadisticas);
        pantallas.put("login", login);

        JFrame ventana = new JFrame("Food4All");
        CardLayout tarjetas = new CardLayout();
        JPanel contenedor = new JPanel(tarjetas);
        boolean[] usuarioAutenticado = {false};
        String[] rolAutenticado = {null};

        for (Map.Entry<String, PantallaBase> entry : pantallas.entrySet()) {
            contenedor.add(entry.getValue(), entry.getKey());
            entry.getValue().setNavegador(destino -> {
                if ("salir".equals(destino)) {
                    ventana.dispose();
                } else if ("ingreso-administrador".equals(destino)) {
                    usuarioAutenticado[0] = true;
                    rolAutenticado[0] = "administrador";
                    tarjetas.show(contenedor, "administrador");
                } else if ("ingreso-cajero".equals(destino)) {
                    usuarioAutenticado[0] = true;
                    rolAutenticado[0] = "cajero";
                    tarjetas.show(contenedor, "cajero");
                } else if ("ingreso-usuario".equals(destino)) {
                    usuarioAutenticado[0] = true;
                    rolAutenticado[0] = "usuario";
                    tarjetas.show(contenedor, "menu");
                } else if ("menu".equals(destino)) {
                    if ("administrador".equals(rolAutenticado[0])) {
                        tarjetas.show(contenedor, "administrador");
                    } else if ("cajero".equals(rolAutenticado[0])) {
                        tarjetas.show(contenedor, "cajero");
                    } else if ("usuario".equals(rolAutenticado[0])) {
                        tarjetas.show(contenedor, "menu");
                    } else {
                        tarjetas.show(contenedor, "login");
                    }
                } else if (!usuarioAutenticado[0]
                        && !"login".equals(destino)) {
                    tarjetas.show(contenedor, "login");
                } else if ("administrador".equals(destino)
                        && "administrador".equals(rolAutenticado[0])) {
                    tarjetas.show(contenedor, "administrador");
                } else if ("cajero".equals(destino)
                        && "cajero".equals(rolAutenticado[0])) {
                    tarjetas.show(contenedor, "cajero");
                } else if ("usuario".equals(rolAutenticado[0])
                        && pantallas.containsKey(destino)
                        && !"administrador".equals(destino)
                        && !"cajero".equals(destino)) {
                    tarjetas.show(contenedor, destino);
                }
            });
        }

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(contenedor);
        ventana.setMinimumSize(new Dimension(760, 520));
        ventana.setSize(980, 700);
        ventana.setLocationByPlatform(true);
        tarjetas.show(contenedor, "login");
        ventana.setVisible(true);
    }
}
