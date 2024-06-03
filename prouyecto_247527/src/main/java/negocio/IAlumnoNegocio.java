/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package negocio;

import dtos.AlumnoDTO;
import entidad.Alumno;
import java.util.List;

/**
 *
 * @author jesus
 */
public interface IAlumnoNegocio {

    List<AlumnoDTO> buscarAlumnosTabla() throws NegocioException;

    List<AlumnoDTO> convertirAlumnoTablaDTO(List<Alumno> alumnos) throws NegocioException;

    boolean eliminarAlumno(int idAlumno) throws NegocioException;

    AlumnoDTO insertarAlumno(AlumnoDTO alumnoDTO) throws NegocioException;

    AlumnoDTO obtenerAlumnoPorId(int idAlumno) throws NegocioException;

    AlumnoDTO editarAlumno(AlumnoDTO alumnoDTO) throws NegocioException;

}
