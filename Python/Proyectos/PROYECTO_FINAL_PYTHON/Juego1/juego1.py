import pygame
from pygame.locals import *
import random
import constantes

class Juego1():
    def __init__(self, ventana, actualizar_ranking_callback=None):
        self.ventana = ventana
        self.actualizar_ranking_callback = actualizar_ranking_callback
        self.running = False  # Estado del juego

    # Función para iniciar el juego
    def iniciar(self):
        pygame.init()
        tam_pantalla = (constantes.ancho, constantes.alto)
        pantalla = pygame.display.set_mode(tam_pantalla)
        pygame.display.set_caption('ATASCO')

        gameover = False
        speed = 2
        score = 0

        # Clase para los vehículos
        class Vehiculo(pygame.sprite.Sprite):
            def __init__(self, image, x, y):
                pygame.sprite.Sprite.__init__(self)
                image_scale = 45 / image.get_rect().width
                new_width = int(image.get_rect().width * image_scale)
                new_height = int(image.get_rect().height * image_scale)
                self.image = pygame.transform.scale(image, (new_width, new_height))
                self.rect = self.image.get_rect()
                self.rect.center = [x, y]

        # Clase para el jugador
        class VehiculoProta(Vehiculo):
            def __init__(self, x, y):
                image = pygame.image.load('assets/juego1_imagenes/car.png')
                super().__init__(image, x, y)

        player_group = pygame.sprite.Group()
        vehicle_group = pygame.sprite.Group()

        player = VehiculoProta(constantes.player_x, constantes.player_y)
        player_group.add(player)

        image_filenames = ['pickup_truck.png', 'semi_trailer.png', 'taxi.png', 'van.png']
        vehicle_images = [pygame.image.load(f'assets/juego1_imagenes/{img}') for img in image_filenames]

        crash = pygame.image.load('assets/juego1_imagenes/crash.png')
        crash_rect = crash.get_rect()

        self.running = True
        while self.running:
            constantes.reloj.tick(constantes.fps)

            # Manejo de eventos
            for event in pygame.event.get():
                if event.type == QUIT:
                    self.running = False

                if event.type == KEYDOWN:
                    if event.key == K_a and player.rect.center[0] > constantes.left_lane:
                        player.rect.x -= 100
                    elif event.key == K_d and player.rect.center[0] < constantes.right_lane:
                        player.rect.x += 100

                    for vehicle in vehicle_group:
                        if pygame.sprite.collide_rect(player, vehicle):
                            gameover = True
                            if event.key == K_a:
                                player.rect.left = vehicle.rect.right
                            elif event.key == K_d:
                                player.rect.right = vehicle.rect.left
                            crash_rect.center = [player.rect.center[0], (player.rect.center[1] + vehicle.rect.center[1]) / 2]

            # Dibujamos los elementos
            pantalla.fill(constantes.verde)
            pygame.draw.rect(pantalla, constantes.gris, constantes.carretera)
            pygame.draw.rect(pantalla, constantes.amarillo, constantes.left_edge_marker)
            pygame.draw.rect(pantalla, constantes.amarillo, constantes.right_edge_marker)

            constantes.lane_marker_move_y += speed * 2
            if constantes.lane_marker_move_y >= constantes.marker_height * 2:
                constantes.lane_marker_move_y = 0

            for y in range(constantes.marker_height * -2, constantes.alto, constantes.marker_height * 2):
                pygame.draw.rect(pantalla, constantes.blanco, (constantes.left_lane + 45, y + constantes.lane_marker_move_y, constantes.marker_width, constantes.marker_height))
                pygame.draw.rect(pantalla, constantes.blanco, (constantes.center_lane + 45, y + constantes.lane_marker_move_y, constantes.marker_width, constantes.marker_height))

            player_group.draw(pantalla)

            if len(vehicle_group) < 2:
                add_vehicle = all(v.rect.top >= v.rect.height * 1.5 for v in vehicle_group)
                if add_vehicle:
                    lane = random.choice(constantes.lanes)
                    image = random.choice(vehicle_images)
                    vehicle = Vehiculo(image, lane, constantes.alto / -2)
                    vehicle_group.add(vehicle)

            for vehicle in vehicle_group:
                vehicle.rect.y += speed
                if vehicle.rect.top >= constantes.alto:
                    vehicle.kill()
                    score += 1
                    if score % 5 == 0:
                        speed += 1

            vehicle_group.draw(pantalla)

            #Mostramos los puntos
            font = pygame.font.Font(pygame.font.get_default_font(), 14)
            text = font.render(f'Puntuación: {score}', True, constantes.blanco)
            pantalla.blit(text, (50, 400))

            #Colisión con los vehículos
            if pygame.sprite.spritecollide(player, vehicle_group, True):
                gameover = True
                crash_rect.center = [player.rect.center[0], player.rect.top]

            #Mostramos la imagen de choque
            if gameover:
                pantalla.blit(crash, crash_rect)
                pygame.draw.rect(pantalla, constantes.rojo, (0, 50, constantes.alto, 100))
                text = font.render('Game over. ¿Quieres jugar de nuevo? (S o N)', True, constantes.blanco)
                pantalla.blit(text, (constantes.ancho // 2 - 100, 100))

                #Actualizar el ranking con la puntuación obtenida
                if self.actualizar_ranking_callback:
                    self.actualizar_ranking_callback(score, "ATASCO")

            pygame.display.update()

            #Aquí se espera la decisión del jugador
            while gameover:  
                for event in pygame.event.get():
                    if event.type == QUIT:
                        gameover = False
                        self.running = False
                        pygame.quit()
                        return score
                    elif event.type == KEYDOWN:
                        if event.key == K_s:  #Si presionamos 'S', reinicia el juego
                            gameover = False
                            speed = 2
                            score = 0
                            vehicle_group.empty()
                            player.rect.center = [constantes.player_x, constantes.player_y]
                        elif event.key == K_n:  #Si presionamos 'N', termina el juego
                            gameover = False
                            self.running = False
                            pygame.quit()
                            return score



