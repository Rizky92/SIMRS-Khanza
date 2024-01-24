/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fungsi;

/**
 *
 * @author USER
 */
public class Utils
{
    public static String replaceLast(String str, String substr, String replace)
    {
        try {
            int pos = str.lastIndexOf(substr);
            
            if (pos > -1) {
                return str.substring(0, pos) + replace + str.substring(pos + substr.length());
            }
            
            return str;
        } catch (Exception e) {
            return str;
        }
    }
}
