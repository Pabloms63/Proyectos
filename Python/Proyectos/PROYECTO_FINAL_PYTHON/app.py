import os
from tkinter import ttk
from tkinter import *
import tkinter.font as tkfont
from PIL import ImageTk, Image
from tkinter import messagebox
from datetime import datetime

from VentanaInicio_sesion import VentanaInicio
from VentanaRegistro import VentanaRegistro

import sqlite3

from Juego3.juego3 import Juego3
from Juego2.juego2 import Juego2
from Juego1.juego1 import Juego1

class FramePrincipal:
    db_path = os.path.join(os.path.dirname(__file__), 'database', 'USERS.db')

    def __init__(self, root): 
        self.ventana = root
        self.nombre_usuario = ''
        #Titulo de la ventana
        self.ventana.title("TORNEO DE VIDEOJUEGOS") 
        #Activamos la redimension de la ventana.
        self.ventana.resizable(0,0) 

        #Variable para controlar el acceso a los juegos
        self.usuario_autenticado = False  

        #Tamaño de la ventana
        self.ventana.geometry("950x650")

        #Fuente de botones
        self.custom_font = tkfont.Font(family = "Tiny5", size=14) 

        #Fuente de titulo
        self.custom_font_titulo = tkfont.Font(family = "Tiny5", size=30)  

        #Fuente de titulo
        self.custom_font_tercer = tkfont.Font(family = "Tiny5", size=10) 

        #Contenedor general
        contenedor = Frame(self.ventana, bg="#40854e")
        contenedor.pack(fill=BOTH, expand=True)

        #Contenedor con color de fondo para los botones y perfil
        self.primer_frame = Frame(contenedor, bg="#40854e")
        self.primer_frame.pack(fill=X, pady = 5) 

        #Contenedor con color de fondo para el titulo
        self.segundo_frame = Frame(contenedor, bg="#40854e") 
        self.segundo_frame.pack(fill=X, pady = 5)

        #Contenedor con color de fondo para el resto
        self.tercer_frame = Frame(contenedor, bg="#40854e") 
        self.tercer_frame.pack(fill=X, pady = 5) 

        # Contenedor para los controles de los juegos
        self.controles_frame = Frame(contenedor, bg="#40854e")
        self.controles_frame.pack(fill=X, pady=5)

        # Texto con los controles de los juegos
        controles_texto = """
        Controles de los juegos:
        - Juego 1 (ATASCO): Usa A Y D del teclado para MOVERTE a los lados. (A -> izquierda, D -> derecha)
        - Juego 2 (LABERINTO): Usa las teclas WASD para MOVERTE. (W -> arriba, A -> izquierda, S -> abajo, D -> derecha)
        - Juego 3 (EL ASTRONAUTA): Usa las teclas WASD para MOVERTE y la BARRA ESPACIADORA para SALTAR.
        """
        self.label_controles = ttk.Label(self.controles_frame, text=controles_texto, font=self.custom_font_tercer, background="#40854e")
        self.label_controles.pack(pady=(10, 0))
        
        #Botones Incio Sesión / Registro
        s = ttk.Style()
        s.configure('my.TButton', font= self.custom_font) 

        self.inicio_sesion = ttk.Button(self.primer_frame, text = 'INICIAR SESIÓN', style='my.TButton', width=25, command= self.iniciar_sesion)
        self.inicio_sesion.grid(row = 1, column = 0, columnspan = 1, padx=(35, 0), pady=(20, 5), sticky=W+E)

        self.registro = ttk.Button(self.primer_frame, text='REGISTRARSE', style='my.TButton', width=25, command= self.registrarse)
        self.registro.grid(row=1, column=1, columnspan=1, padx=(20, 20), pady=(20, 5), sticky=E)

        #Label Perfil
        self.label_perfil = ttk.Label(self.primer_frame, text = "No conectado", font = self.custom_font, background="#40854e")
        self.label_perfil.grid(row=1, column=3, columnspan=1, padx=(50, 0), pady=(20, 5), sticky=E)

        #Cargamos la imagen
        self.imagen = Image.open("D:/ProyectosPython/PROYECTO_FINAL_PYTHON/usuario.png")
        self.imagen = self.imagen.resize((75, 75)) 
        self.foto = ImageTk.PhotoImage(self.imagen)

        #Label para mostrar la imagen
        self.label_imagen = ttk.Label(self.primer_frame, image=self.foto, background="#40854e")
        self.label_imagen.grid(row=1, column=4, pady=(20, 5))  

        #Titulo
        self.etiqueta_nombre = ttk.Label(self.segundo_frame, text="TORNEO DE VIDEOJUEGOS", font= self.custom_font_titulo, background="#40854e") 
        self.etiqueta_nombre.grid(row=2, column=0, padx=(65, 0))

        #Botones para los juegos 
        self.boton_juego1 = ttk.Button(self.tercer_frame, text = "JUEGO 1", style='my.TButton', width=20, command = self.iniciar_juego1)
        self.boton_juego1.grid(row=3, column=0, columnspan=1, sticky=W, padx = (70, 0), pady = (20, 0))

        self.boton_juego2 = ttk.Button(self.tercer_frame, text = "JUEGO 2", style='my.TButton', width=20, command = self.iniciar_juego2)
        self.boton_juego2.grid(row=4, column=0, columnspan=1, sticky=W, padx = (70,0), pady = (40, 5))

        self.boton_juego3 = ttk.Button(self.tercer_frame, text = "JUEGO 3", style='my.TButton', width=20, command = self.iniciar_juego3)
        self.boton_juego3.grid(row=5, column=0, columnspan=1, sticky=W, padx = (70,0), pady = (40, 5))

        #Label Descripcion juegos
        self.label_juego1 = ttk.Label(self.tercer_frame, text = "> ATASCO\n>>> Esquiva a los coches que van pisando huevos.", font = self.custom_font_tercer, background="#40854e")
        self.label_juego1.grid(row = 3, column=1, columnspan=1, sticky=W, padx = (50, 0), pady=(15,0))
        
        self.label_juego2 = ttk.Label(self.tercer_frame, text = "> LABERINTO\n>>> Llega a la salida.", font = self.custom_font_tercer, background="#40854e")
        self.label_juego2.grid(row = 4, column=1, columnspan=1, sticky=W, padx = (50,0), pady=(30,0))
        
        self.label_juego3 = ttk.Label(self.tercer_frame, text = "> EL ASTRONAUTA\n>>>Esquiva a los mosntruos extraterrestres.", font = self.custom_font_tercer, background="#40854e")
        self.label_juego3.grid(row = 5, column=1, columnspan=1, sticky=W, padx = (50,0), pady=(30,0))

        #Creamos un estilo personalizado para las pestañas
        s = ttk.Style()
        s.configure('TNotebook.Tab', font=self.custom_font_tercer)

        #Creamos un Notebook para las pestañas
        self.notebook = ttk.Notebook(self.tercer_frame)
        self.notebook.grid(row=2, column=2, padx=(50, 0), pady=(10, 0), sticky=N)

        #Creamos los frames para cada pestaña
        self.tab1 = Frame(self.notebook, bg="white")
        self.tab2 = Frame(self.notebook, bg="white")
        self.tab3 = Frame(self.notebook, bg="white")

        #Añadimos los frames al Notebook
        self.notebook.add(self.tab1, text=" ATASCO")    
        self.notebook.add(self.tab2, text="LABERINTO")
        self.notebook.add(self.tab3, text="EL ASTRONAUTA")

        # Evento para detectar cambio de pestaña
        self.notebook.bind("<<NotebookTabChanged>>", self.on_tab_selected)

        #Frame separador
        self.ranking = Frame(self.tercer_frame, width=250, bg="white")
        self.ranking.grid(row=3, column=2, rowspan= 4, sticky=NS, padx=(50, 0))

        #Label Ranking
        self.label_ranking = ttk.Label(self.ranking, text = "RANKING", style='my.TButton', width=25)
        self.label_ranking.grid(row=3, sticky=N)

        #Lista de puntajes (Ranking)
        self.ranking_puntajes = []

        #Label donde se mostrará el ranking
        self.label_ranking_texto = ttk.Label(self.ranking, text="", font=self.custom_font_tercer, background="white")
        self.label_ranking_texto.grid(row=4, sticky=N)

    #Metoddo para actualizar el perfil del usuario
    def actualizar_perfil(self, nombre_usuario, imagen_usuario):
        """Actualizar el label del perfil con el nombre del usuario"""
        self.label_perfil.config(text=nombre_usuario)
        self.usuario_autenticado = True
        self.nombre_usuario = nombre_usuario 

        # Actualizar la imagen del perfil del usuario
        self.actualizar_imagen_usuario(nombre_usuario)

        # Actualizar el ranking del usuario
        self.actualizar_ranking_usuario(nombre_usuario)

    #Metodo para actualizar la imagen del usuario
    def actualizar_imagen_usuario(self, nombre_usuario):
        """Actualizar la imagen del perfil del usuario desde la base de datos."""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        #Buscamos la imagen del usuario
        cursor.execute("SELECT imagen FROM usuarios WHERE nombre = ?", (nombre_usuario,))
        imagen_nombre = cursor.fetchone()
        conn.close()

        #Si se encuentra la imagen, se actualiza en la interfaz de usuario
        if imagen_nombre and imagen_nombre[0]:
            img_path = os.path.join("assets/fotos_perfil", imagen_nombre[0])
            if os.path.exists(img_path):
                self.imagen = Image.open(img_path)
                self.imagen = self.imagen.resize((75, 75))
                self.foto = ImageTk.PhotoImage(self.imagen)
                self.label_imagen.config(image=self.foto)
                self.label_imagen.image = self.foto

    #Metodo para actualizar el ranking del usuario
    def actualizar_ranking_usuario(self, score = None, nombre_juego=None):
        """Actualizar el ranking en la interfaz, opcionalmente filtrando por un juego específico."""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS records ( 
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                nombre_juego TEXT,
                record INTEGER
            )
        """)

        if nombre_juego:
            # Obtener solo los récords del juego seleccionado
            cursor.execute("SELECT nombre, record FROM records WHERE nombre_juego = ? ORDER BY record DESC LIMIT 8", 
                        (nombre_juego,))

        ranking = cursor.fetchall()
        conn.close()

        if nombre_juego:
            ranking_texto = "\n".join([f"{nombre}: {record}" for nombre, record in ranking])
        else:
            ranking_texto = "\n".join([f"{juego}: {record}" for juego, record in ranking])

        self.label_ranking_texto.config(text=ranking_texto)


    def on_tab_selected(self, event):
        """Actualizar el ranking cuando se seleccione una pestaña diferente."""
        tab = self.notebook.index(self.notebook.select())  #Obtenemos el índice de la pestaña seleccionada
        
        juegos = ["ATASCO", "LABERINTO", "EL ASTRONAUTA"]  #Nombres de los juegos según el orden de pestañas

        if tab < len(juegos):  # Asegurar que el índice está dentro del rango
            self.actualizar_ranking_usuario(self.nombre_usuario, juegos[tab])  # Filtrar por el juego seleccionado

        
    #Metodo para registrar usuario    
    def registrarse(self):
        VentanaRegistro(Toplevel(self.ventana))

    #Metodo para iniciar sesion
    def iniciar_sesion(self):
        VentanaInicio(Toplevel(self.ventana), self.actualizar_perfil)
        self.actualizar_ranking_usuario(self.nombre_usuario)  # Actualizar el ranking después de iniciar sesión

    #Metodo para guardar la puntuacion
    def guardar_puntuacion(self, nombre_usuario, score, juego):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        # Creamos la tabla 'puntuaciones' si no existe
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS puntuaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                nombre_juego TEXT,
                puntuacion INTEGER,
                fecha TEXT
            )
        """)

        # Creamos la tabla 'records' si no existe
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                nombre_juego TEXT,
                record INTEGER
            )
        """)

        fecha_actual = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        # Insertamos puntaje en puntuaciones
        cursor.execute("INSERT INTO puntuaciones (nombre, nombre_juego, puntuacion, fecha) VALUES (?, ?, ?, ?)",
                       (nombre_usuario, juego, score, fecha_actual))

        # Verificamos si el usuario ya tiene un récord en este juego
        cursor.execute("SELECT record FROM records WHERE nombre = ? AND nombre_juego = ?", (nombre_usuario, juego))
        record_actual = cursor.fetchone()

        # Aseguramos que record_actual[0] no sea None antes de comparar
        if record_actual and record_actual[0] is not None:
            if score > record_actual[0]:
                cursor.execute("UPDATE records SET record = ? WHERE nombre = ? AND nombre_juego = ?",
                               (score, nombre_usuario, juego))
        else:
            # Si no hay récord previo o es None, insertar el nuevo récord
            cursor.execute("INSERT INTO records (nombre, nombre_juego, record) VALUES (?, ?, ?)",
                           (nombre_usuario, juego, score))

        conn.commit()
        conn.close()

    #Metodo para obtener las mejores puntuaciones
    def mejores_puntuaciones(self):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                nombre_juego TEXT,
                record INTEGER
            )
        """)

        #Obtenemos las mejores puntuaciones
        cursor.execute("SELECT nombre, nombre_juego, MAX(record) FROM records GROUP BY nombre, nombre_juego ORDER BY record DESC LIMIT 8")
        top_scores = cursor.fetchall()
        conn.close()

        ranking_texto = "\n".join([f"{nombre} - {juego}: {score}" for nombre, juego, score in top_scores])
        self.label_ranking_texto.config(text=ranking_texto)

    #Metodo para iniciar el juego 1
    def iniciar_juego1(self):
        if not self.usuario_autenticado:
            messagebox.showwarning("Acceso denegado", "Debes iniciar sesión para jugar.")
            return  

        self.ventana.iconify()  # Minimizar la ventana principal

        # Verificar si ya existe una instancia de juego1_ventana y cerrarla
        if hasattr(self, 'juego1_ventana') and self.juego1_ventana is not None:
            self.juego1_ventana.destroy()

        self.juego1_ventana = Juego1(self.ventana, self.actualizar_ranking_usuario)
        score = self.juego1_ventana.iniciar()

        self.ventana.deiconify()  # Restaurar la ventana principal
        self.guardar_puntuacion(self.nombre_usuario, score, "ATASCO")
        self.mejores_puntuaciones()

    #Metodo para iniciar el juego 2
    def iniciar_juego2(self):
        if not self.usuario_autenticado:
            messagebox.showwarning("Acceso denegado", "Debes iniciar sesión para jugar.")
            return  

        self.ventana.iconify()
        
        juego2_ventana = Juego2()  # Pasar la referencia si es necesario
        score = juego2_ventana.iniciar()

        self.ventana.deiconify()
        self.guardar_puntuacion(self.nombre_usuario, score, "LABERINTO")
        self.mejores_puntuaciones()

    #Metodo para iniciar el juego 3
    def iniciar_juego3(self):
        if not self.usuario_autenticado:
            messagebox.showwarning("Acceso denegado", "Debes iniciar sesión para jugar.")
            return  

        self.ventana.iconify()
        juego3 = Juego3()
        score = juego3.iniciar()  # Captura la puntuación

        self.ventana.deiconify()
        self.guardar_puntuacion(self.nombre_usuario, score, juego="EL ASTRONAUTA")
        self.mejores_puntuaciones()

#Metodo principal
if __name__ == '__main__':
    root = Tk()  #Instancia de la ventana principal
    app = FramePrincipal(root) #Se envia a la clase FramePrincipal el control sobre la ventana root
    root.mainloop()  #Comenzamos el bucle de aplicacion, es como un while True