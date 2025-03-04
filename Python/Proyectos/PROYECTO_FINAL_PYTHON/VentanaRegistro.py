import os
from tkinter import ttk, messagebox
from tkinter import *
import tkinter.font as tkfont
from PIL import ImageTk, Image
import sqlite3

class VentanaRegistro:
    db_path = os.path.join(os.path.dirname(__file__), 'database', 'USERS.db')

    def __init__(self, root):
        self.ventana = root
        self.ventana.title("Registro de usuario")
        self.ventana.geometry("450x570")
        self.ventana.resizable(0,0)
        self.ventana.configure(bg="#40854e")

        #Contenedores de la ventana
        contenedor = Frame(self.ventana, bg="#40854e")
        contenedor.grid(row=0, column=0, sticky='nsew', rowspan=6)

        #Contenedor con color de fondo para el titulo
        self.primer_frame = Frame(contenedor, bg="#40854e")
        self.primer_frame.grid(row=0, column=0, pady=5)

        #Contenedor con color de fondo para el resto
        self.segundo_frame = Frame(contenedor, bg="#40854e")
        self.segundo_frame.grid(row=1, rowspan=5, column=0, pady=5)

        #Fuente de texto
        self.custom_font = tkfont.Font(family="Tiny5", size=14)
        self.custom_font_entry = tkfont.Font(family="Tiny5", size=13)
        self.custom_font_titulo = tkfont.Font(family="Tiny5", size=20)

        #TITULO
        self.label_titulo = Label(self.primer_frame, text="REGISTRO DE USUARIO", bg="#40854e", font=self.custom_font_titulo)
        self.label_titulo.grid(row=0, column=0, padx=85, pady=10)

        #Etiqueta y campo de texto para nombre
        self.label_nombre = Label(self.segundo_frame, text="Nombre:", bg="#40854e", font=self.custom_font)
        self.label_nombre.grid(row=1, column=0, padx=10, pady=10)
        self.entry_nombre = ttk.Entry(self.segundo_frame, font=self.custom_font_entry)
        self.entry_nombre.grid(row=1, column=1, padx=10, pady=15)

        #Etiqueta y campo para la contraseña
        self.label_contrasena = Label(self.segundo_frame, text="Contraseña:", bg="#40854e", font=self.custom_font)
        self.label_contrasena.grid(row=2, column=0, padx=10, pady=10)
        self.entry_contrasena = ttk.Entry(self.segundo_frame, font=self.custom_font_entry)
        self.entry_contrasena.grid(row=2, column=1, padx=10, pady=15)

        #Etiqueta y campo para el correo
        self.label_correo = Label(self.segundo_frame, text="Correo:", bg="#40854e", font=self.custom_font)
        self.label_correo.grid(row=3, column=0, padx=10, pady=10)
        self.entry_correo = ttk.Entry(self.segundo_frame, font=self.custom_font_entry)
        self.entry_correo.grid(row=3, column=1, padx=10, pady=15)

        #Label para seleccionar imagen de perfil
        self.label_imagen = Label(self.segundo_frame, text="Seleccione una imagen de perfil: (opcional)", bg="#40854e")
        self.label_imagen.grid(row=4, column=0, columnspan=2, padx=10, pady=10)

        #Rutas de las imágenes predefinidas
        self.imagenes_predefinidas = [
            "assets/fotos_perfil/rey_perfil.png",
            "assets/fotos_perfil/mujer_perfil.png",
            "assets/fotos_perfil/demonio_perfil.png",
            "assets/fotos_perfil/niño_perfil.png",
            "assets/fotos_perfil/animal_perfil.png"
        ]

        #Frame para las imágenes predefinidas
        self.frame_imagenes = Frame(self.segundo_frame, bg="#40854e")
        self.frame_imagenes.grid(row=5, column=0, columnspan=2, padx=10, pady=10)

        #Botones para seleccionar imágenes predefinidas
        self.imagen_seleccionada = None
        for img_path in self.imagenes_predefinidas:
            img = Image.open(img_path)
            img = img.resize((50, 50))  #Reajustamos el tamaño de la imagen a 50x50
            img_tk = ImageTk.PhotoImage(img)
            boton_imagen = Button(self.frame_imagenes, image=img_tk, command=lambda p=img_path: self.seleccionar_imagen(p))
            boton_imagen.image = img_tk  #Mantenemos referencia a la imagen
            boton_imagen.pack(side=LEFT, padx=5, pady=5)  #Distribuimos horizontalmente con padding

        #Botón para registrar
        s = ttk.Style()
        s.configure('my.TButton', font=self.custom_font)
        self.boton_registrar = ttk.Button(self.segundo_frame, text="REGISTRAR", command=self.registrar_usuario, style='my.TButton')
        self.boton_registrar.grid(row=6, column=0, columnspan=2, padx=(40, 0), pady=30)

    #Metodo para seleccionar imagen de perfil
    def seleccionar_imagen(self, img_path):
        self.imagen_seleccionada = img_path
        img = Image.open(img_path)
        img.thumbnail((100, 100))
        self.imagen_mostrar = ImageTk.PhotoImage(img)
        self.label_imagen.config(image=self.imagen_mostrar)
        self.label_imagen.image = self.imagen_mostrar

    #Metodo para registrar usuario
    def registrar_usuario(self):
        self.crear_tabla_usuarios()  #Creamos la tabla si no existe

        nombre = self.entry_nombre.get()
        contrasena = self.entry_contrasena.get()
        correo = self.entry_correo.get()

        if not nombre or not contrasena or not correo:
            messagebox.showwarning("Ingrese los datos bien.")
            print("Todos los campos son obligatorios.")
            return

        if "@" not in correo or "." not in correo:
            messagebox.showwarning("Formato de correo incorrecto", "Por favor, ingresa un correo electrónico válido.")
            print("Por favor, ingresa un correo electrónico válido.")
            return

        if self.usuario_existe(correo):
            messagebox.showwarning("El correo ya está registrado", "Intenta con otro correo.")
            print("El correo ya está registrado.")
            return

        if len(contrasena) < 3 and len(contrasena) < 15:
            messagebox.showwarning("Formato de contraseña incorrecta no válido", "La contraseña debe tener entre 3 y 15 caracteres.")
            print("La contraseña debe tener al menos 6 caracteres.")
            return

        self.guardar_usuario_en_db(nombre, contrasena, correo)

        self.entry_nombre.delete(0, END)
        self.entry_contrasena.delete(0, END)
        self.entry_correo.delete(0, END)
        self.label_imagen.config(image='')

        print("Usuario registrado con éxito.")
        messagebox.showinfo("Registro exitoso", "Usuario registrado con éxito.")

    #Metodo para crear tabla de usuarios
    def crear_tabla_usuarios(self):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute('''CREATE TABLE IF NOT EXISTS usuarios
                        (nombre TEXT, contrasena TEXT, correo TEXT, imagen TEXT)''')
        conn.commit()
        conn.close()

#Metodo para verificar si el usuario existe
    def usuario_existe(self, correo):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM usuarios WHERE correo=?", (correo,))
        existe = cursor.fetchone() is not None
        conn.close()
        return existe

#Metodo para guardar usuario en la base de datos
    def guardar_usuario_en_db(self, nombre, contrasena, correo):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute('''CREATE TABLE IF NOT EXISTS usuarios
                        (nombre TEXT, contrasena TEXT, correo TEXT, imagen TEXT)''')

        imagen_nombre = None
        if self.imagen_seleccionada:
            imagen_nombre = os.path.basename(self.imagen_seleccionada)  # Obtener solo el nombre de la imagen

        cursor.execute("INSERT INTO usuarios (nombre, contrasena, correo, imagen) VALUES (?, ?, ?, ?)",
                       (nombre, contrasena, correo, imagen_nombre))

        conn.commit()
        conn.close()




