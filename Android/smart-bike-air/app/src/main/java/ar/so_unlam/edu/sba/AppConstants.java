package ar.so_unlam.edu.sba;

/**
 * Created by A646241 on 11/11/2017.
 */

public class AppConstants {

    // Tiempo mínimo que debe transcurrir entre la detección de un objeto cercano y el siguiente.
    public static final int  MIN_TIME_FOR_NEAR_OBJECT = 1500; // Expresado en ms.

    // Status for Handler
    public static final int RECIEVE_MESSAGE = 1;

    // VALORES ESTABLECIDOS PARA EL MENEJO DE SENSORES

    public static final int VALUE_MAX_GIROSCOPE_Z = 7;

    public static final int VALUE_MAX_GIROSCOPE_Y = 7;

    public static final int VALUE_MAX_ACCELEROMETER_Z = 10;

    public static final int VALUE_MIN_ACCELEROMETER_Z = -10;

    public static final int VALUE_MAX_ACCELEROMETER_X = 9;

    public static final int VALUE_MIN_ACCELEROMETER_X = -9;

    
    //MARCA FIN DE MENSAJE, SE ESTABLECE "#" COMO MARCA DE FIN DE MENSAJE QUE ESPERA ARDUINO
    
    public static final String END_CMD_CHAR = "#";
    
    
    // VALOR PARA SEPARACION MENSAJES DE VELOCIDAD
    
    public static final int VALUE_MSJ_VELOCITY = 700;


    // ESTADOS DE ALARMA

    public static final String  ACTIVATE_ALARM = "ACTIVATE";

    public static final String  DEACTIVATE_ALARM = "DEACTIVATE";


    // MENSAJES RECIBIDOS DE ARDUINO

    public static final String  unknown = "0";

    public static final String     startedTrip = "1" ;

    public static final String     endedTrip = "2";

    public static final String     activateAlarm = "3";

    public static final String     deactivateAlarm = "4";

    public static final String     turnAlarmOn = "5";

    public static final String     turnAlarmOff = "6";

    public static final String     velocity = "7";

    public static final String     nearObject = "8";

    public static final String     nearObjectEnabled = "9"; // Android habilitó la funcionalidad.

    public static final String     nearObjectDisabled = "10"; // Android deshabilitó la funcionalidad.

    public static final String     automaticLightEnabled = "11" ; // Android habilitó la funcionalidad.

    public static final String     automaticLightDisabled = "12";// Android deshabilitó la funcionalidadd


}
