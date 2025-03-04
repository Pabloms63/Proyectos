import pygame
from random import randint, choice

#Clase para el jugador
class Player(pygame.sprite.Sprite):
    def __init__(self):
        super().__init__()
        self.player_walk_1 = pygame.image.load('assets/juego3_assets/player/player_walk_1.png').convert_alpha()
        self.player_walk_2 = pygame.image.load('assets/juego3_assets/player/player_walk_2.png').convert_alpha()
        self.player_walk = [self.player_walk_1, self.player_walk_2]
        self.player_index = 0
        self.player_jump = pygame.image.load('assets/juego3_assets/player/jump.png').convert_alpha()
        self.image = self.player_walk[self.player_index]
        self.rect = self.image.get_rect(midbottom=(80, 300))
        self.gravity = 0

        self.jump_sound = pygame.mixer.Sound('assets/juego3_assets/audio/jump.mp3')
        self.jump_sound.set_volume(0.2)

    #Método para controlar la entrada del jugador
    def player_input(self):
        keys = pygame.key.get_pressed()
        if keys[pygame.K_SPACE] and self.rect.bottom >= 300:
            self.gravity = -20
            self.jump_sound.play()

    #Método para aplicar la gravedad
    def apply_gravity(self):
        self.gravity += 1
        self.rect.y += self.gravity
        if self.rect.bottom >= 300:
            self.rect.bottom = 300

    #Método para la animación del jugador
    def animation_state(self):
        if self.rect.bottom < 300:
            self.image = self.player_jump
        else:
            self.player_index += 0.1
            if self.player_index >= len(self.player_walk):
                self.player_index = 0
            self.image = self.player_walk[int(self.player_index)]

    #Método para actualizar al jugador
    def update(self):
        self.player_input()
        self.apply_gravity()
        self.animation_state()

#Clase para los obstáculos
class Obstacle(pygame.sprite.Sprite):
    def __init__(self, type):
        super().__init__()

        #Cargamos las imágenes de los obstáculos
        if type == 'fly':
            fly_1 = pygame.image.load('assets/juego3_assets/fly/fly1.png').convert_alpha()
            fly_2 = pygame.image.load('assets/juego3_assets/fly/fly2.png').convert_alpha()
            self.frames = [fly_1, fly_2]
            y_pos = 210
        else:
            snail_1 = pygame.image.load('assets/juego3_assets/snail/snail1.png').convert_alpha()
            snail_2 = pygame.image.load('assets/juego3_assets/snail/snail2.png').convert_alpha()
            self.frames = [snail_1, snail_2]
            y_pos = 300

        self.animation_index = 0
        self.image = self.frames[self.animation_index]
        self.rect = self.image.get_rect(midbottom=(randint(900, 1100), y_pos))

    #Método para la animación de los obstáculos
    def animation_state(self):
        self.animation_index += 0.1
        if self.animation_index >= len(self.frames):
            self.animation_index = 0
        self.image = self.frames[int(self.animation_index)]

    #Método para actualizar los obstáculos
    def update(self):
        self.animation_state()
        self.rect.x -= 6
        self.destroy()

    #Método para eliminar los obstáculos
    def destroy(self):
        if self.rect.x <= -100:
            self.kill()

#Clase para el juego
class Juego3:
    def __init__(self):
        pygame.init()
        self.screen = pygame.display.set_mode((800, 400))
        pygame.display.set_caption("Juego 3")
        pygame.key.set_repeat(1, 50) 
        self.clock = pygame.time.Clock()
        self.font = pygame.font.Font('assets/juego3_assets/font/Pixeltype.ttf', 50)

        self.bg_music = pygame.mixer.Sound('assets/juego3_assets/audio/music.wav')
        self.bg_music.set_volume(0.2)
        self.bg_music.play(loops=-1)

        self.sky_surface = pygame.image.load('assets/juego3_assets/Sky.png').convert()
        self.ground_surface = pygame.image.load('assets/juego3_assets/ground.png').convert()

        self.player_stand = pygame.image.load('assets/juego3_assets/player/player_stand.png').convert_alpha()
        self.player_stand = pygame.transform.rotozoom(self.player_stand, 0, 2)
        self.player_stand_rect = self.player_stand.get_rect(center=(400, 200))

        self.game_name = self.font.render('MARTE', False, (111, 196, 169))
        self.game_name_rect = self.game_name.get_rect(center=(400, 80))

        self.game_message = self.font.render('Presiona el espacio para empezar', False, (111, 196, 169))
        self.game_message_rect = self.game_message.get_rect(center=(400, 330))

        self.player = pygame.sprite.GroupSingle()
        self.player.add(Player())

        self.obstacle_group = pygame.sprite.Group()

        self.obstacle_timer = pygame.USEREVENT + 1
        pygame.time.set_timer(self.obstacle_timer, 1500)

        self.game_active = False
        self.start_time = 0
        self.score = 0

    #Método para mostrar la puntuación
    def display_score(self):
        current_time = int(pygame.time.get_ticks() / 1000) - self.start_time
        score_surf = self.font.render(f'Puntos: {current_time}', False, (64, 64, 64))
        score_rect = score_surf.get_rect(center=(400, 50))
        self.screen.blit(score_surf, score_rect)
        return current_time

    #Método para detectar colisiones
    def collision_sprite(self):
        if pygame.sprite.spritecollide(self.player.sprite, self.obstacle_group, False):
            self.obstacle_group.empty()
            return False
        return True

    #Método para iniciar el juego
    def iniciar(self):
        running = True 
        while running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False  
                
                if self.game_active:
                    if event.type == self.obstacle_timer:
                        self.obstacle_group.add(Obstacle(choice(['fly', 'snail', 'snail', 'snail'])))
                else:
                    if event.type == pygame.KEYDOWN and event.key == pygame.K_SPACE:
                        self.game_active = True
                        self.start_time = int(pygame.time.get_ticks() / 1000)

            if self.game_active:
                self.screen.blit(self.sky_surface, (0, 0))
                self.screen.blit(self.ground_surface, (0, 300))
                self.score = self.display_score()

                self.player.draw(self.screen)
                self.player.update()

                self.obstacle_group.draw(self.screen)
                self.obstacle_group.update()

                self.game_active = self.collision_sprite()
            else:
                self.screen.fill((94, 129, 162))
                self.screen.blit(self.player_stand, self.player_stand_rect)

                score_message = self.font.render(f'Tu puntuacion: {self.score}', False, (111, 196, 169))
                score_message_rect = score_message.get_rect(center=(400, 330))

                self.screen.blit(self.game_name, self.game_name_rect)
                if self.score == 0:
                    self.screen.blit(self.game_message, self.game_message_rect)
                else:
                    self.screen.blit(score_message, score_message_rect)

            pygame.display.update()
            self.clock.tick(60)

        pygame.quit()  
        return self.score  # Asegurar que se devuelve la puntuación final

#Ejecutar el juego
if __name__ == "__main__":
    juego = Juego3()
    juego.iniciar()
