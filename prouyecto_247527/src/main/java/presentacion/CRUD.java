/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package presentacion;

import dtos.AlumnoDTO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import negocio.AlumnoNegocio;
import negocio.IAlumnoNegocio;
import negocio.NegocioException;
import persistencia.AlumnoDAO;
import persistencia.ConexionBD;
import persistencia.IAlumnoDAO;
import persistencia.IConexionBD;
import utilerias.JButtonCellEditor;
import utilerias.JButtonRenderer;

/**
 *
 * @author jesus
 */
public class CRUD extends javax.swing.JFrame {
    
       private int pagina = 1;
    private final int LIMITE = 2;
    private IAlumnoNegocio alumnoNegocio;

    /**
     * Creates new form CRUD
     */
    public CRUD() {
        initComponents();
        cargarConfiguracionInicialTablaAlumnos();
        
    }
    
     /**
     * Creates new form FrmCRUD
     *
     * @param alumnoNegocio
     */
    public CRUD(IAlumnoNegocio alumnoNegocio) throws NegocioException {
        initComponents();

        this.alumnoNegocio = alumnoNegocio;
    }
 private void cargarMetodosIniciales() throws NegocioException {
        this.cargarConfiguracionInicialPantalla();
        this.cargarConfiguracionInicialTablaAlumnos();
        this.cargarAlumnosEnTabla();
    }

    private void cargarConfiguracionInicialPantalla() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void cargarConfiguracionInicialTablaAlumnos() {
        ActionListener onEditarClickListener = new ActionListener() {
            final int columnaId = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                //Metodo para editar un alumno
                editar();
            }
        };
        int indiceColumnaEditar = 5;
        TableColumnModel modeloColumnas = this.tblAlumnos.getColumnModel();
        modeloColumnas.getColumn(indiceColumnaEditar)
                .setCellRenderer(new JButtonRenderer("Editar"));
        modeloColumnas.getColumn(indiceColumnaEditar)
                .setCellEditor(new JButtonCellEditor("Editar",
                        onEditarClickListener));

