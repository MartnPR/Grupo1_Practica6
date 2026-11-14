package pkGUI;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import pkBusinessLogic.AutenticarUsuario;
import pkBusinessLogic.GestorTicket;
import pkBusinessLogic.SeleccionarAlimento;
import pkDataAccess.pkDAO.AlimentoDAO;
import pkDataAccess.pkDTO.UsuarioPoliComedor;
import pkGUI.boundary.Login;
import pkGUI.boundary.MenuPrincipal;
import pkGUI.boundary.PantallaBase;
import pkGUI.boundary.PantallaComedor;
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
        Map<String, PantallaBase> pantallas = new LinkedHashMap<>();
        pantallas.put("menu", new MenuPrincipal(new SelectorVentanas("comedor")));
        pantallas.put("comedor",
                new PantallaComedor(new SeleccionarAlimento(alimentoDAO)));
        pantallas.put("ticket", new PantallaTicket(new GestorTicket()));
        pantallas.put("login", new Login(
                new AutenticarUsuario(), new UsuarioPoliComedor()));

        JFrame ventana = new JFrame("Food4All");
        CardLayout tarjetas = new CardLayout();
        JPanel contenedor = new JPanel(tarjetas);

        for (Map.Entry<String, PantallaBase> entry : pantallas.entrySet()) {
            contenedor.add(entry.getValue(), entry.getKey());
            entry.getValue().setNavegador(destino -> {
                if ("salir".equals(destino)) {
                    ventana.dispose();
                } else if (pantallas.containsKey(destino)) {
                    tarjetas.show(contenedor, destino);
                }
            });
        }

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(contenedor);
        ventana.setMinimumSize(new Dimension(760, 520));
        ventana.setSize(980, 700);
        ventana.setLocationByPlatform(true);
        ventana.setVisible(true);
        tarjetas.show(contenedor, "menu");
    }
}
