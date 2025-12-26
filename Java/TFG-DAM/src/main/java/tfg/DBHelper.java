package tfg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import javax.swing.ImageIcon;

public class DBHelper {
    
    private static final String URL = "jdbc:postgresql://dpg-d0mbre6uk2gs73fhbi40-a.frankfurt-postgres.render.com/tfgtorneovideojuegos";
    private static final String USER = "tfgtorneovideojuegos_user";
    private static final String PASSWORD = "rgfyWBNsND74pyKq2OviG2RSEgNmk5vt";

    public static void inicializarBaseDatos() {
        crearTablas();
    }
    
    public static void crearTablas() {
        String[] sqls = {
        	    // Tabla de usuarios
        	    "CREATE TABLE IF NOT EXISTS usuarios (" +
        	    "id SERIAL PRIMARY KEY, " +
        	    "nombre TEXT NOT NULL UNIQUE, " +
        	    "contraseña TEXT NOT NULL, " +
        	    "correo TEXT NOT NULL UNIQUE, " +
        	    "imagen TEXT, " + 
        	    "fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

        	    // Tabla de juegos
        	    "CREATE TABLE IF NOT EXISTS juegos (" +
        	    "id SERIAL PRIMARY KEY, " +
        	    "nombre TEXT NOT NULL UNIQUE, " +
        	    "descripcion TEXT, " +
        	    "genero TEXT, " +
        	    "fechaLanzamiento DATE)",

        	    // Tabla de puntuaciones (con clave foránea a juegos por id)
        	    "CREATE TABLE IF NOT EXISTS puntuaciones (" +
        	    "id SERIAL PRIMARY KEY, " +
        	    "nombreUsuario TEXT NOT NULL, " +
        	    "juego_id INTEGER NOT NULL, " +
        	    "puntuacion INTEGER NOT NULL, " +
        	    "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        	    "FOREIGN KEY (nombreUsuario) REFERENCES usuarios(nombre) " +
        	    ", FOREIGN KEY (juego_id) REFERENCES juegos(id))",

        	    // Tabla de comentarios o feedback de usuarios
        	    "CREATE TABLE IF NOT EXISTS comentarios (" +
        	    "id SERIAL PRIMARY KEY, " +
        	    "nombreUsuario TEXT NOT NULL, " +
        	    "juego_id INTEGER NOT NULL, " +
        	    "comentario TEXT NOT NULL, " +
        	    "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        	    "FOREIGN KEY (nombreUsuario) REFERENCES usuarios(nombre), " +
        	    "FOREIGN KEY (juego_id) REFERENCES juegos(id))",

        	    // Tabla de roles
        	    "CREATE TABLE IF NOT EXISTS roles (" +
        	    "id SERIAL PRIMARY KEY, " +
        	    "nombre TEXT UNIQUE NOT NULL)",

        	    // Asociación entre usuarios y roles
        	    "CREATE TABLE IF NOT EXISTS usuario_rol (" +
        	    "id SERIAL PRIMARY KEY, " +
        	    "nombreUsuario TEXT NOT NULL, " +
        	    "rolId INTEGER NOT NULL, " +
        	    "FOREIGN KEY (nombreUsuario) REFERENCES usuarios(nombre), " +
        	    "FOREIGN KEY (rolId) REFERENCES roles(id))"
        };

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            for (String sql : sqls) {
                stmt.execute(sql);
            }
            
            stmt.execute("SELECT setval('usuario_rol_id_seq', COALESCE((SELECT MAX(id) FROM usuario_rol), 0) + 1, false)");

        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    public static boolean insertarPuntuacion(String nombreUsuario, String nombreJuego, int puntuacion) {
        String buscarJuego = "SELECT id FROM juegos WHERE nombre = ?";
        String insertarPuntuacion = "INSERT INTO puntuaciones(nombreUsuario, juego_id, puntuacion) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Buscar el ID del juego
            try (PreparedStatement psJuego = conn.prepareStatement(buscarJuego)) {
                psJuego.setString(1, nombreJuego);
                ResultSet rs = psJuego.executeQuery();

                if (rs.next()) {
                    int juegoId = rs.getInt("id");

                    // Insertar puntuación
                    try (PreparedStatement psInsert = conn.prepareStatement(insertarPuntuacion)) {
                        psInsert.setString(1, nombreUsuario);
                        psInsert.setInt(2, juegoId);
                        psInsert.setInt(3, puntuacion);
                        psInsert.executeUpdate();
                        return true;
                    }

                } else {
                    System.err.println("Juego no encontrado: " + nombreJuego);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar puntuación: " + e.getMessage());
            return false;
        }
    }

    public static ResultSet obtenerPuntuacionesPorJuegoLaberinto(String nombreJuego) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        String sql = "SELECT p.nombreUsuario, p.puntuacion FROM puntuaciones p JOIN juegos j ON p.juego_id = j.id WHERE j.nombre = ? " + 
                     "ORDER BY p.puntuacion DESC LIMIT 3";  
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nombreJuego);
        return pstmt.executeQuery();
    } 
    
