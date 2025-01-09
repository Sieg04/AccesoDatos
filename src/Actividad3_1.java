import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Actividad3_1 {
    public static void main(String[] args) {
        //Declaramos las variables necesarias para establecer la conexión a la base de datos
        String BD = "clientes"; //Nombre de la base de datos a la que nos vamos a conectar
        String HOST = "localhost"; //Servidor al que nos vamos a conectar, en este caso al local
        int PORT = 3306; //El puerto que al que se conecta la bd
        String USUARIO = "root"; //Usuario que se conectará a la bd
        String urlConnection = "jdbc:mysql://" + HOST + ":" + PORT + "/" + BD;
        //String que establece la conexión a la bd

        //Usamos un try-catch with resources para que los recursos se cierren automáticamente
        //una vez se ejecute en código dentro del try
        try (Connection conexion = DriverManager.getConnection(urlConnection, USUARIO, "")) {
            //Creamos la tabla clientes si no existe en la base de datos
            Statement st = conexion.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS CLIENTES (DNI CHAR(9) NOT NULL PRIMARY KEY, APELLIDOS VARCHAR(32) NOT NULL, CP CHAR(5))");

            //Introducimos en un array los datos que deseemos insertar en la bd
            String[] entries = {
                "('78901234X', 'NADALES', '44126')",
                "('89012345E', 'ROJAS', null)",
                "('56789012B', 'SAMPER', '29730')"
            };
            //Recorremos el array y hacemos un split de los elementos, cada posición se corresponde a una columna de la tabla
            for (String entry : entries) {
                String dni = entry.split(",")[0].replace("(", "").replace("'", "").trim();
                //Si insertamos un DNI que exista en la bd saltamos el registro
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM CLIENTES WHERE DNI = '" + dni + "'");
                rs.next();
                //Si no existe un registro con la clave introducida lo insertamos
                if (rs.getInt(1) == 0) {
                    st.executeUpdate("INSERT INTO CLIENTES (DNI, APELLIDOS, CP) VALUES " + entry);
                }
            }
            //Realizamos un update al usuario que tiene su cp en null
            st.executeUpdate("UPDATE clientes set cp = '07702' where cp is null");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}