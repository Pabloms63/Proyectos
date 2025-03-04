import os
from tkinter import * 
from tkinter import ttk
from tkinter import messagebox
import tkinter.font as tkfont
import sqlite3

class VentanaInicio:
    db_path = os.path.join(os.path.dirname(__file__), 'database', 'USERS.db')

    def __init__(self, root, actualizar_perfil_callback):
        self.ventana = root
        self.actualizar_perfil_callback = actualizar_perfil_callback
        self.ventana.title("Inicio de sesión")
        self.ventana.geometry("450x350")
        self.ventana.resizable(0,0)
        self.ventana.configure(bg="#40854e")

        #Contenedores de la ventana
        contenedor = Frame(self.ventana, bg="#40854e")
        contenedor.grid(row=0, column=0, sticky='nsew', rowspan = 6)

        #Ccontenedor con color de fondo para el titulo
        self.primer_frame = Frame(contenedor, bg="#40854e")  # Cambia el color de fondo aquí
        self.primer_frame.grid(row=0, column=0, pady=5)  # Coloca el Frame en la ventana

        #Contenedor con color de fondo para el resto
        self.segundo_frame = Frame(contenedor, bg="#40854e")  # Cambia el color de fondo aquí
        self.segundo_frame.grid(row=1, rowspan = 5, column=0, pady=5) # Coloca el Frame en la ventana

        #Fuente de texto
        self.custom_font = tkfont.Font(family = "Tiny5", size=14)  # Asegúrate de que la ruta sea correcta
        #Fuente para Entry
        self.custom_font_entry = tkfont.Font(family = "Tiny5", size=13)
        #Fuente de titulo
        self.custom_font_titulo = tkfont.Font(family = "Tiny5", size=20)  # Asegúrate de que la ruta sea correcta

        #TITULO
        self.label_titulo = Label(self.primer_frame, text="INICIO DE SESIÓN", bg="#40854e", font = self.custom_font_titulo)
        self.label_titulo.grid(row = 0, column = 0, padx = 115, pady = 10)

        #Etiqueta y campo de texto para nombre
        self.label_nombre = Label(self.segundo_frame, text="Nombre: ", bg="#40854e", font = self.custom_font)
        self.label_nombre.grid(row = 1, column = 0, padx = 10, pady = 10)

        self.entry_nombre = ttk.Entry(self.segundo_frame, font=self.custom_font_entry)
        self.entry_nombre.grid(row = 1, column = 1, padx = 10, pady = 15)

        #Etiqueta y campo para la contraseña
        self.label_contrasena = Label(self.segundo_frame, text = "Contraseña: ", bg="#40854e", font = self.custom_font)
        self.label_contrasena.grid(row = 2, column = 0, padx= 10, pady=10)

        self.entry_contrasena = ttk.Entry(self.segundo_frame, font=self.custom_font_entry)
        self.entry_contrasena.grid(row = 2, column = 1, padx = 10, pady = 15)

        #Botón para inicar sesion
        s = ttk.Style()
        s.configure('my.TButton', font= self.custom_font)

        self.boton_registrar = ttk.Button(self.segundo_frame, text="INICIAR SESIÓN", style = 'my.TButton', command=self.iniciar_sesion)
        self.boton_registrar.grid(row=3, column=1, columnspan=2, pady=30)

    def iniciar_sesion(self):
        nombre = self.entry_nombre.get()
        contrasena = self.entry_contrasena.get().strip()

        existe = self.usuario_existe(nombre, contrasena)
        if not existe:
            messagebox.showwarning("Error", "Usuario o contraseña incorrectos.")
            return

        #Buscamos si el usuario existe y obtenemos la imagen
        usuario = self.obtener_usuario(nombre, contrasena)

        if usuario:
            #Obtenemos la ruta de la imagen del usuario (usuario[2] asumiendo que es el tercer campo en la base de datos)
            nombre, imagen_ruta = usuario
            print(f"Usuario: {nombre}, Imagen: {imagen_ruta}")  # Añadimos esta línea para ver qué se está obteniendo
            self.actualizar_perfil_callback(nombre, imagen_ruta)
            print("Inicio de sesión exitoso.")
            self.ventana.destroy()  # Cerramos la ventana de inicio de sesión después de iniciar sesión correctamente
        else:
            print("Usuario o contraseña incorrectos.")
    #Método para obtener el usuario
    def obtener_usuario(self, nombre, contrasena):
        """Obtiene el nombre y la ruta de la imagen del usuario"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        #Aquí se asume que la tabla de usuarios tiene un campo 'imagen_ruta'
        cursor.execute("SELECT nombre, imagen FROM usuarios WHERE nombre=? AND contrasena=?", (nombre, contrasena,))
        usuario = cursor.fetchone()

        conn.close()
        return usuario
    
    #Método para verificar si el usuario existe
    def usuario_existe(self, nombre, contrasena):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        cursor.execute("SELECT * FROM usuarios WHERE nombre=? AND contrasena=?", (nombre, contrasena,))
        existe = cursor.fetchone() is not None

        conn.close()
        return existe
        