    public static ResultSet obtenerPuntuacionesPorJuego(String nombreJuego) throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        String sql = "SELECT p.nombreUsuario, p.puntuacion FROM puntuaciones p JOIN juegos j ON p.juego_id = j.id WHERE j.nombre = ? " + 
                     "ORDER BY p.puntuacion DESC LIMIT 8";  
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nombreJuego);
        return pstmt.executeQuery();
    }   
    
    public static boolean insertarJuego(String nombre, String descripcion, String genero, Date fechaLanzamiento) {
        String sql = "INSERT INTO juegos(nombre, descripcion, genero, fechaLanzamiento) VALUES (?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, descripcion);
            pstmt.setString(3, genero);
            pstmt.setDate(4, fechaLanzamiento);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar juego: " + e.getMessage());
            return false;
        }
    }

    public static boolean insertarComentario(String nombreUsuario, int juegoId, String comentario) {
        String sql = "INSERT INTO comentarios(nombreUsuario, juego_id, comentario) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            pstmt.setInt(2, juegoId); 
            pstmt.setString(3, comentario);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar comentario: " + e.getMessage());
            return false;
        }
    }

    public static List<Comentario> obtenerComentariosPorJuego(int juegoId) {
        List<Comentario> comentarios = new ArrayList<>();

        String sql = "SELECT c.nombreUsuario, c.comentario, c.fecha, u.imagen " + "FROM comentarios c JOIN usuarios u ON c.nombreUsuario = u.nombre " + "WHERE c.juego_id = ? ORDER BY c.fecha ASC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, juegoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombreUsuario");
                String texto = rs.getString("comentario");
                Timestamp fecha = rs.getTimestamp("fecha");
                String fechaFormateada = fecha.toString().substring(0, 16);
                String imagenNombre = rs.getString("imagen");

                // Cargar imagen como recurso
                ImageIcon icono = cargarIconoUsuarioDesdeDB(imagenNombre);

                comentarios.add(new Comentario(nombre, fechaFormateada, texto, icono));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener comentarios: " + e.getMessage());
        }

        return comentarios;
    }

   public static ImageIcon cargarIconoUsuarioDesdeDB(String nombreArchivo) {
        String ruta = "/perfil/" + nombreArchivo + ".png";

        URL url = DBHelper.class.getResource(ruta);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            System.err.println("Imagen no encontrada para: " + ruta);
            return new ImageIcon(DBHelper.class.getResource("/perfil/usuario.png"));
        }
    }

    public static String obtenerImagenUsuario(String nombreUsuario) {
        String query = "SELECT imagen FROM usuarios WHERE nombre = ?";
        try (Connection conn = DriverManager.getConnection(DBHelper.URL, DBHelper.USER, DBHelper.PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("imagen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean esAdmin(String nombreUsuario) {
        String query = "SELECT r.nombre FROM usuario_rol ur " + "JOIN roles r ON ur.rolId = r.id " + "WHERE ur.nombreUsuario = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rol = rs.getString("nombre");
                if ("admin".equalsIgnoreCase(rol)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean esModerador(String nombreUsuario) {
        String query = "SELECT r.nombre FROM usuario_rol ur " + "JOIN roles r ON ur.rolId = r.id " + "WHERE ur.nombreUsuario = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rol = rs.getString("nombre");
                if ("moderador".equalsIgnoreCase(rol)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean eliminarComentario(String nombreUsuario, int juegoId, String comentarioTexto, String fecha) {
        String sql = "DELETE FROM comentarios WHERE nombreUsuario = ? AND juego_id = ? AND comentario = ? AND fecha::text LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            pstmt.setInt(2, juegoId);
            pstmt.setString(3, comentarioTexto);
            pstmt.setString(4, fecha + "%"); 
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<String[]> obtenerUsuarios() {
        List<String[]> usuarios = new ArrayList<>();
        
        // Consulta modificada para verificar si la columna existe
        String sql = "SELECT u.nombre, u.correo, u.imagen, r.nombre as rol, " +
                     "CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns " +
                     "WHERE table_name = 'usuarios' AND column_name = 'fecharegistro') " +
                     "THEN TO_CHAR(u.fechaRegistro, 'YYYY-MM-DD HH24:MI') " +
                     "ELSE 'No disponible' END as fechaRegistro " +
                     "FROM usuarios u " +
                     "JOIN usuario_rol ur ON u.nombre = ur.nombreUsuario " +
                     "JOIN roles r ON ur.rolId = r.id " +
                     "ORDER BY u.nombre ASC";  // Cambiado a ordenar por nombre como fallback

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                String imagen = rs.getString("imagen");
                String rol = rs.getString("rol");
                String fechaRegistro = rs.getString("fechaRegistro");
                
                usuarios.add(new String[]{nombre, correo, imagen, fechaRegistro, rol});
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }

        return usuarios;
    }
    
    public static boolean actualizarRolUsuario(String nombreUsuario, String nuevoRol) {
        String obtenerIdRol = "SELECT id FROM roles WHERE nombre = ?";
        String actualizarRol = "UPDATE usuario_rol SET rolId = ? WHERE nombreUsuario = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Obtener ID del nuevo rol
            try (PreparedStatement psIdRol = conn.prepareStatement(obtenerIdRol)) {
                psIdRol.setString(1, nuevoRol);
                ResultSet rs = psIdRol.executeQuery();
                
                if (rs.next()) {
                    int rolId = rs.getInt("id");
                    
                    // Actualizar el rol del usuario
                    try (PreparedStatement psActualizar = conn.prepareStatement(actualizarRol)) {
                        psActualizar.setInt(1, rolId);
                        psActualizar.setString(2, nombreUsuario);
                        return psActualizar.executeUpdate() > 0;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar rol: " + e.getMessage());
            return false;
        }
    }

    public static String obtenerRolUsuario(String nombreUsuario) {
        String query = "SELECT r.nombre FROM usuario_rol ur " +
                       "JOIN roles r ON ur.rolId = r.id " +
                       "WHERE ur.nombreUsuario = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "usuario";
    }

    public static boolean eliminarUsuario(String nombreUsuario) {
        String eliminarComentarios = "DELETE FROM comentarios WHERE nombreUsuario = ?";
        String eliminarPuntuaciones = "DELETE FROM puntuaciones WHERE nombreUsuario = ?";
        String eliminarSesiones = "DELETE FROM sesiones WHERE nombreUsuario = ?";
        String eliminarUsuarioRol = "DELETE FROM usuario_rol WHERE nombreUsuario = ?";
        String eliminarUsuario = "DELETE FROM usuarios WHERE nombre = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // Iniciar transacción
            
            try {
                // Eliminar registros relacionados primero
                try (PreparedStatement ps = conn.prepareStatement(eliminarComentarios)) {
                    ps.setString(1, nombreUsuario);
                    ps.executeUpdate();
                }
                
                try (PreparedStatement ps = conn.prepareStatement(eliminarPuntuaciones)) {
                    ps.setString(1, nombreUsuario);
                    ps.executeUpdate();
                }
                
                try (PreparedStatement ps = conn.prepareStatement(eliminarSesiones)) {
                    ps.setString(1, nombreUsuario);
                    ps.executeUpdate();
                }
                
                try (PreparedStatement ps = conn.prepareStatement(eliminarUsuarioRol)) {
                    ps.setString(1, nombreUsuario);
                    ps.executeUpdate();
                }
                
                // Finalmente eliminar el usuario
                try (PreparedStatement ps = conn.prepareStatement(eliminarUsuario)) {
                    ps.setString(1, nombreUsuario);
                    int result = ps.executeUpdate();
                    
                    if (result > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}


