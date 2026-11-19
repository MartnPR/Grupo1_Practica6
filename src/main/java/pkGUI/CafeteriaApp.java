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
import pkBusinessLogic.PagarEfectivo;
import pkBusinessLogic.PagarTarjeta;
import pkBusinessLogic.ReservarMesa;
import pkBusinessLogic.SeleccionarAlimento;

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
import pkBusinessLogic.GestorFactura;
import pkBusinessLogic.PagarEfectivo;

public final class CafeteriaApp {

    private CafeteriaApp() {
    }

    public static void main(String[] args) {

        if (GraphicsEnvironment.isHeadless()) {

            System.err.println(
                    "La interfaz Swing requiere un entorno gráfico."
            );

            return;
        }

        SwingUtilities.invokeLater(
                () -> new CafeteriaApp().iniciar()
        );
    }

    private void iniciar() {

        /*
         * Objetos de Business Logic compartidos
         * entre las pantallas.
         */

        SeleccionarAlimento seleccionAlimentos =
                new SeleccionarAlimento();

        AutenticarUsuario autenticacion =
                new AutenticarUsuario();

        PagarTarjeta pagoTarjeta =
                new PagarTarjeta();

        PagarEfectivo pagoEfectivo =
                new PagarEfectivo();
        
        GestorFactura gestorFactura =
                new GestorFactura();

        GestorTicket gestorTicket =
                new GestorTicket();

        ReservarMesa reservarMesa =
                new ReservarMesa();

        CalcularEstadistica calcularEstadistica =
                new CalcularEstadistica();

        /*
         * Pantallas principales.
         */

        Login login =
                new Login(
                        autenticacion
                );

        PantallaCajero cajero =
                new PantallaCajero(
                        gestorTicket
                );
        
        PantallaTicket pantallaTicket =
                new PantallaTicket(
                        gestorTicket
                );

        PantallaEstadisticas estadisticas =
                new PantallaEstadisticas(
                        calcularEstadistica
                );

        PantallaMesasDisponibles mesasDisponibles =
                new PantallaMesasDisponibles(
                        reservarMesa
                );

        /*
         * Registro de pantallas.
         */

        Map<String, PantallaBase> pantallas =
                new LinkedHashMap<>();

        pantallas.put(
                "menu",
                new MenuPrincipal(
                        new SelectorVentanas("comedor")
                )
        );

        pantallas.put(
                "comedor",
                new PantallaComedor(
                        seleccionAlimentos
                )
        );

        pantallas.put(
                "pago",
                new PantallaPago(
                        seleccionAlimentos,
                        pagoTarjeta,
                        pagoEfectivo,
                        gestorFactura,
                        gestorTicket
                )
        );

        pantallas.put(
                "mesas",
                mesasDisponibles
        );

        /*
         * IMPORTANTÍSIMO:
         * PantallaTicket y PantallaCajero reciben
         * la MISMA instancia de GestorTicket.
         */
        pantallas.put(
                "ticket",
                pantallaTicket
        );

        pantallas.put(
                "cajero",
                cajero
        );

        pantallas.put(
                "administrador",
                estadisticas
        );

        pantallas.put(
                "login",
                login
        );

        /*
         * Ventana principal.
         */

        JFrame ventana =
                new JFrame("Food4All");

        CardLayout tarjetas =
                new CardLayout();

        JPanel contenedor =
                new JPanel(tarjetas);

        boolean[] usuarioAutenticado =
                {false};

        String[] rolAutenticado =
                {null};

        for (Map.Entry<String, PantallaBase> entry
                : pantallas.entrySet()) {

            contenedor.add(
                    entry.getValue(),
                    entry.getKey()
            );

            entry.getValue().setNavegador(
                    destino -> {

                if ("salir".equals(destino)) {

                    ventana.dispose();

                } else if (
                        "ingreso-administrador"
                                .equals(destino)) {

                    usuarioAutenticado[0] = true;
                    rolAutenticado[0] =
                            "administrador";

                    tarjetas.show(
                            contenedor,
                            "administrador"
                    );

                } else if (
                        "ingreso-cajero"
                                .equals(destino)) {

                    usuarioAutenticado[0] = true;
                    rolAutenticado[0] =
                            "cajero";

                    tarjetas.show(
                            contenedor,
                            "cajero"
                    );

                } else if (
                        "ingreso-usuario"
                                .equals(destino)) {

                    usuarioAutenticado[0] = true;
                    rolAutenticado[0] =
                            "usuario";

                    mesasDisponibles
                            .setCredencialUsuario(
                                    login
                                            .getCredencialAutenticada()
                            );

                    tarjetas.show(
                            contenedor,
                            "menu"
                    );

                } else if (
                        "menu".equals(destino)) {

                    if ("administrador"
                            .equals(
                                    rolAutenticado[0])) {

                        tarjetas.show(
                                contenedor,
                                "administrador"
                        );

                    } else if ("cajero"
                            .equals(
                                    rolAutenticado[0])) {

                        tarjetas.show(
                                contenedor,
                                "cajero"
                        );

                    } else if ("usuario"
                            .equals(
                                    rolAutenticado[0])) {

                        tarjetas.show(
                                contenedor,
                                "menu"
                        );

                    } else {

                        tarjetas.show(
                                contenedor,
                                "login"
                        );
                    }

                } else if (
                        !usuarioAutenticado[0]
                        && !"login"
                                .equals(destino)) {

                    tarjetas.show(
                            contenedor,
                            "login"
                    );

                } else if (
                        "administrador"
                                .equals(destino)
                        && "administrador"
                                .equals(
                                        rolAutenticado[0])) {

                    tarjetas.show(
                            contenedor,
                            "administrador"
                    );

                } else if (
                        "cajero"
                                .equals(destino)
                        && "cajero"
                                .equals(
                                        rolAutenticado[0])) {

                    tarjetas.show(
                            contenedor,
                            "cajero"
                    );

                } else if ("usuario".equals(rolAutenticado[0])
                        && pantallas.containsKey(destino)
                        && !"administrador".equals(destino)
                        && !"cajero".equals(destino)) {

                    if ("ticket".equals(destino)) {
                        pantallaTicket.mostrarTicket();
                    }

                    tarjetas.show(
                            contenedor,
                            destino
                    );
                }
            });
        }

        ventana.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        ventana.setContentPane(
                contenedor
        );

        ventana.setMinimumSize(
                new Dimension(
                        760,
                        520
                )
        );

        ventana.setSize(
                980,
                700
        );

        ventana.setLocationByPlatform(
                true
        );

        tarjetas.show(
                contenedor,
                "login"
        );

        ventana.setVisible(
                true
        );
    }
}