        ActionListener onEliminarClickListener = new ActionListener() {
            final int columnaId = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                //Metodo para eliminar un alumno
                eliminar();
            }
        };
        int indiceColumnaEliminar = 6;
        modeloColumnas = this.tblAlumnos.getColumnModel();
        modeloColumnas.getColumn(indiceColumnaEliminar)
                .setCellRenderer(new JButtonRenderer("Eliminar"));
        modeloColumnas.getColumn(indiceColumnaEliminar)
                .setCellEditor(new JButtonCellEditor("Eliminar",
                        onEliminarClickListener));
    }

    private int getIdSeleccionadoTablaAlumnos() {
        int indiceFilaSeleccionada = this.tblAlumnos.getSelectedRow();
        if (indiceFilaSeleccionada != -1) {
            DefaultTableModel modelo = (DefaultTableModel) this.tblAlumnos.getModel();
            int indiceColumnaId = 0;
            int idSocioSeleccionado = (int) modelo.getValueAt(indiceFilaSeleccionada,
                    indiceColumnaId);
            return idSocioSeleccionado;
        } else {
            return 0;
        }
    }

    private void registrarAlumno() throws NegocioException {
        try {
            // Sentencia SQL para insertar un nuevo alumno en la tabla 'alumnos'
            try ( // Establecer la conexión a la base de datos (reemplaza CADENA_CONEXION, USUARIO y CONTRASEÑA con tus valores)
                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/participantes", "root", "Bi0log1a1")) {
                // Sentencia SQL para insertar un nuevo alumno en la tabla 'alumnos'
                String sentenciaSql = "INSERT INTO alumnos (nombres, apellidoPaterno, apellidoMaterno, eliminado, activo) VALUES (?, ?, ?, ?, ?);";
                // Preparar la sentencia SQL, permitiendo obtener las claves generadas automáticamente
                PreparedStatement preparedStatement = conexion.prepareStatement(sentenciaSql, Statement.RETURN_GENERATED_KEYS);
                // Establecer los valores para los parámetros de la sentencia SQL
                preparedStatement.setString(1, txtNombre.getText());
                preparedStatement.setString(2, txtPaterni.getText());
                preparedStatement.setString(3, txtMaterno.getText());
                // Obtener el estado del CheckBox (activo o inactivo)
                boolean Activo = cbEstatus.isSelected();
                preparedStatement.setBoolean(4, false); // Siempre falso al registrar un nuevo alumno
                preparedStatement.setBoolean(5, Activo);
                // Ejecutar la sentencia SQL de inserción
                preparedStatement.executeUpdate();
                // Obtener las claves generadas automáticamente (por ejemplo, el ID del nuevo registro)
                ResultSet resultado = preparedStatement.getGeneratedKeys();
                while (resultado.next()) {
                    // Imprimir el ID generado para el nuevo registro
                    System.out.println("Alumno registrado con ID: " + resultado.getInt(1));
                }
                // Cerrar la conexión
            }
        } catch (SQLException ex) {
            // Capturar y manejar cualquier excepción SQL que ocurra
            System.out.println("Ocurrió un error: " + ex.getMessage());
        }
        cargarAlumnosEnTabla();
    }

    private void editar() {
        //Metodo para regresar el alumno seleccionado
        int id = this.getIdSeleccionadoTablaAlumnos();
    }

    private void eliminar() {
        //Metodo para regresar el alumno seleccionado
        int id = this.getIdSeleccionadoTablaAlumnos();
    }

    private void llenarTablaAlumnos(List<AlumnoDTO> alumnosLista) {
        DefaultTableModel modeloTabla = (DefaultTableModel) this.tblAlumnos.getModel();

        if (modeloTabla.getRowCount() > 0) {
            for (int i = modeloTabla.getRowCount() - 1; i > -1; i--) {
                modeloTabla.removeRow(i);
            }
        }

        if (alumnosLista != null) {
            alumnosLista.forEach(row -> {
                Object[] fila = new Object[5];
                fila[0] = row.getIdAlumno();
                fila[1] = row.getNombre();
                fila[2] = row.getApellidoPaterno();
                fila[3] = row.getApellidoMaterno();
                fila[4] = row.getEstatus();

                modeloTabla.addRow(fila);
            });
        }
    }

    private void cargarAlumnosEnTabla() throws NegocioException {
        List<AlumnoDTO> alumnos = alumnoNegocio.buscarAlumnosTabla();
        int inicio = (pagina - 1) * LIMITE;
        int fin = Math.min(inicio + LIMITE, alumnos.size());
        List<AlumnoDTO> alumnosPaginados = alumnos.subList(inicio, fin);
        // Ahora, actualiza la tabla con la lista de alumnosPaginados
        this.llenarTablaAlumnos(alumnosPaginados); // Utiliza la lista alumnosPaginados aquí
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtMaterno = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtPaterni = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbEstatus = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlumnos = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnSiguiente = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnAtras = new javax.swing.JButton();
        lblPagina = new javax.swing.JLabel();
        btnNuevoRegistro = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtMaterno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaternoActionPerformed(evt);
            }
        });
        jPanel1.add(txtMaterno, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 80, 150, -1));
        jPanel1.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 150, -1));
        jPanel1.add(txtPaterni, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 80, 150, -1));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Apellido Materno");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, -1, -1));

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Nombre:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Apellido Paterno");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, -1, -1));

        cbEstatus.setForeground(new java.awt.Color(0, 0, 0));
        cbEstatus.setText("Activo");
        cbEstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEstatusActionPerformed(evt);
            }
        });
        jPanel1.add(cbEstatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 80, -1, -1));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        tblAlumnos.setBackground(new java.awt.Color(255, 255, 255));
        tblAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Nombres", "A.Paterno", "A.Materno", "Estatus", "Editar", "Eliminar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAlumnos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 720, 340));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Administracion de Alumnos");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, -1));

        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });
        jPanel1.add(btnSiguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 510, -1, -1));

        btnGuardar.setText("Guardar");
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 70, -1, -1));

        btnAtras.setText("Atras");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });
        jPanel1.add(btnAtras, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, -1, -1));

        lblPagina.setForeground(new java.awt.Color(0, 0, 0));
        lblPagina.setText("Pagina 1");
        jPanel1.add(lblPagina, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 510, -1, -1));

        btnNuevoRegistro.setText("Nuevo Registro");
        btnNuevoRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoRegistroActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevoRegistro, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 120, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbEstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEstatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbEstatusActionPerformed

    private void txtMaternoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaternoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaternoActionPerformed

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
        // TODO add your handling code here:
        if (pagina > 1) {
            pagina--;
            lblPagina.setText("Página " + pagina); // Actualiza el texto del JLabel
             try {
                 cargarAlumnosEnTabla();
             } catch (NegocioException ex) {
                 Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }//GEN-LAST:event_btnAtrasActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        // TODO add your handling code here:
        List<AlumnoDTO> alumnos = null;
        try {
            alumnos = alumnoNegocio.buscarAlumnosTabla(); // Manejo de excepciones
        } catch (NegocioException ex) {
            Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }
        int totalPaginas = (int) Math.ceil((double) alumnos.size() / LIMITE);
        if (pagina < totalPaginas) {
            pagina++;
            try {
                cargarAlumnosEnTabla();
            } catch (NegocioException ex) {
                Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
            }
            lblPagina.setText("Página " + pagina); // Actualiza el texto del JLabel
        }
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnNuevoRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoRegistroActionPerformed
        // TODO add your handling code here:
         try {
            registrarAlumno();
        } catch (NegocioException ex) {
            Logger.getLogger(CRUD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnNuevoRegistroActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Crea una instancia de ConexionBD (o la implementación real que estés utilizando)
                IConexionBD conexionBD = new ConexionBD();
                // Crea una instancia de AlumnoDAO pasando la conexión como argumento
                IAlumnoDAO alumnoDAO = (IAlumnoDAO) new AlumnoDAO(conexionBD);
                // Crea una instancia de AlumnoNegocio pasando el DAO como argumento
                IAlumnoNegocio alumnoNegocio = (IAlumnoNegocio) new AlumnoNegocio(alumnoDAO);
                new CRUD().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevoRegistro;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JCheckBox cbEstatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPagina;
    private javax.swing.JTable tblAlumnos;
    private javax.swing.JTextField txtMaterno;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPaterni;
    // End of variables declaration//GEN-END:variables
}
