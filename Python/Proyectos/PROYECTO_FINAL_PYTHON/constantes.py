import pygame

#================JUEGO 1=================

#Ventana
ancho = 500
alto = 500
tam_pantalla = (ancho, alto)
#Colores
gris = (100, 100, 100)
verde = (76, 208, 56)
rojo = (200, 0, 0)
blanco = (255, 255, 255)
amarillo = (255, 232, 0)
#Carretera
ancho_carretera = 300
marker_width = 10
marker_height = 50
#Carril
left_lane = 150
center_lane = 250
right_lane = 350
lanes = [left_lane, center_lane, right_lane]
#Carretera y bordes
carretera = (100, 0, ancho_carretera, alto)
left_edge_marker = (95, 0, marker_width, alto)
right_edge_marker = (395, 0, marker_width, alto)
lane_marker_move_y = 0
#Jugador
player_x = 250
player_y = 400
#Frame
reloj = pygame.time.Clock()
fps = 120
