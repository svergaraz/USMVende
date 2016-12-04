package cl.telematica.android.usmvende.Models;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseDatosSqlite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDB";

    private String sqlString = "CREATE TABLE 'Logins' ('Usuario_actual' TEXT)";

    public BaseDatosSqlite(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATEBASE OPERATION","Databased created / opened ...");
    }

    //se ejecuta cada vez que se actualiza el número de versión de la base de datos.
    //Usualmente se utiliza cuando se agregan atributos a una tabla, o cuando se agregan tablas nuevas.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //se ejecuta la primera vez que se instancia la clase que hereda de SQLiteOpenHelper,
    // y principalmente se utiliza para crear las tablas necesarias de nuestra base de datos.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlString);//Permite ejecutar una query pasada como parámetro String
        Log.e("DATEBASE OPERATION","table created...");
    }
}
