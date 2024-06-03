/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dtos.AlumnoDTO;
import entidad.Alumno;
import java.util.ArrayList;
import java.util.List;
import persistencia.IAlumnoDAO;
import persistencia.PersistenciaException;

/**
 *
 * @author jesus
 */
public class AlumnoNegocio implements IAlumnoNegocio {

    private IAlumnoDAO alumnoDAO;

    public AlumnoNegocio(IAlumnoDAO alumnoDAO) {
        this.alumnoDAO = alumnoDAO;
    }

    @Override
    public List<AlumnoDTO> buscarAlumnosTabla() throws NegocioException {
        try {
            List<Alumno> alumnos = this.alumnoDAO.buscarAlumnosTabla();
            return this.convertirAlumnoTablaDTO(alumnos);
        } catch (PersistenciaException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new NegocioException(ex.getMessage());
        }
    }

    @Override
    public List<AlumnoDTO> convertirAlumnoTablaDTO(List<Alumno> alumnos) throws NegocioException {
        if (alumnos == null) {
            throw new NegocioException("No se pudieron obtener los alumnos");
        }

        List<AlumnoDTO> alumnosDTO = new ArrayList<>();
        for (Alumno alumno : alumnos) {
            AlumnoDTO dto = new AlumnoDTO();
            dto.setIdAlumno(alumno.getIdAlumno());
            dto.setNombre(alumno.getNombre());
            dto.setApellidoPaterno(alumno.getApellidoPaterno());
            dto.setApellidoMaterno(alumno.getApellidoMaterno());
            dto.setEstatus(alumno.isActivo() == true ? "Activo" : "Inactivo");
            alumnosDTO.add(dto);
        }
        return alumnosDTO;
    }

    // Método para eliminar un alumno
    @Override
    public boolean eliminarAlumno(int idAlumno) throws NegocioException {
        try {
            return this.alumnoDAO.eliminar(idAlumno);
        } catch (PersistenciaException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new NegocioException(ex.getMessage());
        }
    }

    // Método para insertar un nuevo alumno
    @Override
    public AlumnoDTO insertarAlumno(AlumnoDTO alumnoDTO) throws NegocioException {
        try {
            Alumno alumno = new Alumno(alumnoDTO.getNombre(), alumnoDTO.getApellidoPaterno(), alumnoDTO.getApellidoMaterno(), false, true);
            Alumno alumnoInsertado = this.alumnoDAO.insertar(alumno);
            alumnoDTO.setIdAlumno(alumnoInsertado.getIdAlumno());
            return alumnoDTO;
        } catch (PersistenciaException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new NegocioException(ex.getMessage());
        }
    }

    // Método para obtener un alumno por id
    @Override
    public AlumnoDTO obtenerAlumnoPorId(int idAlumno) throws NegocioException {
        try {
            Alumno alumno = this.alumnoDAO.obtenerPorid(idAlumno);
            if (alumno == null) {
                throw new NegocioException("Alumno no encontrado");
            }
            return new AlumnoDTO(alumno.getIdAlumno(), alumno.getNombre(), alumno.getApellidoPaterno(), alumno.getApellidoMaterno(), alumno.isActivo() ? "Activo" : "Inactivo");
        } catch (PersistenciaException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new NegocioException(ex.getMessage());
        }
    }

    // Método para editar un alumno
    @Override
    public AlumnoDTO editarAlumno(AlumnoDTO alumnoDTO) throws NegocioException {
        try {
            Alumno alumno = new Alumno(alumnoDTO.getIdAlumno(), alumnoDTO.getNombre(), alumnoDTO.getApellidoPaterno(), alumnoDTO.getApellidoMaterno(), false, alumnoDTO.getEstatus().equals("Activo"));
            Alumno alumnoEditado = this.alumnoDAO.editar(alumno);
            return new AlumnoDTO(alumnoEditado.getIdAlumno(), alumnoEditado.getNombre(), alumnoEditado.getApellidoPaterno(), alumnoEditado.getApellidoMaterno(), alumnoEditado.isActivo() ? "Activo" : "Inactivo");
        } catch (PersistenciaException ex) {
            // hacer uso de Logger
            System.out.println(ex.getMessage());
            throw new NegocioException(ex.getMessage());
        }
    }

}
