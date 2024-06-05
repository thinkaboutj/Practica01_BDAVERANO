/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.prouyecto_prubea;

import negocio.AlumnoNegocio;
import negocio.IAlumnoNegocio;
import negocio.NegocioException;
import persistencia.AlumnoDAO;
import persistencia.ConexionBD;
import persistencia.IAlumnoDAO;
import persistencia.IConexionBD;
import presentacion.CRUD;


/**
 *
 * @author jesus
 */
public class Prouyecto_prubea {

  

    public static void main(String[] args) {
        
           IConexionBD conexionBD = new ConexionBD();
        IAlumnoDAO alumnoDAO =  new AlumnoDAO(conexionBD);
        
        IAlumnoNegocio alumnoNegocio = new AlumnoNegocio(alumnoDAO);
        
        CRUD frmcrud = new  CRUD (alumnoNegocio);
        frmcrud.show();
        
        System.out.println("Termina la ejecución");
        
 

    }
}
