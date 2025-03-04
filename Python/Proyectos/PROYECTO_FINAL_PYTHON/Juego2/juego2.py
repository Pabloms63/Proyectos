import os
import sys
import pygame

walls = []

class Juego2():
    @staticmethod
    def iniciar():
        #Clase del jugador
        class Jugador(object):
            def __init__(self):
                self.rect = pygame.Rect(32, 32, 16, 16)
            
            #Método para mover al jugador
            def move(self, dx, dy):
                if dx != 0:
                    self.move_single_axis(dx, 0)
                if dy != 0:
                    self.move_single_axis(0, dy)
            
            #Método para mover al jugador en un solo eje
            def move_single_axis(self, dx, dy):
                
                self.rect.x += dx
                self.rect.y += dy
                for wall in walls:
                    if self.rect.colliderect(wall.rect):
                        if dx > 0:
                            self.rect.right = wall.rect.left
                        if dx < 0:
                            self.rect.left = wall.rect.right
                        if dy > 0:
                            self.rect.bottom = wall.rect.top
                        if dy < 0:
                            self.rect.top = wall.rect.bottom

        #Clase para las paredes
        class Wall(object):
            def __init__(self, pos):
                walls.append(self)
                self.rect = pygame.Rect(pos[0], pos[1], 16, 16)

        os.environ["SDL_VIDEO_CENTERED"] = "1"
        pygame.init()

        #Configuración de la pantalla
        pygame.display.set_caption("EL LABERINTO")
        screen = pygame.display.set_mode((500, 500))
        clock = pygame.time.Clock()
        font2 = pygame.font.Font("Tiny5-Regular.ttf", 20)  

        walls = []
        player = Jugador()

        #Diseño del nivel
        level = [
            "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
            "W                  WWWWWWWWWWWWWWWWWW",
            "W         WWWWWW   WWWWWWWWWWWWWWWWWW",
            "W   WWWW       W   WWWWWWWWWWWWWWWWWW",
            "W   W        WWWW  WWWWWWWWWWWWWWWWWW",
            "W WWW  WWWW        WWWWWWWWWWWWWWWWWW",
            "W   W     W W      WWWWWWWWWWWWWWWWWW",
            "W   W     W   WWW WWWWWWWWWWWWWWWWWWW",
            "W   WWW WWW   W W  WWWWWWWWWWWWWWWWWW",
            "W     W   W   W W  WWWWWWWWWWWWWWWWWW",
            "WWW   W   WWWWW W  WWWWWWWWWWWWWWWWWW",
            "W W      WW        WWWWWWWWWWWWWWWWWW",
            "W W   WWWW   WWW   WWWWWWWWWWWWWWWWWW",
            "W     W    W   W   WWWWWWWWWWWWWWWWWW",
            "WWWWWWWWWWWW     WWWWWWWWWWWWWWWWWWWW",
            "W   WWW       WWWWWWWWWWWWWWWWWWWWWWW",
            "W   W    WWWWWWWW  WWWWWWWWWWWWWWWWWW",
            "W WWW   WWW        WWWWWWWWWWWWWWWWWW",
            "W   W     W W      WWWWWWWWWWWWWWWWWW",
            "W         W   WWW WWWWWWWWWWWWWWWWWWW",
            "W   WWW WWWWW    W WWWWWWWWWWWWWWWWWW",
            "W     W   W     WW WWWWWWWWWWWWWWWWWW",
            "WWW   W      WW WW WWWWWWWWWWWWWWWWWW",
            "W W      WW        WWWWWWWWWWWWWWWWWW",
            "W WWWWWWWWW  WWW   WWWWWWWWWWWWWWWWWW",
            "W     W       W    WWWWWWWWWWWWWWWWWW",
            "WW    WW  WWWWWWWWWWWWWWWWWWWWWWWWWWW",
            "WWW   W   WWWWW W  WWWWWWWWWWWWWWWWWW",
            "W W                WWWWWWWWWWWWWWWWWW",
            "W W   WWWWE  WWW   WWWWWWWWWWWWWWWWWW",
            "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
        ]

        x = y = 0
        for row in level:
            for col in row:
                if col == "W":
                    Wall((x, y))
                if col == "E":
                    end_rect = pygame.Rect(x, y, 16, 16)
                x += 16
            y += 16
            x = 0

        #Tiempo inicial
        countdown = 60
        start_ticks = pygame.time.get_ticks()

        running = True
        game_won = False 

        #Bucle principal
        while running:
            clock.tick(60)
            seconds = (pygame.time.get_ticks() - start_ticks) // 1000
            remaining_time = max(0, countdown - seconds)
            
            #Si el tiempo se agota, terminar el juego
            if remaining_time == 0:
                print("Tiempo agotado! Fin del juego.")
                pygame.quit()
                sys.exit()
            
            #Manejo de eventos
            for e in pygame.event.get():
                if e.type == pygame.QUIT:
                    running = False
                if e.type == pygame.KEYDOWN and e.key == pygame.K_ESCAPE:
                    running = False
            
            #Movimiento del jugador
            key = pygame.key.get_pressed()
            if key[pygame.K_a]:
                player.move(-2, 0)
            if key[pygame.K_d]:
                player.move(2, 0)
            if key[pygame.K_w]:
                player.move(0, -2)
            if key[pygame.K_s]:
                player.move(0, 2)
            
            #Si el jugador llega al punto final, terminar el juego
            if player.rect.colliderect(end_rect):
                game_won = True  # Marcar que el jugador ha ganado
                print("¡Has ganado!")
                pygame.quit()
                break
            
            screen.fill((0, 0, 0))
            for wall in walls: 
                pygame.draw.rect(screen, (255, 255, 255), wall.rect)
            pygame.draw.rect(screen, (255, 0, 0), end_rect)
            pygame.draw.rect(screen, (255, 200, 0), player.rect)
            
            #Renderizamos la cuenta atrás
            countdown_text = font2.render(f"Puntuación: {remaining_time}", True, (0, 0, 0))
            screen.blit(countdown_text, (screen.get_width() - 150, 10))
            
            pygame.display.flip()
        
        #Solo guardamos la puntuación si el jugador ha ganado
        if game_won:
            score = remaining_time
            return score  #Devolvemos la puntuación solo si el jugador ha ganado
        else:
            return None  #Si el jugador no ganó, no guardamos la puntuación

        pygame.quit()
        return score
