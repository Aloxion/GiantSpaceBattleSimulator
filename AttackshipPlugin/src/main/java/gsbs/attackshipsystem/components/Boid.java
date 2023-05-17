package gsbs.attackshipsystem.components;

import gsbs.common.components.Component;
import gsbs.common.entities.Entity;
import gsbs.common.math.Vector2;

public class Boid extends Component {
    public Vector2 acceleration = new Vector2();
    public Vector2 velocity = new Vector2();
    public Vector2 position = new Vector2();
    public Entity leader;

    public Boid(Entity leader) {
        this.leader = leader;
    }
}