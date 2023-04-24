package gsbs.common.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory extends Component {
    private final List<UUID> attackShips = new ArrayList<>();

    public List<UUID> getAttackShips() {
        return attackShips;
    }

    public void addAttackShipToInventory(UUID attackShipID) {
        attackShips.add(attackShipID);
    }
}
