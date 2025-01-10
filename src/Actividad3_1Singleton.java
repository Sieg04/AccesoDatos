import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Actividad3_1Singleton {
    public static void main(String[] args) {
        //Declaramos las variables necesarias para establecer la conexión a la base de datos
        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
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
    public static class DatabaseConnection {
        // Instancia única de la clase
        private static DatabaseConnection instance;
        // Conexión a la base de datos
        private Connection connection;
        // URL de conexión a la base de datos
        private String url = "jdbc:mysql://localhost:3306/clientes";
        // Nombre de usuario para la base de datos
        private String username = "root";
        // Contraseña para la base de datos
        private String password = "";

        // Constructor privado para evitar la creación de instancias desde fuera de la clase
        private DatabaseConnection() throws SQLException {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establecer la conexión
                this.connection = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException ex) {
                // Lanzar una excepción si el driver no se encuentra
                throw new SQLException(ex);
            }
        }

        // Método para obtener la conexión
        public Connection getConnection() {
            return connection;
        }

        // Método estático para obtener la instancia única de la clase
        public static DatabaseConnection getInstance() throws SQLException {
            // Crear una nueva instancia si no existe
            if (instance == null) {
                instance = new DatabaseConnection();
                // Crear una nueva instancia si la conexión está cerrada
            } else if (instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }

            // Devolver la instancia única
            return instance;
        }
    }
}