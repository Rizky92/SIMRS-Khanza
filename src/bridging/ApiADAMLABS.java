/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bridging;

import fungsi.koneksiDB;
import fungsi.sekuel;

/**
 *
 * @author USER
 */
public class ApiADAMLABS
{
    private final String URL = koneksiDB.ADAMLABSAPIURL(),
                         KEY = koneksiDB.ADAMLABSAPIKEY(),
                         KODERS = koneksiDB.ADAMLABSAPIKODERS();
    
    private final sekuel Sequel = new sekuel();
    
    public void registrasi(String kodeRegistrasi)
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void tarikHasilLab()
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void tarikHasilLab(String kodeRegistrasi)
    {
        try {
            
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public void updateHasilLab()
    {
        //
    }
}
