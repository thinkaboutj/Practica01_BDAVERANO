/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.prouyecto_prubea;

import negocio.IAlumnoNegocio;
import negocio.NegocioException;
import presentacion.CRUD;


/**
 *
 * @author jesus
 */
public class Prouyecto_prubea {

  

    public static void main(String[] args) {
        
        CRUD crud = new CRUD(IAlumnoNegocio alumnoNegocio);
        
        crud.setVisible(true);

    }
}
