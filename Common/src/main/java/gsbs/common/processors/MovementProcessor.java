package gsbs.common.processors;

import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.services.IProcess;


import static java.lang.Math.*;

public class MovementProcessor implements IProcess {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity : world.getEntities()) {
            var movement = entity.getComponent(Movement.class);
            var position = entity.getComponent(Position.class);
            if (position == null || movement == null){
                continue;
            }

            float x = position.getX();
            float y = position.getY();
            float radians = position.getRadians();
            float dt = gameData.getDeltaTime();

            //Confirming that there is movement
            if(movement == null){
                continue;
            }

            if (movement.isLeft()) {
                radians += movement.getRotationSpeed() * dt;
            }

            if (movement.isRight()) {
                radians -= movement.getRotationSpeed() * dt;
            }

            // accelerating
            if (movement.isUp()) {
                movement.setDx((float) (movement.getDx() + cos(radians) * movement.getAcceleration() * dt));
                movement.setDy((float) (movement.getDy() + sin(radians) * movement.getAcceleration() * dt));
            }

            // deccelerating
            float vec = (float) sqrt(movement.getDx() * movement.getDx() + movement.getDy() * movement.getDy());
            if (vec > 0) {
                movement.setDx(movement.getDx() - (movement.getDx() / vec) * movement.getDeceleration() * dt);
                movement.setDy(movement.getDy() - (movement.getDy() / vec) * movement.getDeceleration() * dt);
            }
            if (vec > movement.getMaxSpeed()) {
                movement.setDx((movement.getDx() / vec) * movement.getMaxSpeed());
                movement.setDy((movement.getDy() / vec) * movement.getMaxSpeed());
            }

            // set position
            x += movement.getDx() * dt;
            if (x > gameData.getDisplayWidth()) {
                x = 0;
            }
            else if (x < 0) {
                x = gameData.getDisplayWidth();
            }

            y += movement.getDy() * dt;
            if (y > gameData.getDisplayHeight()) {
                y = 0;
            }
            else if (y < 0) {
                y = gameData.getDisplayHeight();
            }

            position.setX(x);
            position.setY(y);
            position.setRadians(radians);
        }
    }
}
