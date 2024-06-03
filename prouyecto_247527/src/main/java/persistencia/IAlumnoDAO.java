/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package persistencia;

import entidad.Alumno;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author jesus
 */
public interface IAlumnoDAO {

    public List<Alumno> buscarAlumnosTabla() throws PersistenciaException;

    public Alumno convertirAEntidad(ResultSet resultado) throws PersistenciaException;

    public Alumno insertar(Alumno alumno) throws PersistenciaException;

    public Alumno obtenerPorid(int id) throws PersistenciaException;

    public Alumno editar(Alumno alumno) throws PersistenciaException;

    public boolean eliminar(int idAlumno) throws PersistenciaException;

}
