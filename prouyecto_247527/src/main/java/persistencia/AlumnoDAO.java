/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Alumno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jesus
 */
public class AlumnoDAO implements IAlumnoDAO {

    private final IConexionBD conexionBD;

    public AlumnoDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<Alumno> buscarAlumnosTabla() throws PersistenciaException {
        try {
            List<Alumno> alumnosLista = null;

            Connection conexion = this.conexionBD.crearConexion();
            String codigoSQL = "SELECT idAlumno, nombres, apellidoPaterno, apellidoMaterno, eliminado, activo FROM alumnos limit 5 offset 0";
            Statement comandoSQL = conexion.createStatement();
            ResultSet resultado = comandoSQL.executeQuery(codigoSQL);
            while (resultado.next()) {
                if (alumnosLista == null) {
                    alumnosLista = new ArrayList<>();
                }
                Alumno alumno = this.convertirAEntidad(resultado);
                alumnosLista.add(alumno);
            }
            conexion.close();
            return alumnosLista;
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Ocurrió un error al leer la base de datos");
        }
    }

    @Override
    public Alumno convertirAEntidad(ResultSet resultado) throws PersistenciaException {
        try {
            int idAlumno = resultado.getInt("idAlumno");
            String nombres = resultado.getString("nombres");
            String apellidoPaterno = resultado.getString("apellidoPaterno");
            String apellidoMaterno = resultado.getString("apellidoMaterno");
            boolean eliminado = resultado.getBoolean("eliminado");
            boolean activo = resultado.getBoolean("activo");
            return new Alumno(idAlumno, nombres, apellidoPaterno, apellidoMaterno, eliminado, activo);
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Error al convertir el resultado a entidad.");
        }

    }

    @Override
    public Alumno insertar(Alumno alumno) throws PersistenciaException {
        String codigoSQL = "INSERT INTO alumnos (nombres, apellidoPaterno, apellidoMaterno, eliminado, activo) VALUES (?, ?, ?, ?, ?)";
        Connection conexion = null;
        PreparedStatement comandoSQL = null;
        ResultSet llavesGeneradas = null;

        try {
            // Crear una nueva conexión
            conexion = this.conexionBD.crearConexion();
            // Desactivar el modo de auto-commit
            conexion.setAutoCommit(false);

            // Preparar la sentencia SQL
            comandoSQL = conexion.prepareStatement(codigoSQL, Statement.RETURN_GENERATED_KEYS);
            comandoSQL.setString(1, alumno.getNombre());
            comandoSQL.setString(2, alumno.getApellidoPaterno());
            comandoSQL.setString(3, alumno.getApellidoMaterno());
            comandoSQL.setBoolean(4, alumno.isEliminado());
            comandoSQL.setBoolean(5, alumno.isActivo());

            // Ejecutar la actualización
            int filasAfectadas = comandoSQL.executeUpdate();
            if (filasAfectadas > 0) {
                // Obtener las llaves generadas
                llavesGeneradas = comandoSQL.getGeneratedKeys();
                if (llavesGeneradas.next()) {
                    int idAlumno = llavesGeneradas.getInt(1);
                    alumno.setIdAlumno(idAlumno);
                    // Confirmar la transacción
                    conexion.commit();
                    return alumno;
                }
            }
            throw new PersistenciaException("No se pudo insertar el alumno.");
        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
            if (conexion != null) {
                try {
                    // Si ocurre una excepción, revertir la transacción
                    conexion.rollback();
                } catch (SQLException rollbackEx) {
                    System.out.println(rollbackEx.getMessage());
                }
            }
            throw new PersistenciaException("Error al insertar el alumno en la base de datos.");
        } finally {
            // Cerrar recursos
            try {
                if (llavesGeneradas != null) {
                    llavesGeneradas.close();
                }
                if (comandoSQL != null) {
                    comandoSQL.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException cierreEx) {
                System.out.println(cierreEx.getMessage());
            }
        }
    }

    @Override
    public Alumno obtenerPorid(int id) throws PersistenciaException {
        String codigoSQL = "SELECT idAlumno, nombres, apellidoPaterno, apellidoMaterno, eliminado, activo FROM alumnos WHERE idAlumno = ?";
        try (Connection conexion = this.conexionBD.crearConexion(); PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL)) {

            comandoSQL.setInt(1, id);
            try (ResultSet resultado = comandoSQL.executeQuery()) {
                if (resultado.next()) {
                    return this.convertirAEntidad(resultado);
                }
                return null; // Alumno no encontrado
            }
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Error al obtener el alumno por id.");
        }
    }

    @Override
    public Alumno editar(Alumno alumno) throws PersistenciaException {
        String codigoSQL = "UPDATE alumnos SET nombres = ?, apellidoPaterno = ?, apellidoMaterno = ?, eliminado = ?, activo = ? WHERE idAlumno = ?";
        try (Connection conexion = this.conexionBD.crearConexion(); PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL)) {

            comandoSQL.setString(1, alumno.getNombre());
            comandoSQL.setString(2, alumno.getApellidoPaterno());
            comandoSQL.setString(3, alumno.getApellidoMaterno());
            comandoSQL.setBoolean(4, alumno.isEliminado());
            comandoSQL.setBoolean(5, alumno.isActivo());
            comandoSQL.setInt(6, alumno.getIdAlumno());

            int filasAfectadas = comandoSQL.executeUpdate();
            if (filasAfectadas > 0) {
                return alumno;
            }
            throw new PersistenciaException("No se pudo actualizar el alumno.");
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Error al actualizar el alumno en la base de datos.");
        }
    }

    @Override
    public boolean eliminar(int idAlumno) throws PersistenciaException {
        String codigoSQL = "UPDATE alumnos SET eliminado = true WHERE idAlumno = ?";
        try (Connection conexion = this.conexionBD.crearConexion(); PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL)) {

            comandoSQL.setInt(1, idAlumno);

            int filasAfectadas = comandoSQL.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Error al eliminar el alumno en la base de datos.");
        }
    }

}
