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

            try (Connection conexion = this.conexionBD.crearConexion()) {
                String codigoSQL = "SELECT idAlumno, nombres, apellidoPaterno, apellidoMaterno, eliminado, activo FROM alumnos";
                Statement comandoSQL = conexion.createStatement();
                ResultSet resultado = comandoSQL.executeQuery(codigoSQL);
                while (resultado.next()) {
                    if (alumnosLista == null) {
                        alumnosLista = new ArrayList<>();
                    }
                    Alumno alumno = this.convertirAEntidad(resultado);
                    alumnosLista.add(alumno);
                }
            }
            return alumnosLista;
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Ocurrió un error al leer la base de datos, inténtelo de nuevo y si el error persiste comuníquese con el encargado del sistema.");
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
        try (Connection conexion = this.conexionBD.crearConexion();
             PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL, Statement.RETURN_GENERATED_KEYS)) {

            comandoSQL.setString(1, alumno.getNombre());
            comandoSQL.setString(2, alumno.getApellidoPaterno());
            comandoSQL.setString(3, alumno.getApellidoMaterno());
            comandoSQL.setBoolean(4, alumno.isEliminado());
            comandoSQL.setBoolean(5, alumno.isActivo());

            int filasAfectadas = comandoSQL.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet llavesGeneradas = comandoSQL.getGeneratedKeys()) {
                    if (llavesGeneradas.next()) {
                        int idAlumno = llavesGeneradas.getInt(1);
                        alumno.setIdAlumno(idAlumno);
                        return alumno;
                    }
                }
            }
            throw new PersistenciaException("No se pudo insertar el alumno.");
        } catch (SQLException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new PersistenciaException("Error al insertar el alumno en la base de datos.");
        }
    }

    @Override
    public Alumno obtenerPorid(int id) throws PersistenciaException {
         String codigoSQL = "SELECT idAlumno, nombres, apellidoPaterno, apellidoMaterno, eliminado, activo FROM alumnos WHERE idAlumno = ?";
        try (Connection conexion = this.conexionBD.crearConexion();
             PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL)) {

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
        try (Connection conexion = this.conexionBD.crearConexion();
             PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL)) {

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
        try (Connection conexion = this.conexionBD.crearConexion();
             PreparedStatement comandoSQL = conexion.prepareStatement(codigoSQL)) {

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
